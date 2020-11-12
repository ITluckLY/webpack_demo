package com.dcfs.esc.ftp.namenode.network.handler;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.chunk.ChunkConfig;
import com.dcfs.esc.ftp.comm.chunk.ChunkContentFormat;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.chunk.StreamChunk;
import com.dcfs.esc.ftp.comm.chunk.util.ChunkBytesUtil;
import com.dcfs.esc.ftp.comm.chunk.util.ChunkStreamUtil;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ExceptionDto;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.helper.ChannelHandlerContextHelper;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import com.dcfs.esc.ftp.comm.util.ByteBufTool;
import com.dcfs.esc.ftp.namenode.context.ChannelContext;
import com.dcfs.esc.ftp.namenode.executor.FilePathExecutor;
import com.dcfs.esc.ftp.namenode.helper.ChannelHelper;
import com.dcfs.esc.ftp.svr.comm.dto.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.TooLongFrameException;

import java.net.SocketAddress;
import java.util.Random;

/**
 * Created by cgmo on 2017/6/1.
 */
public class ChunkDispatchHandler extends MySimpleChannelInboundHandler<ByteBuf> implements ChannelOutboundHandler {
    private byte[] chunkBytes;
    //每个channel有各自的ChannelContext，用来处理不同的请求间的信息交互
    private final ChannelContext channelContext = new ChannelContext();
    private long nano;
    //private Date startTime
    //private Date endTime
    private FilePathExecutor filePathExecutor = new FilePathExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        nano = NanoHelper.nanos();
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#===========channelActive#{}", nano, ctx.channel().id().asLongText());
        }
        ChannelHandlerContextHelper.setNano(ctx, nano);
        String remoteAddress = ChannelHelper.getRemoteAddress(ctx);
        log.debug("nano:{}#remoteAddress:{}", nano, remoteAddress);
        channelContext.setNano(nano);
        channelContext.setUserIp(remoteAddress);
        String seq = String.valueOf(new Random().nextLong());
        channelContext.setSeq(seq);
        InitDto initDto = new InitDto(seq);
        initDto.setNano(nano);
        DtoStreamChunkHelper.writeAndFlushDto(ctx, initDto);
        ctx.fireChannelActive();
        //startTime = new Date()
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        ChunkType chunkType;
        boolean toClose = false;
        try {
            chunkBytes = ByteBufTool.bytes(byteBuf);
            ChunkContentFormat contentFormat = StreamChunk.findContentFormat(chunkBytes);
            channelContext.chunkConfig().setChunkContentFormat(contentFormat);
            chunkType = ChunkStreamUtil.findChunkType(chunkBytes);
            switch (chunkType) {
                case PutFilePathReq:
                    dealPutFilePathReq(ctx);
                    break;
                case GetFilePathReq:
                    dealGetFilePathReq(ctx);
                    break;
                case RemoveFilePathReq:
                    dealRemoveFilePathReq(ctx);
                    break;
                case End:
                    toClose = true;
                    break;
                default:
                    toClose = true;
                    log.warn("nano:{}#请求类型不匹配#{}", nano, chunkType);
                    dealChunkTypeNotMatch(ctx, chunkType);
                    break;
            }
        } catch (Exception e) {
            toClose = true;
            log.error("nano:{}#ChunkDispatch err", nano, e);
            dealException(ctx, e);
        } finally {
            //endTime = new Date()
            toClose = toClose || !channelContext.isAccepted() || channelContext.isForceClose() || StreamChunk.findEnd(chunkBytes);
            if (toClose) {
                ChannelPromise promise = ctx.newPromise();
                close(ctx, promise);
            }
        }
    }

    private void dealPutFilePathReq(ChannelHandlerContext ctx) throws FtpException {
        PutFilePathReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, PutFilePathReqDto.class);
        PutFilePathRspDto rspDto = filePathExecutor.invoke(channelContext, reqDto);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
    }

    private void dealGetFilePathReq(ChannelHandlerContext ctx) throws FtpException {
        GetFilePathReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, GetFilePathReqDto.class);
        GetFilePathRspDto rspDto = filePathExecutor.invoke(channelContext, reqDto);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
    }

    private void dealRemoveFilePathReq(ChannelHandlerContext ctx) throws FtpException {
        RemoveFilePathReqDto reqDto = ChunkBytesUtil.chunkBytesToDto(chunkBytes, RemoveFilePathReqDto.class);
        RemoveFilePathRspDto rspDto = filePathExecutor.invoke(channelContext, reqDto);
        ChunkConfig chunkConfig = channelContext.chunkConfig();
        writeAndFlushDtoBySafeNullDto(ctx, rspDto, chunkConfig);
    }

    private boolean checkAuthSeq(String authSeq) {//NOSONAR
        /*boolean equals = "12355321".equals(authSeq);//NOSONAR
        channelContext.setAccepted(equals);//NOSONAR
        return equals;*///NOSONAR
        return false;
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
        DtoStreamChunkHelper.writeAndFlushDto(ctx, dto, channelContext.chunkConfig());
    }

    private void clean() {
        log.debug("nano:{}#clean channelContext...", nano);
        channelContext.clean();
        chunkBytes = null;
        filePathExecutor = null;
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
        //分片过大异常，直接关闭channel
        if (cause instanceof TooLongFrameException) {
            log.error("nano:{}#分片过大异常", nano, cause);
            writeAndFlushDtoBySafeNullDto(ctx, new ExceptionDto(FtpErrCode.TOO_LONG_FRAME_ERR));
            ctx.close();
            return;
        }

        log.error("nano:{}#channel err", nano, cause);
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
        try {
            clean();
        } catch (Exception e) {
            log.error("nano:{}#清理channelContext err", nano, e);
        } finally {
            super.handlerRemoved(ctx);
        }
    }
}
