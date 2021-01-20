package com.dcfs.esc.ftp.datanode.http.httpopfactory;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.key.Key;
import com.dcfs.esb.ftp.key.KeyManager;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;

import com.dcfs.esb.ftp.server.service.GetAuthUser;
import com.dcfs.esb.ftp.server.service.PutAuthUser;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.utils.ShortUrlUtil;

import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.comm.scrt.security.Encrypter;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSAKeyPair;
import com.dcfs.esc.ftp.comm.scrt.security.util.Securitier;
import com.dcfs.esc.ftp.datanode.helper.HttpHelper;
import com.dcfs.esc.ftp.datanode.http.ITransferOp;
import com.dcfs.esc.ftp.datanode.nework.httphandler.HttpFileRouteHandler;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Created by Tianyza on 2019/12/20.
 */
public class UploadFileWithPost implements ITransferOp {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private static final String CLIENT_FILE_MD5 = "ClientFileMd5";
    private String userIP;
    private byte[] body;

    public UploadFileWithPost(String userIP, byte[] body) {
        this.userIP = userIP;
        this.body = body;
    }

    @Override
    public String doTransferFile(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        SDKRequestHead sdkRequestHead = new SDKRequestHead(httpRequest);//获取请求头参数
        sdkRequestHead.setUserIp(userIP);
        log.info("流水号{},客户端地址{}", sdkRequestHead.getRqsSrlNo(), userIP);
        boolean auth = false;
        try {
            auth = doAuthByUserPasswd(sdkRequestHead);
        } catch (FtpException e) {
            log.error("PwdAuthServiceError", e);
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
            tranAuth = doTranCodeAuth(sdkRequestHead, FileMsgType.PUT_AUTH);
        } catch (FtpException e) {
            log.error("上传服务权限校验失败", e);
        }
        if (!tranAuth) {
            sdkRequestHead.setRetCode("9999");
            sdkRequestHead.setRetMsg(FtpErrCode.AUTH_TRAN_CODE_FAILED);
            sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
        }
        if (null == body) {
            sdkRequestHead.setRetCode("9999");
            sdkRequestHead.setRetMsg(FtpErrCode.READ_REQ_LENGTH_ERROR);
            sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
        }
        try {
            uploadFile(ctx, sdkRequestHead, body);
        } catch (IOException e) {
            log.error("上传失败", e);
        } catch (FtpException e) {
            log.error("上传失败出现异常", e);
        }


        return null;
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

    /**
     * 发送的返回值
     *
     * @param ctx    返回
     * @param status 状态
     */
    private void sendSdkRsp(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(sdkRequestHead.getRetMsg(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set("RqsSrlNo", sdkRequestHead.getRqsSrlNo());
        response.headers().set("retcode", sdkRequestHead.getRetCode());
        response.headers().set("retmsg", sdkRequestHead.getRetMsg());
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

    public void uploadFile(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead, byte[] body) throws IOException, FtpException {

        String result = "开始接收文件分片";
        sdkRequestHead.setStartTime(new Date());//开始时间
        byte[] deRSAFile = new byte[0];

        if (sdkRequestHead.isSDK() && sdkRequestHead.isSign()) {

            deRSAFile = deRSA(sdkRequestHead, body);
        } else if (sdkRequestHead.isMd5()) {
            String md5 = sdkRequestHead.getMd5();
            if (null == md5 || "".equals(md5)) {
                sdkRequestHead.setRetCode("9999");
                sdkRequestHead.setRetMsg(FtpErrCode.FILE_MD5_VALID_FAIL);
                sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
                return ;
            }
            if(!md5.equals(Encrypter.md5(body))){
                sdkRequestHead.setRetCode("9999");
                sdkRequestHead.setRetMsg(FtpErrCode.FILE_CHECK_ERROR);
                sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
                return ;
            } else {
                deRSAFile = body;
            }

        } else {
            deRSAFile = body;

        }


        String realFileName = HttpHelper.getRealUpPath(sdkRequestHead);
        String tempFilePath = realFileName + SvrGlobalCons.DCFS_TMP_FILE_EXT;
        File tempFile = new File(tempFilePath);
        openForWrite(tempFile, sdkRequestHead.getOffset(), deRSAFile);

        result = "接收分片偏移量[" + sdkRequestHead.getOffset() + "]完成";
        sdkRequestHead.setRetCode("0000");
        if (!sdkRequestHead.isSDK()){
        	sdkRequestHead.setLastPiece(true);
        }
        if (sdkRequestHead.isLastPiece() || !sdkRequestHead.isSDK()) {
            File newRnmFile = new File(tempFile.getParent(), sdkRequestHead.getFileName());
            renameOrigFile(newRnmFile);//重命名源文件
            sdkRequestHead.setFileSize(tempFile.length());
            if (tempFile.renameTo(newRnmFile)) {
                log.info("flowNo:{}#重命名本地文件[{}]成功，重命名后文件[{}]",
                        sdkRequestHead.getRqsSrlNo(),
                        tempFile,
                        newRnmFile);
                sdkRequestHead.setSucc(true);
                HttpHelper.getRealServPath(sdkRequestHead);
                String cfgFileName = newRnmFile.getAbsolutePath() + SvrGlobalCons.DCFS_CFG_FILE_EXT;
                saveFileProperties(sdkRequestHead,cfgFileName);


                result = "{\"retcode\":\"0000\",\"retmsg\":\"文件上传成功！\"}";
                sdkRequestHead.setRetCode("0000");
                sdkRequestHead.setRetMsg("文件上传成功");

            } else {
                result = "POST请求重名失败";
                log.error("flowNo:{}#重命名本地文件[{}]失败，重命名后文件[{}]",
                        sdkRequestHead.getRqsSrlNo(),
                        tempFile,
                        newRnmFile);
                sdkRequestHead.setSucc(false);
                result = "{\"retcode\":\"9999\",\"retmsg\":\"文件上传失败！\"}";
                sdkRequestHead.setRetCode("9999");
                sdkRequestHead.setRetMsg("文件上传失败");
            }
            HttpHelper.saveUploadLog(sdkRequestHead);
        }

        log.info("flowNo{},{}", sdkRequestHead.getRqsSrlNo(), result);
//        HttpHelper.send(ctx, result, HttpResponseStatus.OK);

        sdkRequestHead.setRetMsg(result);
        sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);

        //路由通知
        if (sdkRequestHead.isSucc()) {
            HttpFileRouteHandler routeHandler = new HttpFileRouteHandler();
            routeHandler.doFileRoute(sdkRequestHead);
        }

    }

    /**
     * 写文件
     *
     * @param tmpFile
     * @param offset
     * @param deRSAFile
     * @return
     * @throws FtpException
     */
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
            IOUtils.closeQuietly(tmpWriter);
        }

        return offset;
    }

    /**
     * 解密
     *
     * @param sdkRequestHead
     * @param body
     * @return
     * @throws IOException
     */
    public static byte[] deRSA(SDKRequestHead sdkRequestHead, byte[] body) throws IOException {
        String key = sdkRequestHead.getKey();
        String sign = sdkRequestHead.getSign();

        //解密流程 start
        PrivateKey priB;
        PublicKey pubA;
        Securitier pS = null;
        try {
            String uid = sdkRequestHead.getUid();
            Key userPubAKey = KeyManager.getInstance().serachKey(uid, KeyManager.SYSTEM_P);
            String userPubA = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxH2aTjVO6Owk8yyi75U5/XLOPhEp4dArekEl0jyEey6oIX+thZI2nAk1rBIWvqiqVqB5TYfi16c2nFJGHg7M9fLfHLVlLMb9olNEy7EcefYRoc0ZZMXTF2slqLS1MNqnPR3ZaF5nQ3PzrcfUO6AQLQ8skkqEdEONcrHNNJewaEAL/zgu7ZduJKRe8XFTrTMkd9F+Y1fHsNVJbTVsYsTtJZuNKotrgrhlc2HufRU1TWKV43iJy/ZlM5SXAt7ML1alrd5awGrsl3ctyLqqU6qrEu1EraE4Do/h9dNA4u7MMxkhDFjReaaySj4eyWlFOUDEX9Xw4HHHdtiYkiTWAxH1kwIDAQAB";
            if (null != userPubAKey && userPubAKey.getContent() != null) {
                userPubA = userPubAKey.getContent();
            }

            Key platformPriBKey = KeyManager.getInstance().serachKey("open", KeyManager.SYSTEM_S);
            String platformPriB = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDLPCDOatsxQA0/RzVWu5ve0jRXIq8hKETknAYBdgOiVJjJA7DBaGoiEp3e2gDfPsa7PotsxR997AimXJ06YubkADkOvQmDzIr42LRqcwBJ0s0xMKqLdjyjp40IpKJI+uVleQcm7me/eN/wuM0JwECpmISGhux1N9Pm/vBn7wXsgS0btmgDuByysfiogG3Bu5oFeuWd5qibnyNiO7YKkaSKuF90Nzwwl3dJnjyrE64GtTV70fGoQ+ExPgpvy65H9ezEvNiN39PhJrKbv+ILycD0CKEQlSyg8/AtdS89bS+1AotAJGMhZh3i/6dxOi9o8J6e3Ha/ABbfkqJt+BdnjPYVAgMBAAECggEAbRCNgnixvJJlCYpLQ4pWsZMY55IDbcXQguaACpSsipbfjzBd0M0Z98MJ2aggQ63KtH1yESMQLWFXmv+kmMquobjE5fXof0RrZ/PTjjS8+OEZoMY/H6yKCkFGXFcNiCCsrnHutqPGJWjWtLZXwl3b1ae1pwQNLTKSkypJFV2eTjZnexTbQeTMKIajk1zyNDhV5sHvdlh57Mk1twNQAEbPpGHbZZRhmNRU/RM0MRErAWilG+sGfv9yolMJTsVmHTKZ/3dhqTydePTFkdxVbMaauesWXkIl/vgpCvYX5fm/CXKZGYiTKdK/hkkrUvXrwVFBapLZwsXCsDlLpP4PUiIrGQKBgQDqAD9kCatvQIO0Zerczckf9MMuEuDrMRLwRdUQveTxWead+Lb3hQSm4h+pJCVMslbsDzIEbyksUcKBkTjvLxe85lurbCfdUPwaxcWmwmpv2UsH7YLiWPedo/v9uVKSBJMm7pA/1obZZXJf2PNMW6y6qh7Rvh8PGMG2bnIW2KYHMwKBgQDeV20g1uvGwKRNEU1VUL/m5FT4KdHtL5g4hlwzSQs1z0kLZZQaH5IwfZAQy6YhBfZTSlrCXVMe9TDTjXsVYJMh6/dy6uf/uSFjgqJowd4XHd810D99j3auX9gedZPrVQdqZyQnWek62HmolA+Epk38/y1FlFMUoZ7yGIV9iTptlwKBgQCQSNrNelDk5mjPYVByGcl8FNYMoMNjAf1lfgmkqxJx5zcWDPf0o2Z86E0RZCJDqmuCXtQ+e7RtiXRfpAXxJZ6DcZ7wkl36TydD8tKuwo4SrW9gUgk0PBtibmzAw/av5nY9uTfkxQidH7poc0OBv70/1Anw1kQccO1w6aUbhTbKlQKBgEjmgXFzBnt0Tmq2cZS3lTQvSWzUyW/rWw2tgC2QlYxfGqQR+i1WjKg5gzL+ksnhGpLUnk18K/TKj+fNS74GnDdTFx8mxkqieMAX5QhUCG2r7bPHRAn3MAKcik/yXT2b84J/rur4H/CbachMQ4pKvOdnA4D80QDCvUrDn4E5rV6hAoGAWygmfGLWwaZe2x8q2eLCnsoeXch4Y4rbpW/jSJmW1yq9532d5J3BGJ20oL203ubJuX5SamzeSrdD1WtR+LlDzfuxxLSUVAdYIVO5tNlFuo1XIGIKMmXEPLH+1tRQnfnPNsaNzDgLi4i25yM34vNXsp3g2GYdeeQNJNwMHumJX8k=";
            if (null != platformPriBKey && platformPriBKey.getContent() != null) {
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
     *
     * @return
     */
    protected String renameByUUID(String fileName) {
        String pre = FilenameUtils.getBaseName(fileName);;
        String ext = FilenameUtils.getExtension(fileName);//不带原文件名
//        return pre + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")  + "." + ext;
        return pre +"_"+ ShortUrlUtil.short36(UUIDService.nextId()) + "." + ext;
    }

    /**
     * 保存文件的配置信息
     */
    public void saveFileProperties(SDKRequestHead sdkRequestHead,String cfgFileName) throws FtpException {
        File cfgFile = new File(cfgFileName);
        RandomAccessFile cfgWriter = null;
        try {
            cfgWriter = new RandomAccessFile(cfgFile, "rw");
        } catch (FileNotFoundException e) {
            return;
        }
        Properties fileProperties = new Properties();
        setFileProperties(sdkRequestHead,fileProperties);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            //保存完成后，去除前面的curr_idx=X的部分
            cfgWriter.seek(0);
            if (fileProperties != null) {
                // 保存到文件中
                fileProperties.store(bos, "file properties");
                cfgWriter.write(bos.toByteArray());
            }
        } catch (Exception e) {
            log.error("#flowNo:{}#保存文件的配置信息出错", sdkRequestHead.getRqsSrlNo(), e);
            throw new FtpException(FtpErrCode.SAVE_CONFIG_FILE_ERROR, sdkRequestHead.getRqsSrlNo(), 1l, e);
        } finally {
            try {
                cfgWriter.close();
            } catch (Exception e) {
                log.error("#flowNo:{}#", sdkRequestHead.getRqsSrlNo(), e);//NOSONAR
            }
        }

    }
    /**
     * 设置文件的相关信息
     *
     * @param sdkRequestHead 传入参数
     */
    public void setFileProperties(SDKRequestHead sdkRequestHead,Properties fileProperties) {

        Date now = new Date();
        setFilePropertie(fileProperties,"ClientIp", sdkRequestHead.getUid());
        setFilePropertie(fileProperties,"FileName", sdkRequestHead.getFileName());
        setFilePropertie(fileProperties,"ClientFileName", "");
        setFilePropertie(fileProperties,"User", sdkRequestHead.getUid());
        setFilePropertie(fileProperties,"CreateTime", DateFormatUtils.format(now, ContextConstants.DATE_FORMAT_PATT));
        setFilePropertie(fileProperties,"FileSize", Long.toString(sdkRequestHead.getFileSize()));
        setFilePropertie(fileProperties,"version", Long.toString(now.getTime()));
        setFilePropertie(fileProperties,"tmpFile", "");
        setFilePropertie(fileProperties,"targetSysname", "");
        setFilePropertie(fileProperties,"targetFileName", "");
        setFilePropertie(fileProperties,CLIENT_FILE_MD5, "");
        //setFilePropertie("orgiFileName", reqDto.getFileName());//NOSONAR
    }
    public void setFilePropertie(Properties fileProperties,String key, String value) {
        fileProperties.setProperty(key, value == null ? "" : value);
    }

    /**
     * 重命名源文件
     * @param newRnmFile
     */
    public void renameOrigFile(File newRnmFile) {
        if (newRnmFile.exists()) {
            File orgNewFile = new File(newRnmFile.getParent(),renameByUUID(newRnmFile.getName()));
            boolean rename = newRnmFile.renameTo(orgNewFile);
            log.info("存在同名文件，旧文件改名为{}，rename{}",orgNewFile.getName(),rename);
            File newRnmFileCfg = new File(newRnmFile+ SvrGlobalCons.DCFS_CFG_FILE_EXT);
            if (newRnmFileCfg.exists()) {
                File orgNewFileCfg =  new File(orgNewFile+ SvrGlobalCons.DCFS_CFG_FILE_EXT);
                boolean renameCfg = newRnmFileCfg.renameTo(orgNewFileCfg);
                log.info("存在同名文件，旧文件改名为{}，rename{}",orgNewFileCfg.getName(),renameCfg);
            }
        }

    }

}
