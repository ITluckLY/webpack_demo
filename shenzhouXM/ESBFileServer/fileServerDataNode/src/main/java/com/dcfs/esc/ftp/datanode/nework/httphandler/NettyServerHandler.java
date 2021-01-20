package com.dcfs.esc.ftp.datanode.nework.httphandler;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.key.Key;
import com.dcfs.esb.ftp.key.KeyManager;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import com.dcfs.esb.ftp.server.model.FileDownloadRecord;
import com.dcfs.esb.ftp.server.model.FileUploadRecord;
import com.dcfs.esb.ftp.server.service.GetAuthUser;
import com.dcfs.esb.ftp.server.service.PutAuthUser;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.comm.scrt.security.aes.AESKey;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSAKeyPair;
import com.dcfs.esc.ftp.comm.scrt.security.util.Securitier;
import com.dcfs.esc.ftp.datanode.file.HttpFile;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final String DEF_UPLOAD = "/upload";
    private static final String TYPE_UPLOAD = "upload";
    private static final String DEF_DOWNLOAD = "/download";
    private static final String TYPE_DOWNLOAD = "download";

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    private String uploadPath;

    public NettyServerHandler(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    private String result = "";

    /*
     * 收到消息时，返回信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            result = "未知请求!";
            send(ctx, result, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();


        try {
            String path = httpRequest.uri();          //获取路径
            HttpMethod method = httpRequest.method();//获取请求方法
            byte[] body = getBody(httpRequest);
            //如果不是这个路径，就直接返回错误

//            log.info("接收到:"+method+" 请求");
//            log.info("body:"+body);//接受到的消息，做业务逻辑处理...


            //如果是POST请求
            if (HttpMethod.POST.equals(method)) {

                SDKRequestHead sdkRequestHead = getHeader(httpRequest);//获取请求头参数
                sdkRequestHead.setUserIp(clientIP);
                log.info("流水号{},客户端地址{}",sdkRequestHead.getRqsSrlNo(),clientIP);
                boolean auth = doAuthByUserPasswd(sdkRequestHead);
                if (!auth) {
                    sdkRequestHead.setRetCode("9999");
                    sdkRequestHead.setRetMsg(FtpErrCode.AUTH_PWD_FAILED);
                    sendSdkRsp(ctx, sdkRequestHead,HttpResponseStatus.OK);
                    return;
                }


                if (path.startsWith(DEF_UPLOAD)) {//上传
                    Boolean tranAuth = doTranCodeAuth(sdkRequestHead, TYPE_UPLOAD);
                    if (!tranAuth) {
                        sdkRequestHead.setRetCode("9999");
                        sdkRequestHead.setRetMsg(FtpErrCode.AUTH_TRAN_CODE_FAILED);
                        sendSdkRsp(ctx, sdkRequestHead,HttpResponseStatus.OK);
                    }

                    uploadFile(ctx, sdkRequestHead, body);
                    return;

                } else if (path.startsWith(DEF_DOWNLOAD)) {//下载
                    Boolean tranAuth = doTranCodeAuth(sdkRequestHead, TYPE_DOWNLOAD);
                    if (!tranAuth) {
                        sdkRequestHead.setRetCode("9999");
                        sdkRequestHead.setRetMsg(FtpErrCode.AUTH_TRAN_CODE_FAILED);
                        sendSdkRsp(ctx, sdkRequestHead,HttpResponseStatus.OK);
                    }
                    downloadFile(ctx, sdkRequestHead);
                    return;
                }


            } else if (HttpMethod.GET.equals(method)) {

                //如果是GET请求
                if (path.startsWith(DEF_DOWNLOAD) && null != path) {//get下載

                    SDKRequestHead sdkRequestHead = getHeaderByUri(path);
                    sdkRequestHead.setUserIp(clientIP);
                    log.info("流水号{},客户端地址{}",sdkRequestHead.getRqsSrlNo(),clientIP);

                    sdkRequestHead.setStartTime(new Date());

                    Boolean tranAuth = doTranCodeAuth(sdkRequestHead, TYPE_DOWNLOAD);
                    if (!tranAuth) {
                        sdkRequestHead.setRetCode("9999");
                        sdkRequestHead.setRetMsg(FtpErrCode.AUTH_TRAN_CODE_FAILED);
                        sendSdkRsp(ctx, sdkRequestHead,HttpResponseStatus.OK);
                    }
                    String realFileName = getRealDown(sdkRequestHead);
                    File downFile = new File(realFileName);
//                    openForReadByGet(ctx,downFile);
                    try {
                        openForReadByGetNetty(ctx, downFile, httpRequest);
                        sdkRequestHead.setSucc(true);
                    } catch (Exception e) {
                       log.error("Get 下载失败。",e);
                        sdkRequestHead.setSucc(false);
                    }
                    saveDownloadLog(sdkRequestHead);

                }
                //接受到的消息，做业务逻辑处理...

//                send(ctx,result, HttpResponseStatus.OK);
                return;
            } else if (HttpMethod.PUT.equals(method)) {//如果是PUT请求
                //接受到的消息，做业务逻辑处理...
                result = "PUT请求";
                send(ctx, result, HttpResponseStatus.OK);
                return;
            } else if (HttpMethod.DELETE.equals(method)) {//如果是DELETE请求
                //接受到的消息，做业务逻辑处理...
                result = "DELETE请求";
                send(ctx, result, HttpResponseStatus.OK);
                return;
            } else {
                result = "请求[" + method + "]非法请求!";
                send(ctx, result, HttpResponseStatus.BAD_REQUEST);
                return;
            }
        } catch (Exception e) {
            System.out.println("处理请求失败!");
            e.printStackTrace();
        } finally {
            //释放请求
            httpRequest.release();
        }
    }

    /**
     * 解密
     *
     * @param sdkRequestHead
     * @param body
     * @return
     * @throws IOException
     */
    public byte[] deRSA(SDKRequestHead sdkRequestHead, byte[] body) throws IOException {
        String key = sdkRequestHead.getKey();
        String sign = sdkRequestHead.getSign();

        //解密流程 start
        PrivateKey priB;
        PublicKey pubA;
        Securitier pS = null;
        try {
            String uid = sdkRequestHead.getUid();
            Key userPubAKey = KeyManager.getInstance().serachKey(uid,KeyManager.SYSTEM_P);
            String userPubA =  "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiJOeidVQ2IM6u+P9I4npZCylGawYkA23gOxl9YF09fbE+/G2O5Db4kCilUj+YeFtVLPjaHwmHx5xVhx9+6vTIS0Ty/0rmlZ/+2h28SKvAry0yauzq5pNcPvebkZAFWZZfOuwigp979q7DL9SdG+l6/5jAeXrxvYr+UUEP01aqxYtkbMa4NpM6OoW93ZXdwzlQObzb6IMXjdyD+L/yKxuFiwkl4UHLi74RPFq4ohqzo8ENhXvnmjD1x5MJPo0rapG4mX26UJ7MLSSPxpq/VVL2LzOc2Tpv0rH0zaFmgLy9gaDfV5ggpyWvLW8x5CFsDZgb++scun7x6lJDiiYsbE8PQIDAQAB";
            if (null != userPubAKey && userPubAKey.getContent() !=null ) {
                userPubA = userPubAKey.getContent();
            }

            Key platformPriBKey = KeyManager.getInstance().serachKey(uid,KeyManager.SYSTEM_S);
            String platformPriB = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCtKaFQ2Yqj5/bzHT6VGvoYTeBQF0LjPnWg71FndqtOv2qtc37Cm/Xiv5uv0ifJWWH/mUaVsIV+4AXKulUdawp/jlCsN3wpIyPmUux3TR3f++mwAkqh6YnuATgUkNgwB9HBYX8Vto1mSmoXv5W5iQ/CNrEp6xyX3uWu508Z/yPTkKBgBiK8Y5Me0hhVSwYdZbML5b0967HPkkbb03Cz0GbCWfNfFsCvIk1ytKofM++geNiawbO5mHTI4udJhNiZQcIFtiNH9vqmvDjZeC6Ixx9GJ1EhBjE+SNn01VmFmVs4a42uIefaGWxdztLT84jS3QqE+7J2P7tHRD4jnQXHstN5AgMBAAECggEBAKAeNoREI+abv8Xd7NiDMW7308s7NyZZwvslT+iT2qPebA4nFbdt71fURWm2sgOFGxD18kbICyjejXx//+RL7lE0j3QveievzqpTU0Sdklpi3htagxhkkp35kpmxtbX0BQkD1jeFrgpAFQoQpaoCCwhS+k8AkFf+S/QG/yXSvpYJRtoBOE+awKZnWfJaiIqvBv8Pr+RM2S9NMQWHHbd96eqLG3xr+7dU2RBLHzeDv4vIKbH9iIRCopzBUn+nvoblFY7dOJWMP5OV95B5QUqKjyRKFSwEIfLlhXsWdYRkrsO6u6kBoGPhaH81hHuSuB6yndLA5a0UvEnERUdhvFKnx2ECgYEA5hFpM2tbRmquNoLSYMLQTzBVcJPyddTt0EIzx3Tab+cbHYwt8za4PCrwIafp5jOZ1CEzYHM+oNI48WhNvo59B0HKkSrmJYvSmP57Axmm4l3u9VPYBIsEfZiCqMAq6uTwrCGqGczuSV5OlC/o1pq5Rq0iy31mSzyDYZqlbRXXAKcCgYEAwK44SEvZSgdpdx941iHgcUhSoJhHT+9Cn9Gn5LtP/T3oH+fDbKHyS4WYd0lqlfLNsOxuAZ4aq09tgPGQDxB28pQfLbrEtB8dFNyS6NdVqUsznv6muzgvvndPEmTeY7f7K79gExseT00agUfZ8pfwI+89NIHYfFDpA7A912aC7t8CgYBRz/UyBMYzdzI2TjUTlZNP5acsXmAFCM/8k94n2mTnEC6zRir85wOC2d4EidqCMu1L4vaLINAvjjITnnyu+6p4TywCjVfEGEGuiCgkRRX0P2T/Nm6Fxw20v+wOqC9a0kjHXT1I6Xf+/RtCVPsefWzPNjg+Vnxu2rj5l22mKfMH1wKBgQCF2QC0CCybpmZ3H9+7MLFPQGRVtzLirlxj1SqCVDSOwPB9KWyyDBXarKVrSA7It3B31OXeLcxs8LIq/qOKM2/Nt2OpSrLWRT7YfRl4sg2JmvnzGwcLy04vS4YcSwHJ18adb0X4P8BgajldUWJviWiCLfEghoqyYSpdK5LqDQYrlQKBgQCHNe3MIQtyfZ1en+5KvpgOLv7krDNRjhr1GMbQJ8vfCtR87gBQfYvhpvt0hte0XjmVSVwMtwQLDXXndCYHRSHALr6F35xULOynmSc3S+4wIaVTt/PzhQLyxQWE3RxxK+I44OAP4RXuTjXn6PogQ+z/yofUvqYW2IdfTk1YL8TszQ==";
            if (null != platformPriBKey && platformPriBKey.getContent() !=null ) {
                platformPriB = platformPriBKey.getContent();
            }

            priB = RSAKeyPair.getPrivateKey(platformPriB);
            pubA = RSAKeyPair.getPublicKey(userPubA);
            RSAKeyPair pRsa = new RSAKeyPair();
            //pRsa 表示provider 的密钥信息:自己的私钥、别人的公钥
            pRsa.load(priB.getEncoded(), pubA.getEncoded());
            pS = new Securitier();
            pS.setKeyPair(pRsa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //利用key解密解签
        byte[] pRsp = pS.decVerify(key, sign, new String(body, StandardCharsets.UTF_8), true);

        String responseMsg = new String(pRsp, "UTF-8");
//        log.info("服务端接收明文:\r\n" + responseMsg);

        //解密流程 end
        return pRsp;

    }

    public void uploadFile(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead, byte[] body) throws IOException, FtpException {

        result = "开始接收文件分片";
        sdkRequestHead.setStartTime(new Date());//开始时间
        byte[] deRSAFile = new byte[0];
        if (sdkRequestHead.isSDK() && sdkRequestHead.isSign()) {

            deRSAFile = deRSA(sdkRequestHead, body);
        } else {
            deRSAFile = body;

        }
        if (sdkRequestHead.isMd5()) {

        }

        String realFileName = getRealUpPath(sdkRequestHead);
        String tempFilePath = realFileName + SvrGlobalCons.DCFS_TMP_FILE_EXT;
        File tempFile = new File(tempFilePath);
        openForWrite(tempFile, sdkRequestHead.getOffset(), deRSAFile);

        result = "接收分片偏移量[" + sdkRequestHead.getOffset() + "]完成";
        if (sdkRequestHead.isLastPiece() || !sdkRequestHead.isSDK()) {
            File newRnmFile = new File(tempFile.getParent(), sdkRequestHead.getFileName());
            if (tempFile.renameTo(newRnmFile)) {
                log.info("flowNo:{}#重命名本地文件[{}]成功，重命名后文件[{}]",
                        sdkRequestHead.getRqsSrlNo(),
                        tempFile,
                        newRnmFile);
                sdkRequestHead.setSucc(true);


                result = "{\"retcode\":\"0000\",\"retmsg\":\"文件下载成功！\"}";

            } else {
                result = "POST请求重名失败";
                log.error("flowNo:{}#重命名本地文件[{}]失败，重命名后文件[{}]",
                        sdkRequestHead.getRqsSrlNo(),
                        tempFile,
                        newRnmFile);
                sdkRequestHead.setSucc(false);

                result = "{\"retcode\":\"9999\",\"retmsg\":\"文件下载失败！\"}";
            }

        }

        log.info("flowNo{},{}",sdkRequestHead.getRqsSrlNo(),result);
        send(ctx, result, HttpResponseStatus.OK);
        saveUploadLog (sdkRequestHead);
        //路由通知
        if(sdkRequestHead.isSucc()) {
            HttpFileRouteHandler routeHandler = new HttpFileRouteHandler();
            routeHandler.doFileRoute(sdkRequestHead);
        }

    }

    public void downloadFile(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead) throws IOException, FtpException {

        result = "开始下载文件分片";
        sdkRequestHead.setStartTime(new Date());//开始时间

        String realFileName = getRealDown(sdkRequestHead);
        File downFile = new File(realFileName);
        openForRead(ctx, downFile, sdkRequestHead);
        result = "读取分片偏移量[" + sdkRequestHead.getOffset() + "]完成";
        log.info("flowNo{},{}",sdkRequestHead.getRqsSrlNo(),result);

    }

    public String getRealUpPath(SDKRequestHead sdkRequestHead) {

        String fileRootPath = FtpConfig.getInstance().getFileRootPath();
        String serverPath = FileUtil.concatFilePath("/",sdkRequestHead.getTranCode(),
                sdkRequestHead.getUid(),
                sdkRequestHead.getRqsSrlNo());
        sdkRequestHead.setServFileName(serverPath);
        return FileUtil.concatFilePath(fileRootPath, serverPath);

    }

    public String getRealDownPath(SDKRequestHead sdkRequestHead) {

        String fileRootPath = FtpConfig.getInstance().getFileRootPath();
        String serverPath = FileUtil.concatFilePath("/",sdkRequestHead.getTranCode(),
                sdkRequestHead.getUid(),
                sdkRequestHead.getRqsSrlNo());
        sdkRequestHead.setServFileName(serverPath);
        return FileUtil.concatFilePath(fileRootPath,serverPath);

    }

    /**
     * 下载参数里获取路径
     * @param sdkRequestHead
     * @return
     */
    public String getRealDown(SDKRequestHead sdkRequestHead) {

        String fileRootPath = FtpConfig.getInstance().getFileRootPath();

        String serverPath = sdkRequestHead.getFileName();
        sdkRequestHead.setServFileName(serverPath);
        return FileUtil.concatFilePath(fileRootPath,serverPath);

    }

    public long openForWrite(File tmpFile, long offset, byte[] deRSAFile) throws FtpException {


        // 目录不存在，则创建相关目录
        if (!tmpFile.getParentFile().exists()) {
            tmpFile.getParentFile().mkdirs();
        }
        RandomAccessFile tmpWriter = null;
        try {
            // 创建临时文件写入流
            tmpWriter = new RandomAccessFile(tmpFile, "rw");
            tmpWriter.seek(offset);
            tmpWriter.write(deRSAFile);

        } catch (Exception e) {
            log.error("nano:#flowNo:#打开文件出错", e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        } finally {
            closeQuietly(tmpWriter);
        }

        return offset;
    }

    /**
     * 读取文件，在服务端用于处理文件的下载，在客户端用于处理文件的下载。
     *
     * @throws FtpException
     */
    public void openForRead(ChannelHandlerContext ctx, File realFile, SDKRequestHead sdkRequestHead) throws FtpException {


        // 目录不存在，则创建相关目录
        if (!realFile.exists()) {
            return;
        }
        RandomAccessFile reader = null;
        try {
            // 创建临时文件写入流
            reader = new RandomAccessFile(realFile, "r");
            long size = realFile.length();
            int pieceNum = FtpConfig.getInstance().getPieceNum();
            long offset = sdkRequestHead.getOffset();
            if (offset > 0) reader.seek(offset);

            byte[] buff = new byte[pieceNum];
            // 用于保存实际读取的字节数
            int hasRead = 0;
            hasRead = reader.read(buff);
            if (hasRead < pieceNum || offset + hasRead >= size) {
                sdkRequestHead.setLastPiece(true);
                sdkRequestHead.setSucc(true);
                saveDownloadLog(sdkRequestHead);
            } else {
                sdkRequestHead.setLastPiece(false);
            }
            byte[] tmp = new byte[hasRead];
            if (hasRead < buff.length) {
                System.arraycopy(buff, 0, tmp, 0, tmp.length);
            } else {
                tmp = buff;
            }
            Map<String, String> dataMap = doEncSign(tmp);

            sendResp(ctx, dataMap, sdkRequestHead, HttpResponseStatus.OK);


        } catch (Exception e) {
            log.error("nano:#flowNo:#打开文件出错", e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        } finally {
            closeQuietly(reader);
        }

    }

    /**
     * 读取文件，在服务端用于处理文件的下载，在客户端用于处理文件的下载。
     *
     * @throws FtpException
     */
    public void openForReadByGet(ChannelHandlerContext ctx, File realFile) throws FtpException {


        // 目录不存在，则创建相关目录
        if (!realFile.exists()) {
            return;
        }
        RandomAccessFile reader = null;
        try {
            // 创建临时文件写入流
//            reader = new RandomAccessFile(realFile, "r");
//            long size = realFile.length();
//            int pieceNum = FtpConfig.getInstance().getPieceNum();
//            long offset= sdkRequestHead.getOffset();
//            if (offset > 0) reader.seek(offset);

//
//            byte[] buff = new byte[pieceNum];
//            // 用于保存实际读取的字节数
//            int hasRead = 0;
//
//            // 循环读取
//            while ((hasRead = reader.read(buff)) > 0) {
//                // 打印读取的内容,并将字节转为字符串输入
//
//                byte[] tmp = new byte[hasRead];
//                if (hasRead < buff.length) {
//                    System.arraycopy(buff, 0, tmp, 0, tmp.length);
//                } else {
//                    tmp = buff;
//                }
//                ctx.write(buff);
//
//
//                ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192));
//
//
//
//
//
//                //发送一个分片，则写入新的偏移量到缓存文件
//                //
//                offset += hasRead;
//            }
            reader = new RandomAccessFile(realFile, "r");
            long size = realFile.length();
            int pieceNum = FtpConfig.getInstance().getPieceNum();

            ctx.write(new ChunkedFile(reader, 0, size, pieceNum));
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//            sendRespGetRsp(ctx, sdkRequestHead, HttpResponseStatus.OK, "ok");
        } catch (Exception e) {
            log.error("nano:#flowNo:#打开文件出错", e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        } finally {
            closeQuietly(reader);
        }

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
     * 读取文件，在服务端用于处理文件的下载，在客户端用于处理文件的下载。
     *
     * @throws FtpException
     */
    public void openForRead1(ChannelHandlerContext ctx, File realFile, HttpFile context) throws FtpException {


        // 目录不存在，则创建相关目录
        if (!realFile.exists()) {
            return;
        }
        RandomAccessFile reader = null;
        try {
            // 创建临时文件写入流
            reader = new RandomAccessFile(realFile, "r");
            long size = realFile.length();
            int pieceNum = FtpConfig.getInstance().getPieceNum();
            long offset = context.getOffset();
            if (offset > 0) reader.seek(offset);

            byte[] buff = new byte[FtpConfig.getInstance().getPieceNum()];
            // 用于保存实际读取的字节数
            int hasRead = 0;

            // 循环读取
            while ((hasRead = reader.read(buff)) > 0) {
                // 打印读取的内容,并将字节转为字符串输入
                if (hasRead < pieceNum || offset + hasRead <= size) {
                    context.setLastPiece(true);
                } else {
                    context.setLastPiece(false);
                }
                Map<String, String> dataMap = doEncSign(buff);

                sendResp(ctx, dataMap, context, HttpResponseStatus.OK);
                //发送一个分片，则写入新的偏移量到缓存文件
                //
                offset += hasRead;
            }

        } catch (Exception e) {
            log.error("nano:#flowNo:#打开文件出错", e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        } finally {
            closeQuietly(reader);
        }

    }

    /**
     * Put-加密加签
     *
     * @param buffer
     * @return
     */
    public static Map<String, String> doEncSign(byte[] buffer) {
        PrivateKey priB;
        PublicKey pubA;
        Map<String, String> dataMap = null;

        RSAKeyPair cRsa = new RSAKeyPair();
        AESKey aes = new AESKey();
        aes.generator();
        try {
            //priB = RSAKeyPair.getPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCj6FEYXIP4kgSYzP5NeW+V+E5YoSWmUmB6/+ymuwREod7KC7KXygDUJnldsC/F40R3/r5Dj6pv9Qi83OriNCIAeib2p5Z1aN0OkhPGPi7tanMbElu37wjQHSLZlqU/3PflF/vnQHbZ4fgzjhOSpOJPVrviLqTJ2gCuRkjBXyZ8wsHvqDEdX+wUE1Z3n6Y+o5sdr8OASzxtO6JHCnFUnV1KOQBstNkqF5M/2Fnsg9Je0awPICJ8MZFyLe/tGWXg3fH3AVWoAiLCNSfSWlwq702c5xHWUT7j1EyL8ZL8txlUCXLbkeHJmGzpdnH51LCS8EdMIUPz3VgwgnLzc8Q/qUvLAgMBAAECggEBAIpDJ11IdV6SNeR7T60k2dcFDXm//dVuOcqn5gXDTldiwF9pPK7EDKzpA4nfXH0uOAyMzAyLvPcSGNvP8yb7WQ9T+1gniEjkO0zWNm4M+GL3X7+fXdUrgyCi40nuxNi5WjdbYvfwrhEfh9Jdb+9MvUa14GlsdT98cQSRb0AHNp0X/2vZ93wmk4Faew7rRyEi775Z6O8dKbxyifWt+FLTIBSXFCS8lS/GEdGq37VsKk71MdrOivP1zkRYyrQyEG84AoPpLeKzNKZCzy3YNSGXZOLQDB1udrrZ76OZuFQqsfzwVj0zErvFFufXDmYSV8eLWKY7F7e0ifyP0appmLrHRKkCgYEA/K0MG1OJcsEstyMpAs8yCpC2Muz4+wC8X2YZ+Dh3M1D4gw4eqAUOCXrIuRPb9S9s/Kt4GS+BcXZbLRG/7850P/pe6Z1gH+wod7Qh1rAXki9YwLtFks3b8M7kCT1c1zAyvIGtFx6t0Qpdq0soqWy+agiYq4X4o6kNzRQN4JSFekUCgYEAphBRfMB88yiKA+9gv7CnMF1dCS/irKnufA2aWwjN8v/DEDXdjAx0ysgZqB17kCvMKyxKKKzLh0on/6FkeFuyUe6mrujf3wis0h9+EM4iDF84Mx6ZbCEnpkzBnPcpcMvSNw4KhegmNS0l0KphaMK4w/fYfnv6lQagFnCQr46rls8CgYBjrei+xv/MM4TuYoKFRzPYkyotgOrgKnQltmO8Vpo/tkuzd9iENCpLdxLEYJ8/ZIw8SXDBjsoj9qsVZpEvi2S08JKM2rbXIRT91CQdS66gzujWb4qM1YVUxGmVc42ynqMFVqrwGfw8ITi+oJHT9MBRmD6SO/HQrppxMt4eoLjfeQKBgB9o/sDF99xWUqSj5nzbgQQY5LwUHp/iFIXKXOPTKoFH9zFdvuX6hSzF5HRz/ycp4ZhY7D39URHp6N7lPAe38Gam6Ug0LAQXs/ialFHBa5dDe3HP290j+EfwRL7X0TLQmpZnRX5GhtOQEpMBZyNVkXUfsAdck+0UL7uO1w8dwT7pAoGBAM8WACknQ6a65Db1YCFq0bUag/bR4yYP2KY2taJ4n4Cm9VEp3tXR2pAYlpsz4JCz6hz1ww91DuMxYIfOvLYDAlfy4I8CJgiudp2bB0tYy9bDyrWmjmLLhTOjOvRvlAc/7ZehXgKon2GU48WKFsvTd0620zQ3U1SkPqnNs2MsQAh1");
            //pubA = RSAKeyPair.getPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhkPYAAAToyfWlTzSN2Fe069NgH0fjfO+yGlkHeUMOmfA4rvN1TCVQ/f5HmbFMocM3nd90YId7aOQcqqMiI+mS9Y0OEjgmUju9IaFIhLqAYp6blLF9CjjSWW6PQ9ow9NtoIgZCiHxIFm7rxnyGh3YUwHTnATqI92X72VKQm9lVHkRx2ZQT2c2Ptz45WNUMxN6IRtXGWTOpGJJykDVt9ZAJB4nx2+lEsDqUDeyXfJoJ+casw4+NhSHpwUJpJ2FQAL2cCNIgj5eCqxBbe9buE8r7QW3Uxj+iBh0BEUTWYwMOihtTBqTFQUQ43P+mxFFXBZ3kR06Hi40/MkOVP0ww4QNWQIDAQAB");
            priB = RSAKeyPair.getPrivateKey("MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCtKaFQ2Yqj5/bzHT6VGvoYTeBQF0LjPnWg71FndqtOv2qtc37Cm/Xiv5uv0ifJWWH/mUaVsIV+4AXKulUdawp/jlCsN3wpIyPmUux3TR3f++mwAkqh6YnuATgUkNgwB9HBYX8Vto1mSmoXv5W5iQ/CNrEp6xyX3uWu508Z/yPTkKBgBiK8Y5Me0hhVSwYdZbML5b0967HPkkbb03Cz0GbCWfNfFsCvIk1ytKofM++geNiawbO5mHTI4udJhNiZQcIFtiNH9vqmvDjZeC6Ixx9GJ1EhBjE+SNn01VmFmVs4a42uIefaGWxdztLT84jS3QqE+7J2P7tHRD4jnQXHstN5AgMBAAECggEBAKAeNoREI+abv8Xd7NiDMW7308s7NyZZwvslT+iT2qPebA4nFbdt71fURWm2sgOFGxD18kbICyjejXx//+RL7lE0j3QveievzqpTU0Sdklpi3htagxhkkp35kpmxtbX0BQkD1jeFrgpAFQoQpaoCCwhS+k8AkFf+S/QG/yXSvpYJRtoBOE+awKZnWfJaiIqvBv8Pr+RM2S9NMQWHHbd96eqLG3xr+7dU2RBLHzeDv4vIKbH9iIRCopzBUn+nvoblFY7dOJWMP5OV95B5QUqKjyRKFSwEIfLlhXsWdYRkrsO6u6kBoGPhaH81hHuSuB6yndLA5a0UvEnERUdhvFKnx2ECgYEA5hFpM2tbRmquNoLSYMLQTzBVcJPyddTt0EIzx3Tab+cbHYwt8za4PCrwIafp5jOZ1CEzYHM+oNI48WhNvo59B0HKkSrmJYvSmP57Axmm4l3u9VPYBIsEfZiCqMAq6uTwrCGqGczuSV5OlC/o1pq5Rq0iy31mSzyDYZqlbRXXAKcCgYEAwK44SEvZSgdpdx941iHgcUhSoJhHT+9Cn9Gn5LtP/T3oH+fDbKHyS4WYd0lqlfLNsOxuAZ4aq09tgPGQDxB28pQfLbrEtB8dFNyS6NdVqUsznv6muzgvvndPEmTeY7f7K79gExseT00agUfZ8pfwI+89NIHYfFDpA7A912aC7t8CgYBRz/UyBMYzdzI2TjUTlZNP5acsXmAFCM/8k94n2mTnEC6zRir85wOC2d4EidqCMu1L4vaLINAvjjITnnyu+6p4TywCjVfEGEGuiCgkRRX0P2T/Nm6Fxw20v+wOqC9a0kjHXT1I6Xf+/RtCVPsefWzPNjg+Vnxu2rj5l22mKfMH1wKBgQCF2QC0CCybpmZ3H9+7MLFPQGRVtzLirlxj1SqCVDSOwPB9KWyyDBXarKVrSA7It3B31OXeLcxs8LIq/qOKM2/Nt2OpSrLWRT7YfRl4sg2JmvnzGwcLy04vS4YcSwHJ18adb0X4P8BgajldUWJviWiCLfEghoqyYSpdK5LqDQYrlQKBgQCHNe3MIQtyfZ1en+5KvpgOLv7krDNRjhr1GMbQJ8vfCtR87gBQfYvhpvt0hte0XjmVSVwMtwQLDXXndCYHRSHALr6F35xULOynmSc3S+4wIaVTt/PzhQLyxQWE3RxxK+I44OAP4RXuTjXn6PogQ+z/yofUvqYW2IdfTk1YL8TszQ==");
            pubA = RSAKeyPair.getPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiJOeidVQ2IM6u+P9I4npZCylGawYkA23gOxl9YF09fbE+/G2O5Db4kCilUj+YeFtVLPjaHwmHx5xVhx9+6vTIS0Ty/0rmlZ/+2h28SKvAry0yauzq5pNcPvebkZAFWZZfOuwigp979q7DL9SdG+l6/5jAeXrxvYr+UUEP01aqxYtkbMa4NpM6OoW93ZXdwzlQObzb6IMXjdyD+L/yKxuFiwkl4UHLi74RPFq4ohqzo8ENhXvnmjD1x5MJPo0rapG4mX26UJ7MLSSPxpq/VVL2LzOc2Tpv0rH0zaFmgLy9gaDfV5ggpyWvLW8x5CFsDZgb++scun7x6lJDiiYsbE8PQIDAQAB");

            cRsa.load(priB.getEncoded(), pubA.getEncoded());
            Securitier cS = new Securitier();
            cS.setKeyPair(aes);
            cS.setKeyPair(cRsa);
            // 传入请求报文和key，进行加密加签 true是否包含密匙
            dataMap = cS.encSign(buffer, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }

    /**
     * 获取body参数
     *
     * @param request
     * @return
     */
    private byte[] getBody(FullHttpRequest request) {
        ByteBuf buf = request.content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        return req;
    }

    /**
     * 获取body参数
     *
     * @param request
     * @return
     */
    private SDKRequestHead getHeader(FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        List<Map.Entry<String, String>> headerMap = headers.entries();
        SDKRequestHead sdkRequestHead = new SDKRequestHead();
        for (Map.Entry<String, String> header : headerMap) {
            if (header.getKey().equalsIgnoreCase("key")) {
                sdkRequestHead.setKey(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("offset")) {
                sdkRequestHead.setOffset(Long.parseLong(header.getValue()));
                continue;
            }

            if (header.getKey().equalsIgnoreCase("sign")) {
                sdkRequestHead.setSign(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("uid")) {
                sdkRequestHead.setUid(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("passwdid")) {
                sdkRequestHead.setPasswdId(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("fileSize")) {
                sdkRequestHead.setFileSize(Long.parseLong(header.getValue()));
                continue;
            }

            if (header.getKey().equalsIgnoreCase("RqsSrlNo")) {
                sdkRequestHead.setRqsSrlNo(header.getValue());
                continue;

            }

            if (header.getKey().equalsIgnoreCase("System")) {
                sdkRequestHead.setSystemGroup(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("fileName")) {
                sdkRequestHead.setFileName(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("tranCode")) {
                sdkRequestHead.setTranCode(header.getValue());
                continue;

            }
            if (header.getKey().equalsIgnoreCase("lastPiece")) {
                sdkRequestHead.setLastPiece(Boolean.valueOf(header.getValue()));
                continue;

            }
            if (header.getKey().equalsIgnoreCase("Authorization")) {
                sdkRequestHead.setDesAuthorization(header.getValue());
                continue;

            }
            if (header.getKey().equalsIgnoreCase("isSign")) {
                sdkRequestHead.setSign(Boolean.valueOf(header.getValue()));
                continue;

            }
            if (header.getKey().equalsIgnoreCase("isMd5")) {
                sdkRequestHead.setMd5(Boolean.valueOf(header.getValue()));
                continue;

            }
            if (header.getKey().equalsIgnoreCase("isSDK")) {
                sdkRequestHead.setSDK(Boolean.valueOf(header.getValue()));
                continue;

            }
            if (header.getKey().equalsIgnoreCase("md5")) {
                sdkRequestHead.setMd5(header.getValue());
                continue;

            }

        }
        return sdkRequestHead;
    }

    /**
     * 获取body参数
     *
     * @return
     */
    private SDKRequestHead getHeaderByUri(String path) {
        SDKRequestHead sdkRequestHead = new SDKRequestHead();
        QueryStringDecoder decoder = new QueryStringDecoder(path);
        Map<String, List<String>> parame = decoder.parameters();
        Iterator<Map.Entry<String, List<String>>> iterator = parame.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> next = iterator.next();


            if (next.getKey().equalsIgnoreCase("userid")) {
                sdkRequestHead.setUid(next.getValue().get(0));
                continue;
            }

            if (next.getKey().equalsIgnoreCase("passid")) {
                sdkRequestHead.setPasswdId(next.getValue().get(0));
                continue;
            }

            if (next.getKey().equalsIgnoreCase("RqsSrlNo")) {
                sdkRequestHead.setRqsSrlNo(next.getValue().get(0));
                continue;

            }

            if (next.getKey().equalsIgnoreCase("fileName")) {
                sdkRequestHead.setFileName(next.getValue().get(0));

                continue;
            }

            if (next.getKey().equalsIgnoreCase("tranCode")) {
                sdkRequestHead.setTranCode(next.getValue().get(0));
                continue;

            }

        }
        return sdkRequestHead;
    }

    /**
     * 发送的返回值
     *
     * @param ctx     返回
     * @param context 消息
     * @param status  状态
     */
    private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 发送的返回值
     *
     * @param ctx    返回
     * @param status 状态
     */
    private void sendResp(ChannelHandlerContext ctx, Map<String, String> dataMap, SDKRequestHead sdkRequestHead, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(dataMap.get("msg"), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        response.headers().set("key", dataMap.get("key"));
        response.headers().set("sign", dataMap.get("sign"));
        response.headers().set("RqsSrlNo", sdkRequestHead.getRqsSrlNo());
        response.headers().set("offset", sdkRequestHead.getOffset());
        response.headers().set("lastPiece", sdkRequestHead.isLastPiece());
        response.headers().set("fileSize", sdkRequestHead.getFileSize());
//        response.headers().set("md5",sdkRequestHead.getMd5());


        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 发送的返回值
     *
     * @param ctx    返回
     * @param status 状态
     */
    private void sendRespGetRsp(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead, HttpResponseStatus status, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        response.headers().set("fileSize", sdkRequestHead.getFileSize());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
        super.channelActive(ctx);
    }

    private void closeQuietly(RandomAccessFile rfile) {
        if (rfile != null) {
            log.debug("#关闭rfile文件...");
            try {
                rfile.close();
            } catch (IOException e) {
                log.error("#关闭rfile文件出错#", e);
                IOUtils.closeQuietly(rfile);
            }
        }
    }


    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, FullHttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        this.sendAndCleanupConnection(ctx, response, request);
    }

    /**
     * 发送的返回值
     *
     * @param ctx     返回
     * @param status  状态
     */
    private void sendSdkRsp(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(sdkRequestHead.getRetMsg(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set("RqsSrlNo", sdkRequestHead.getRqsSrlNo());
        response.headers().set("retcode", sdkRequestHead.getRetCode());
        response.headers().set("retmsg", sdkRequestHead.getRetMsg());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
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
    public void saveUploadLog (SDKRequestHead sdkRequestHead) {
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

        EsbFileService.getInstance().logFileUpload(fileUploadRecord);
    }

    public void saveDownloadLog(SDKRequestHead sdkRequestHead) throws Exception {

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
        EsbFileService.getInstance().logFileDownload(downloadRecord);
    }
    public static void main(String[] args) {
        String passwd = "8DkCg18u1bAs/sKIjWBOEOEG6SrUqdgP6dkVYRzvdHqYZiVoak2tFlxlQKTXp9+R";
        passwd = ScrtUtil.decryptEsb(passwd);
        System.out.println(passwd);
    }

}