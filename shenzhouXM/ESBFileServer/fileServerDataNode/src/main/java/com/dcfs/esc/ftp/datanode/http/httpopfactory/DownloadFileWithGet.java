package com.dcfs.esc.ftp.datanode.http.httpopfactory;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import com.dcfs.esb.ftp.server.service.GetAuthUser;
import com.dcfs.esb.ftp.server.service.PutAuthUser;
import com.dcfs.esb.ftp.server.service.ServiceContainer;

import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.datanode.helper.HttpHelper;
import com.dcfs.esc.ftp.datanode.http.ITransferOp;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Tianyza on 2019/12/20.
 */
public class DownloadFileWithGet implements ITransferOp {
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private String userIP;
    public DownloadFileWithGet(String userIP) {
        this.userIP = userIP;
    }

    @Override
    public String doTransferFile(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        String path = httpRequest.uri();          //获取路径
        SDKRequestHead sdkRequestHead = new SDKRequestHead(path);
        sdkRequestHead.setUserIp(userIP);
        log.info("流水号{},客户端地址{}",sdkRequestHead.getRqsSrlNo(),userIP);

        sdkRequestHead.setStartTime(new Date());
        //用户校验
        boolean auth = false;
        try {
            auth = doAuthByUserPasswd(sdkRequestHead);
        } catch (FtpException e) {
            log.error("PwdAuthServiceError",e);
        }
        if (!auth) {
            sdkRequestHead.setRetCode("9999");
            sdkRequestHead.setRetMsg(FtpErrCode.AUTH_PWD_FAILED);
            sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
            return null;
        }
        //权限校验
        Boolean tranAuth = null;
        try {
            tranAuth = doTranCodeAuth(sdkRequestHead, FileMsgType.GET_AUTH);
        } catch (FtpException e) {
            log.error("下载服务权限校验失败",e);
        }
        if (!tranAuth) {
            sdkRequestHead.setRetCode("9999");
            sdkRequestHead.setRetMsg(FtpErrCode.AUTH_TRAN_CODE_FAILED);
            sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
        }
        String realFileName = HttpHelper.getRealDown(sdkRequestHead);
        File downFile = new File(realFileName);
//                    openForReadByGet(ctx,downFile);
        try {
            openForReadByGetNetty(ctx, downFile, httpRequest);
            sdkRequestHead.setFileSize(downFile.length());
            sdkRequestHead.setLastPiece(true);
            sdkRequestHead.setSucc(true);
        } catch (Exception e) {
            log.error("Get 下载失败。",e);
            sdkRequestHead.setSucc(false);
        }
        try {
            HttpHelper.saveDownloadLog(sdkRequestHead);
        } catch (Exception e) {

        }

        return null;
    }


    public boolean doTranCodeAuth(SDKRequestHead sdkRequestHead, String fileMsgFlag) throws FtpException {//NOSONAR

        String tranCode = sdkRequestHead.getTranCode();
        String sysName = "comm";
        String user = sdkRequestHead.getUid();

        ServiceContainer serviceContainer = ServiceContainer.getInstance();

        String flowNo = sdkRequestHead.getRqsSrlNo();
        boolean isAuth = false;

        if (FileMsgTypeHelper.isGetFileAuth(fileMsgFlag)) {
            //用户在下载权限用户列表中，并且下载路径在上传路径列表包含内
            List<GetAuthUser> getAuthUserList = serviceContainer.getGetAuthUsers(sysName, tranCode);
            isAuth = getAuthUserList.contains(new GetAuthUser(user));

        } else if (FileMsgTypeHelper.isPutFileAuth(fileMsgFlag)) {
            List<PutAuthUser> putAuthUserList = serviceContainer.getPutAuthUsers(sysName, tranCode);

            //用户在上传权限用户列表中
            PutAuthUser putAuthUser = null;
            for (PutAuthUser authUser : putAuthUserList) {
                if (authUser.getUname().equals(user)) {
                    putAuthUser = authUser;
                    break;
                }
            }
            if (putAuthUser != null) {
                isAuth = true;

            } else {
                isAuth = false;
            }
        }

        log.debug("#flowNo:{}#交易码权限校验是否通过?{}", flowNo, isAuth);
        return isAuth;
    }
    /**
     * 读取文件，在服务端用于处理文件的下载，在客户端用于处理文件的下载。
     *
     * @throws FtpException
     */
    public void openForReadByGetNetty(ChannelHandlerContext ctx, File realFile, FullHttpRequest request) throws Exception {
        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(realFile, "r");
        } catch (FileNotFoundException ignore) {
            sendError(ctx, NOT_FOUND, request);
            return;
        }
        long fileLength = realFile.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, realFile);
        setDateAndCacheHeaders(response, realFile);

        if (!keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        } else if (request.protocolVersion().equals(HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        response.headers().set("Content-Disposition", "attachment;filename=" + realFile.getName());

        // Write the initial line and the header.
        ctx.write(response);
        // Write the content.
        ChannelFuture sendFileFuture;
        ChannelFuture lastContentFuture;
        if (ctx.pipeline().get(SslHandler.class) == null) {
            sendFileFuture =
                    ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            // Write the end marker.
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            sendFileFuture =
                    ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
                            ctx.newProgressivePromise());
            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
            lastContentFuture = sendFileFuture;
        }

        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    log.debug(future.channel() + " Transfer progress: " + progress);
                } else {
                    log.debug(future.channel() + " Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                log.debug(future.channel() + " Transfer complete.");
            }
        });

        // Decide whether to close the connection or not.
        if (!keepAlive) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }

    }
    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response HTTP response
     * @param file     file to extract content type
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response    HTTP response
     * @param fileToCache file to extract content type
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, FullHttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        this.sendAndCleanupConnection(ctx, response, request);
    }
    /**
     * If Keep-Alive is disabled, attaches "Connection: close" header to the response
     * and closes the connection after the response being sent.
     */
    private void sendAndCleanupConnection(ChannelHandlerContext ctx, FullHttpResponse response, FullHttpRequest request) {
//        final FullHttpRequest request = this.request;
        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setContentLength(response, response.content().readableBytes());
        if (!keepAlive) {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        } else if (request.protocolVersion().equals(HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ChannelFuture flushPromise = ctx.writeAndFlush(response);

        if (!keepAlive) {
            // Close the connection as soon as the response is sent.
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }
    /**
     * 发送的返回值
     *
     * @param ctx     返回
     * @param status  状态
     */
    private void sendSdkRsp(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead,HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(sdkRequestHead.getRetMsg(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set("RqsSrlNo", sdkRequestHead.getRqsSrlNo());
        response.headers().set("retcode", sdkRequestHead.getRetCode());
        response.headers().set("retmsg", sdkRequestHead.getRetMsg());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 用户密码校验
     *
     * @param sdkRequestHead
     * @return
     * @throws FtpException
     */
    protected boolean doAuthByUserPasswd(SDKRequestHead sdkRequestHead) throws FtpException {
        log.info("PwdAuthServiceBegin");
        // 检查用户名和密码，确认用户是否合法
        UserInfoWorker userInfoWorker = UserInfoWorker.getInstance();
        boolean auth = userInfoWorker.doAuth(sdkRequestHead.getUid(), sdkRequestHead.getPasswdId());
        log.info("#flowNo:{}#用户名{}和密码{}检验:{}", sdkRequestHead.getRqsSrlNo(),
                sdkRequestHead.getUid(), sdkRequestHead.getPasswdId(), auth);

        log.info("PwdAuthServiceEnd");
        return auth;
    }
}
