package com.dcfs.esc.ftp.datanode.helper;

import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.model.FileDownloadRecord;
import com.dcfs.esb.ftp.server.model.FileUploadRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Created by mocg on 2017/6/7.
 */
public class HttpHelper {
    private HttpHelper() {

    }

    public static String getRealUpPath(SDKRequestHead sdkRequestHead) {

        String fileRootPath = FtpConfig.getInstance().getFileRootPath();
        String serverPath = FileUtil.concatFilePath("/", sdkRequestHead.getTranCode(),
                sdkRequestHead.getUid(),
                sdkRequestHead.getRqsSrlNo());
        sdkRequestHead.setServFileName(serverPath);
        return FileUtil.concatFilePath(fileRootPath, serverPath);

    }
    public static String getRealServUpPath(SDKRequestHead sdkRequestHead) {

        String fileRootPath = FtpConfig.getInstance().getFileRootPath();
        String serverPath = FileUtil.concatFilePath("/", sdkRequestHead.getTranCode(),
                sdkRequestHead.getUid(),
                sdkRequestHead.getFileName());
        sdkRequestHead.setServFileName(serverPath);
        return FileUtil.concatFilePath(fileRootPath, serverPath);

    }
    public static String getRealServPath(SDKRequestHead sdkRequestHead) {


        String serverPath = FileUtil.concatFilePath("/", sdkRequestHead.getTranCode(),
                sdkRequestHead.getUid(),
                sdkRequestHead.getFileName());
        sdkRequestHead.setServFileName(serverPath);
        return serverPath;

    }

    public static String getRealDownPath(SDKRequestHead sdkRequestHead) {

        String fileRootPath = FtpConfig.getInstance().getFileRootPath();
        String serverPath = FileUtil.concatFilePath("/", sdkRequestHead.getTranCode(),
                sdkRequestHead.getUid(),
                sdkRequestHead.getFileName());
        sdkRequestHead.setServFileName(serverPath);
        return FileUtil.concatFilePath(fileRootPath, serverPath);

    }

    /**
     * 下载参数里获取路径
     *
     * @param sdkRequestHead
     * @return
     */
    public static String getRealDown(SDKRequestHead sdkRequestHead) {

        String fileRootPath = FtpConfig.getInstance().getFileRootPath();

        String serverPath = sdkRequestHead.getFileName();
        sdkRequestHead.setServFileName(serverPath);
        return FileUtil.concatFilePath(fileRootPath, serverPath);

    }

    /**
     * 发送的返回值
     *
     * @param ctx     返回
     * @param context 消息
     * @param status  状态
     */
    public static void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        response.headers().set(ACCESS_CONTROL_ALLOW_HEADERS,"*");//允许headers自定义
        response.headers().set(ACCESS_CONTROL_ALLOW_METHODS,"*");
        response.headers().set(ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");
        response.headers().set(ACCESS_CONTROL_MAX_AGE ,"-1");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }



    /**
     * 发送的返回值
     *
     * @param ctx    返回
     * @param status 状态
     */
    public static void sendRespGetRsp(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead, HttpResponseStatus status, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        response.headers().set("fileSize", sdkRequestHead.getFileSize());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }



    public static void saveUploadLog (SDKRequestHead sdkRequestHead) {
        FileUploadRecord fileUploadRecord = new FileUploadRecord();
        fileUploadRecord.setStartTime(sdkRequestHead.getStartTime());
        fileUploadRecord.setEndTime(new Date());
        fileUploadRecord.setSuss(sdkRequestHead.isSucc());
        fileUploadRecord.setSysname("comm");
        fileUploadRecord.setNodeName(FtpConfig.getInstance().getNodeName());
        fileUploadRecord.setFileVersion(0l);
        fileUploadRecord.setFileName(sdkRequestHead.getServFileName());
        fileUploadRecord.setServerIp(FtpConfig.getInstance().getHostIp());
        fileUploadRecord.setFlowNo(sdkRequestHead.getRqsSrlNo());
        fileUploadRecord.setTranCode(sdkRequestHead.getTranCode());
        fileUploadRecord.setUid(sdkRequestHead.getUid());
        fileUploadRecord.setFileSize(sdkRequestHead.getFileSize());
        fileUploadRecord.setAuthFlag(true);
        fileUploadRecord.setScrtFlag(sdkRequestHead.isSign() ||sdkRequestHead.isMd5());
        fileUploadRecord.setLastPiece(sdkRequestHead.isLastPiece());
        fileUploadRecord.setFileMsgFlag(FileMsgType.PUT_AUTH);

        EsbFileService.getInstance().logFileUpload(fileUploadRecord);
    }

    public static void saveDownloadLog(SDKRequestHead sdkRequestHead) throws Exception {

        FileDownloadRecord downloadRecord = new FileDownloadRecord();
        downloadRecord.setStartTime(sdkRequestHead.getStartTime());
        downloadRecord.setEndTime(new Date());
        downloadRecord.setSuss(sdkRequestHead.isSucc());
        downloadRecord.setSysname("comm");
        downloadRecord.setNodeName(FtpConfig.getInstance().getNodeName());
        downloadRecord.setNodeList(null);
        downloadRecord.setFileVersion(0l);
        downloadRecord.setServerIp(FtpConfig.getInstance().getHostIp());
        downloadRecord.setFlowNo(sdkRequestHead.getRqsSrlNo());
        downloadRecord.setTranCode(sdkRequestHead.getTranCode());
        downloadRecord.setUid(sdkRequestHead.getUid());
        downloadRecord.setFileSize(sdkRequestHead.getFileSize());
        downloadRecord.setAuthFlag(true);
        downloadRecord.setScrtFlag(sdkRequestHead.isSign() ||sdkRequestHead.isMd5());
        downloadRecord.setLastPiece(sdkRequestHead.isLastPiece());
        downloadRecord.setFileExists(true);
        downloadRecord.setFileName(sdkRequestHead.getServFileName());
        EsbFileService.getInstance().logFileDownload(downloadRecord);
    }





}
