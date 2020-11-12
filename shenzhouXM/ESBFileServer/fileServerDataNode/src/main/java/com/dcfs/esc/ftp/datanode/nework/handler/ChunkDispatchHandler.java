package com.dcfs.esc.ftp.datanode.nework.handler;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.server.model.ClientRegisterRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BeanConverter;
import com.dcfs.esc.ftp.comm.chunk.ChunkConfig;
import com.dcfs.esc.ftp.comm.chunk.ChunkContentFormat;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.chunk.StreamChunk;
import com.dcfs.esc.ftp.comm.chunk.util.ChunkBytesUtil;
import com.dcfs.esc.ftp.comm.chunk.util.ChunkStreamUtil;
import com.dcfs.esc.ftp.comm.dto.*;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgDownloadResultReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgDownloadResultRspDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatRspDto;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import com.dcfs.esc.ftp.comm.helper.ChannelHandlerContextHelper;
import com.dcfs.esc.ftp.comm.helper.DtoSeqHelper;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import com.dcfs.esc.ftp.comm.util.ByteBufTool;
import com.dcfs.esc.ftp.datanode.constant.DatanodeGlobalCons;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.ContextBean;
import com.dcfs.esc.ftp.datanode.context.ContextBeanType;
import com.dcfs.esc.ftp.datanode.context.HeartBeatContextBean;
import com.dcfs.esc.ftp.datanode.executor.*;
import com.dcfs.esc.ftp.datanode.helper.ChannelHelper;
import com.dcfs.esc.ftp.datanode.log.RecordLogger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.TooLongFrameException;
import org.apache.commons.lang.StringUtils;

import java.net.SocketAddress;
import java.util.Date;

/**

 启动 入口
 */
public class ChunkDispatchHandler extends MySimpleChannelInboundHandler<ByteBuf> implements ChannelOutboundHandler {

    private static final AuthExecutor authContext = new AuthExecutor();
    private static final DownloadExecutor downloadExecutor = new DownloadExecutor();
    private static final UploadExecutor uploadExecutor = new UploadExecutor();
    private static final NodeListExecutor nodeListExecutor = new NodeListExecutor();
    private static final FileMsgDownloadResultExecutor downloadResultExecutor = new FileMsgDownloadResultExecutor();
    private static final StateHeartBeatExecutor stateHeartbeatExecutor = new StateHeartBeatExecutor();
    //每个channel有各自的ChannelContext，用来处理不同的请求间的信息交互
    private final ChannelContext channelContext = new ChannelContext();
    private long nano;
    private byte[] chunkBytes;
    private Date startTime;
    private Date endTime;
    /*日志已记录*/
    private boolean logRecorded;
    private ChunkType chunkType;

    /**
     *   ChannelHandlerContext  ： 上下文
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        nano = NanoHelper.nanos();
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#===========channelActive#{}", nano, ctx.channel().id().asLongText());
        }
        ChannelHandlerContextHelper.setNano(ctx, nano);
        String remoteAddress = ChannelHelper.getRemoteAddress(ctx);  //请求地址 客户端请求过来的数据
        log.info("nano:{}#remoteAddress:{}", nano, remoteAddress);
        channelContext.setNano(nano);
        channelContext.setServerIp(ChannelHelper.getLocalAddress(ctx));
        channelContext.setUserIp(remoteAddress);
        String seq = DtoSeqHelper.generateInitDtoSeq();
        channelContext.setSeq(seq);
        InitDto initDto = new InitDto(seq);
        initDto.setNano(nano);
        DtoStreamChunkHelper.writeAndFlushDto(ctx, initDto);
        ctx.fireChannelActive();
        startTime = new Date();
        CapabilityDebugHelper.init();
        CapabilityDebugHelper.markCurrTime("ChunkDispatchHandler-channelActive");
    }

    /**
     *          ChannelHandlerContext  上下文
     * @param ctx
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        ChunkType chunkType2 = null;
        boolean toClose = false;
        try {
            checkNextRequestTime();
            chunkBytes = ByteBufTool.bytes(byteBuf);
            ChunkContentFormat contentFormat = StreamChunk.findContentFormat(chunkBytes);
            channelContext.chunkConfig().setChunkContentFormat(contentFormat);
            chunkType2 = ChunkStreamUtil.findChunkType(chunkBytes);
            this.chunkType = chunkType2;
            switch (chunkType2) {
                case DownloadAuthReq:
                    channelContext.initContextBean(ContextBeanType.DOWNLOAD);
                    dealDownloadAuthReq(ctx);
                    break;
                case DownloadDataReq:
                    channelContext.initContextBean(ContextBeanType.DOWNLOAD);
                    dealDownloadDataReq(ctx);
                    break;
                case UploadAuthReq://上传操作
                    channelContext.initContextBean(ContextBeanType.UPLOAD);
                    dealUploadAuthReq(ctx);
                    break;
                case UploadDataReq:
                    //  +1
                    channelContext.initContextBean(ContextBeanType.UPLOAD);
                    dealUploadDataReq(ctx);
                    break;
                case NodeListReq:
                    channelContext.initContextBean(ContextBeanType.NODE_LIST);
                    dealNodeListReq(ctx);
                    break;
                case FileMsgDownloadResultReq:
                    channelContext.initContextBean(null);
                    dealFileMsgDownloadResultReq(ctx);
                    break;
                //ADD 20171129客户端健康检测--心跳检查 Begin
                case StateHeartbeatReq:
                    channelContext.initContextBean(ContextBeanType.HERAT_BEAT);
                    dealHeartbeatReq(ctx);
                    break;
                case StateHeartbeatRsp:
                    channelContext.initContextBean(ContextBeanType.HERAT_BEAT);
                    dealHeartbeatRsp(ctx);
                    break;
                //ADD 20171129客户端健康检测--心跳检查  END
                case End:
                    toClose = true;
                    break;
                default:
                    toClose = true;
                    log.warn("nano:{}#请求类型不匹配#{}", nano, chunkType2);
                    dealChunkTypeNotMatch(ctx, chunkType2);
                    break;
            }
        } catch (Throwable e) {//NOSONAR
            toClose = true;
            log.error("nano:{}#ChunkDispatch err", nano, e);
            dealException(ctx, e);
        } finally {
            toClose = toClose || !channelContext.isAccepted() || channelContext.isForceClose() || StreamChunk.findEnd(chunkBytes);
            if (toClose || channelContext.isBizEnd()) {
                endTime = new Date();
                logRecord(chunkType2);
            }
            if (channelContext.isForceClose()) {
                ChannelPromise promise = ctx.newPromise();
                close(ctx, promise);
            }
        }
    }

    private void dealNodeListReq(ChannelHandlerContext ctx) throws Exception {
        NodeListReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, NodeListReqDto.class);
        NodeListRspDto rspDto = nodeListExecutor.invoke(channelContext, reqDto, NodeListRspDto.class);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
    }

    private void dealFileMsgDownloadResultReq(ChannelHandlerContext ctx) throws Exception {
        FileMsgDownloadResultReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, FileMsgDownloadResultReqDto.class);
        FileMsgDownloadResultRspDto rspDto = downloadResultExecutor.invoke(channelContext, reqDto, FileMsgDownloadResultRspDto.class);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
    }

    private void dealDownloadAuthReq(ChannelHandlerContext ctx) throws Exception {
        FileDownloadAuthReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, FileDownloadAuthReqDto.class);
        FileDownloadAuthRspDto rspDto = authContext.invoke(channelContext, reqDto, FileDownloadAuthRspDto.class);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
        //channel权限设置 用户权限认证、文件存在、节点不重定向
        if (rspDto != null) channelContext.setAccepted(rspDto.isAuth() && rspDto.isFileExists() && !rspDto.isRedirect());
        //channelContext.setAccepted(channelContext.cxtBean().isAuth());//NOSONAR
    }

    private void dealDownloadDataReq(ChannelHandlerContext ctx) throws Exception {
        if (channelContext.isAccepted() || StreamChunk.findHasAuthSeq(chunkBytes)) {
            FileDownloadDataReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, FileDownloadDataReqDto.class);
            //authSeq
            if (!channelContext.isAccepted() && !checkAuthSeq(reqDto.getAuthSeq())) {//NOSONAR
                NoAuthDto noAuthDto = new NoAuthDto(FtpErrCode.AUTH_SEQ_FAILED);
                writeAndFlushDtoBySafeNullDto(ctx, noAuthDto);
                return;
            }
            FileDownloadDataRspDto rspDto = downloadExecutor.invoke(channelContext, reqDto, FileDownloadDataRspDto.class);
            ChunkConfig chunkConfig = channelContext.chunkConfig();
            writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
            channelContext.bizEndTrue(rspDto == null || rspDto.isEnd(), chunkConfig.isEnd());
        } else {
            NoAuthDto noAuthDto = new NoAuthDto(FtpErrCode.AUTH_USER_FAILED);
            writeAndFlushDtoBySafeNullDto(ctx, noAuthDto);
        }
    }

    private void dealUploadAuthReq(ChannelHandlerContext ctx) throws Exception {
        FileUploadAuthReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, FileUploadAuthReqDto.class);
        FileUploadAuthRspDto rspDto = authContext.invoke(channelContext, reqDto, FileUploadAuthRspDto.class);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
        //channel权限设置 用户权限认证、节点不重定向
        if (rspDto != null) channelContext.setAccepted(rspDto.isAuth() && !rspDto.isRedirect());
    }

    /**
     * 处理客户端健康检测--心跳
     * Add 20171129
     * @param ctx
     * @throws Exception
     */
    private void dealHeartbeatReq(ChannelHandlerContext ctx) throws Exception {
        StateHeartbeatReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, StateHeartbeatReqDto.class);
        StateHeartbeatRspDto rspDto = stateHeartbeatExecutor.invoke(channelContext, reqDto, StateHeartbeatRspDto.class);
        if (StringUtils.isBlank(reqDto.getUid())) {
             ctx.channel();
        }
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
    }
    /**
     * 处理客户端健康检测--心跳
     * Add 20171129
     * @param ctx
     * @throws Exception
     */
    private void dealHeartbeatRsp(ChannelHandlerContext ctx) throws Exception {
        StateHeartbeatReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, StateHeartbeatReqDto.class);

        log.info("nano:{}#客户端名称[{}]#客户端地址:[{}:{}]#消息类型:[{}]#客户端版本号:[{}],",
                nano, reqDto.getConfName(),reqDto.getConfIp(),reqDto.getConfPort(), reqDto.getChunkType(),
                reqDto.getUid(),reqDto.getClientVersion());
        StateHeartbeatRspDto rspDto = stateHeartbeatExecutor.invoke(channelContext, reqDto, StateHeartbeatRspDto.class);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        rspDto.setSucc(true);
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);

    }

    private void dealUploadDataReq(ChannelHandlerContext ctx) throws Exception {
        if (channelContext.isAccepted() || StreamChunk.findHasAuthSeq(chunkBytes)) {
            FileUploadDataReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, FileUploadDataReqDto.class);
            //authSeq
            if (!channelContext.isAccepted() && !checkAuthSeq(reqDto.getAuthSeq())) {//NOSONAR
                NoAuthDto noAuthDto = new NoAuthDto(FtpErrCode.AUTH_SEQ_FAILED);
                writeAndFlushDtoBySafeNullDto(ctx, noAuthDto);
                return;
            }
            CapabilityDebugHelper.markCurrTime("uploadExecutor.invoke-before");
            FileUploadDataRspDto rspDto = uploadExecutor.invoke(channelContext, reqDto, FileUploadDataRspDto.class);
            CapabilityDebugHelper.markCurrTime("uploadExecutor.invoke-after");
            ChunkConfig chunkConfig = channelContext.chunkConfig();
            writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
            channelContext.bizEndTrue(rspDto == null || rspDto.isEnd(), chunkConfig.isEnd());
        } else {
            NoAuthDto noAuthDto = new NoAuthDto(FtpErrCode.AUTH_USER_FAILED);
            writeAndFlushDtoBySafeNullDto(ctx, noAuthDto);
        }
    }

    private void logRecord(ChunkType chunkType) {
        if (logRecorded) return;
        logRecorded = true;
        if (endTime == null) endTime = new Date();
        try {
            RecordLogger recordLogger = new RecordLogger();
            recordLogger.logRecord(chunkType, channelContext, startTime, endTime);
        } catch (Throwable e) {//NOSONAR
            log.error("nano:{}#logRecord err", nano, e);
        }
    }

    private boolean checkAuthSeq(String authSeq) {//NOSONAR
        /*boolean equals = "123321".equals(authSeq);//NOSONAR
        channelContext.setAccepted(equals);//NOSONAR
        return equals;*///NOSONAR
        return false;
    }

    private void checkNextRequestTime() {
        long afterTime = channelContext.getNextRequestAfterTime();
        if (afterTime > 0) {
            channelContext.setNextRequestAfterTime(0);
            long currTime = System.currentTimeMillis();
            if (afterTime - currTime > 0) {
                if (log.isTraceEnabled()) {
                    log.trace("nano:{}#checkNextRequestTime#afterTime:{}, currTime:{}", nano, afterTime, currTime);
                }
                throw new NestedRuntimeException("访问时间过早");
            }
        }
    }

    private void writeAndFlushDtoBySafeNullDto(ChannelHandlerContext ctx, BaseDto dto, ChunkConfig chunkConfig) {
        if (dto == null) {
            log.error("nano:{}#syserr#dto is null", nano);
            dto = new ExceptionDto(FtpErrCode.FAIL, "syserr#dto is null");//NOSONAR
        }
        dto.setNano(nano);
        //to-do
        chunkConfig.setLastChunk(dto.isLastChunk() || chunkConfig.isLastChunk());
        chunkConfig.setEnd(dto.isEnd() || chunkConfig.isEnd());
        DtoStreamChunkHelper.writeAndFlushDto(ctx, dto, chunkConfig);
    }

    private void writeAndFlushDtoBySafeNullDto(ChannelHandlerContext ctx, BaseDto dto) {
        writeAndFlushDtoBySafeNullDto(ctx, dto, channelContext.chunkConfig());
    }

    private void dealChunkTypeNotMatch(ChannelHandlerContext ctx, ChunkType chunkType) {
        ExceptionDto dto = new ExceptionDto();
        dto.setErrCode(FtpErrCode.CHUNK_TYPE_DONT_MACTH);
        dto.setErrMsg("请求类型不匹配#" + chunkType);
        dto.setNano(nano);
        DtoStreamChunkHelper.writeAndFlushDto(ctx, dto, channelContext.chunkConfig());
    }

    private void dealException(ChannelHandlerContext ctx, Throwable e) {
        String errmsg = e.getMessage();
        if (errmsg == null) errmsg = "系统错误-未知错误";
        ExceptionDto dto = new ExceptionDto();
        dto.setErrCode(FtpErrCode.getErrCode(e));
        dto.setErrMsg(errmsg);
        dto.setNano(nano);
        ContextBean cxtBean = channelContext.cxtBean();
        cxtBean.setErrCode(dto.getErrCode());
        cxtBean.setErrMsg(errmsg);
        DtoStreamChunkHelper.writeAndFlushDto(ctx, dto, channelContext.chunkConfig());
    }

    private void clean() {
        log.debug("nano:{}#clean channelContext...", nano);
        logRecord(chunkType);
        channelContext.clean(this);

        chunkBytes = null;

        CapabilityDebugHelper.printAndClean(nano);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#===========channelClose#{}", nano, ctx.channel().id().asLongText());
        }
        try {
            clean();
        } catch (Exception e) {
            log.error("nano:{}#清理channelContext err", nano, e);//NOSONAR
        } finally {
            ctx.close(promise);
        }
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ContextBean cxtBean = channelContext.cxtBean();
        //分片过大异常，直接关闭channel
        if (cause instanceof TooLongFrameException) {
            log.error("nano:{}#分片过大异常", nano, cause);
            cxtBean.errCode(FtpErrCode.TOO_LONG_FRAME_ERR);
            writeAndFlushDtoBySafeNullDto(ctx, new ExceptionDto(FtpErrCode.TOO_LONG_FRAME_ERR));
            ctx.close();
            return;
        }

        log.error("nano:{}#channel err", nano, cause);
        if (cxtBean != null) {
            cxtBean.setErrCode(FtpErrCode.getErrCode(cause));
            cxtBean.setErrMsg(cause.getMessage());
        }
        //add 20180402 断线注销
        log.error(" Type for {} client cancellation", chunkType);
        if (cxtBean instanceof HeartBeatContextBean) {

            HeartBeatContextBean heartBeatContextBean = (HeartBeatContextBean) cxtBean;
            heartBeatContextBean.setStatus(DatanodeGlobalCons.CLIENT_STATUS_STOPPED);
            //更新客户端状态表
            if (endTime == null) endTime = new Date();
            logClientUnregister(heartBeatContextBean, startTime, endTime);
        }
        try {
            clean();
        } catch (Exception e) {
            log.error("nano:{}#清理channelContext err", nano, e);
        } finally {
            super.exceptionCaught(ctx, cause);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#===========handlerRemoved#{}", nano, ctx.channel().id().asLongText());
        }
        ContextBean cxtBean = channelContext.cxtBean();
        //add 20180402 断线注销
        log.error(" Type for {} client cancellation", chunkType);
        if (cxtBean instanceof HeartBeatContextBean) {

            HeartBeatContextBean heartBeatContextBean = (HeartBeatContextBean) cxtBean;
            heartBeatContextBean.setStatus(DatanodeGlobalCons.CLIENT_STATUS_STOPPED);
            //更新客户端状态表
            if (endTime == null) endTime = new Date();
            logClientUnregister(heartBeatContextBean, startTime, endTime);
        }
        try {
            clean();
        } catch (Exception e) {
            log.error("nano:{}#清理channelContext err", nano, e);
        } finally {
            super.handlerRemoved(ctx);
        }
    }
    /**
     * 发送客户端下线通知。
     *
     * @param cxtBean
     * @param startTime
     * @param endTime
     */
    private void logClientUnregister(HeartBeatContextBean cxtBean, Date startTime, Date endTime) {
        ClientRegisterRecord record = BeanConverter.strictConvertTo(cxtBean, ClientRegisterRecord.class);
        record.setUid(cxtBean.getUid());
        record.setNodeIp(cxtBean.getUserIp());
        record.setNodePort(cxtBean.getNodePort());
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        EsbFileService.getInstance().logClientUnregister(record);
    }
}
