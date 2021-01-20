package com.dcfs.esc.ftp.datanode.http.httpopfactory;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.key.Key;
import com.dcfs.esb.ftp.key.KeyManager;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;

import com.dcfs.esb.ftp.server.service.GetAuthUser;
import com.dcfs.esb.ftp.server.service.PutAuthUser;
import com.dcfs.esb.ftp.server.service.ServiceContainer;

import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.comm.scrt.security.Encrypter;
import com.dcfs.esc.ftp.comm.scrt.security.aes.AESKey;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSAKeyPair;
import com.dcfs.esc.ftp.comm.scrt.security.util.Securitier;
import com.dcfs.esc.ftp.datanode.helper.HttpHelper;
import com.dcfs.esc.ftp.datanode.http.ITransferOp;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/12/20.
 */
public class DownloadFileWithPost implements ITransferOp {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private String userIP;
    private byte[] body;
    public DownloadFileWithPost(String userIP,byte[] body) {
        this.userIP = userIP;
        this.body = body;
    }

    @Override
    public String doTransferFile(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {

        SDKRequestHead sdkRequestHead = new SDKRequestHead(httpRequest);//获取请求头参数
        sdkRequestHead.setUserIp(userIP);
        log.info("流水号{},客户端地址{}",sdkRequestHead.getRqsSrlNo(),userIP);
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

        try {

            downloadFile(ctx, sdkRequestHead);

        } catch (IOException e) {
            log.error("下载文件IO异常",e);

        } catch (FtpException e) {
            log.error("下载文件异常",e);
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
    public void downloadFile(ChannelHandlerContext ctx, SDKRequestHead sdkRequestHead) throws IOException, FtpException {

        String result = "开始下载文件分片";
        sdkRequestHead.setStartTime(new Date());//开始时间

        String realFileName = HttpHelper.getRealDown(sdkRequestHead);
        File downFile = new File(realFileName);
        openForRead(ctx, downFile, sdkRequestHead);
        result = "读取分片偏移量[" + sdkRequestHead.getOffset() + "]完成";
        sdkRequestHead.setRetCode("0000");
        sdkRequestHead.setRetMsg(result);
        log.info("flowNo{},{}",sdkRequestHead.getRqsSrlNo(),result);

    }
    /**
     * 读取文件，在服务端用于处理文件的下载，在客户端用于处理文件的下载。
     *
     * @throws FtpException
     */
    public void openForRead(ChannelHandlerContext ctx, File realFile, SDKRequestHead sdkRequestHead) throws FtpException {

    	sdkRequestHead.setRetCode("0000");
    	sdkRequestHead.setRetMsg("beginRead");
        // 目录不存在，则创建相关目录
        if (!realFile.exists()) {
            sdkRequestHead.setRetCode("9999");
            sdkRequestHead.setRetMsg(FtpErrCode.FILE_NOT_FOUND_ERROR);
            sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
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
                sdkRequestHead.setFileSize(size);
                sdkRequestHead.setSucc(true);
                HttpHelper.saveDownloadLog(sdkRequestHead);
            } else {
                sdkRequestHead.setLastPiece(false);
            }
            byte[] tmp = new byte[hasRead];
            if (hasRead < buff.length) {
                System.arraycopy(buff, 0, tmp, 0, tmp.length);
            } else {
                tmp = buff;
            }
            if (sdkRequestHead.isSign()) {
                //Map<String, String> dataMap = doEncSign(tmp);
                Map<String, String> dataMap = doEncSign(tmp,sdkRequestHead.getUid());


                sendResp(ctx, dataMap, sdkRequestHead, HttpResponseStatus.OK);
            } else {
                if (sdkRequestHead.isMd5()){
                    //进行单个分片的MD5计算
                    sdkRequestHead.setSign(false);
                    sdkRequestHead.setMd5(Encrypter.md5(tmp));
                }
                String context = new String(tmp,CharsetUtil.UTF_8);
                sendRespContext(ctx, context, sdkRequestHead, HttpResponseStatus.OK);
            }



        } catch (Exception e) {
            log.error("nano:#flowNo:#打开文件出错", e);
            sdkRequestHead.setRetCode("9999");
            sdkRequestHead.setRetMsg(FtpErrCode.FILE_NOT_FOUND_ERROR);
            sendSdkRsp(ctx, sdkRequestHead, HttpResponseStatus.OK);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

    }
    /**
     * Put-加密加签
     *
     * @param buffer
     * @return
     */
    public static Map<String, String> doEncSign(byte[] buffer,String uid) {
        PrivateKey priB;
        PublicKey pubA;
        Map<String, String> dataMap = null;

        RSAKeyPair cRsa = new RSAKeyPair();
        AESKey aes = new AESKey();
        aes.generator();
        try {
            //priB = RSAKeyPair.getPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCj6FEYXIP4kgSYzP5NeW+V+E5YoSWmUmB6/+ymuwREod7KC7KXygDUJnldsC/F40R3/r5Dj6pv9Qi83OriNCIAeib2p5Z1aN0OkhPGPi7tanMbElu37wjQHSLZlqU/3PflF/vnQHbZ4fgzjhOSpOJPVrviLqTJ2gCuRkjBXyZ8wsHvqDEdX+wUE1Z3n6Y+o5sdr8OASzxtO6JHCnFUnV1KOQBstNkqF5M/2Fnsg9Je0awPICJ8MZFyLe/tGWXg3fH3AVWoAiLCNSfSWlwq702c5xHWUT7j1EyL8ZL8txlUCXLbkeHJmGzpdnH51LCS8EdMIUPz3VgwgnLzc8Q/qUvLAgMBAAECggEBAIpDJ11IdV6SNeR7T60k2dcFDXm//dVuOcqn5gXDTldiwF9pPK7EDKzpA4nfXH0uOAyMzAyLvPcSGNvP8yb7WQ9T+1gniEjkO0zWNm4M+GL3X7+fXdUrgyCi40nuxNi5WjdbYvfwrhEfh9Jdb+9MvUa14GlsdT98cQSRb0AHNp0X/2vZ93wmk4Faew7rRyEi775Z6O8dKbxyifWt+FLTIBSXFCS8lS/GEdGq37VsKk71MdrOivP1zkRYyrQyEG84AoPpLeKzNKZCzy3YNSGXZOLQDB1udrrZ76OZuFQqsfzwVj0zErvFFufXDmYSV8eLWKY7F7e0ifyP0appmLrHRKkCgYEA/K0MG1OJcsEstyMpAs8yCpC2Muz4+wC8X2YZ+Dh3M1D4gw4eqAUOCXrIuRPb9S9s/Kt4GS+BcXZbLRG/7850P/pe6Z1gH+wod7Qh1rAXki9YwLtFks3b8M7kCT1c1zAyvIGtFx6t0Qpdq0soqWy+agiYq4X4o6kNzRQN4JSFekUCgYEAphBRfMB88yiKA+9gv7CnMF1dCS/irKnufA2aWwjN8v/DEDXdjAx0ysgZqB17kCvMKyxKKKzLh0on/6FkeFuyUe6mrujf3wis0h9+EM4iDF84Mx6ZbCEnpkzBnPcpcMvSNw4KhegmNS0l0KphaMK4w/fYfnv6lQagFnCQr46rls8CgYBjrei+xv/MM4TuYoKFRzPYkyotgOrgKnQltmO8Vpo/tkuzd9iENCpLdxLEYJ8/ZIw8SXDBjsoj9qsVZpEvi2S08JKM2rbXIRT91CQdS66gzujWb4qM1YVUxGmVc42ynqMFVqrwGfw8ITi+oJHT9MBRmD6SO/HQrppxMt4eoLjfeQKBgB9o/sDF99xWUqSj5nzbgQQY5LwUHp/iFIXKXOPTKoFH9zFdvuX6hSzF5HRz/ycp4ZhY7D39URHp6N7lPAe38Gam6Ug0LAQXs/ialFHBa5dDe3HP290j+EfwRL7X0TLQmpZnRX5GhtOQEpMBZyNVkXUfsAdck+0UL7uO1w8dwT7pAoGBAM8WACknQ6a65Db1YCFq0bUag/bR4yYP2KY2taJ4n4Cm9VEp3tXR2pAYlpsz4JCz6hz1ww91DuMxYIfOvLYDAlfy4I8CJgiudp2bB0tYy9bDyrWmjmLLhTOjOvRvlAc/7ZehXgKon2GU48WKFsvTd0620zQ3U1SkPqnNs2MsQAh1");
            //pubA = RSAKeyPair.getPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhkPYAAAToyfWlTzSN2Fe069NgH0fjfO+yGlkHeUMOmfA4rvN1TCVQ/f5HmbFMocM3nd90YId7aOQcqqMiI+mS9Y0OEjgmUju9IaFIhLqAYp6blLF9CjjSWW6PQ9ow9NtoIgZCiHxIFm7rxnyGh3YUwHTnATqI92X72VKQm9lVHkRx2ZQT2c2Ptz45WNUMxN6IRtXGWTOpGJJykDVt9ZAJB4nx2+lEsDqUDeyXfJoJ+casw4+NhSHpwUJpJ2FQAL2cCNIgj5eCqxBbe9buE8r7QW3Uxj+iBh0BEUTWYwMOihtTBqTFQUQ43P+mxFFXBZ3kR06Hi40/MkOVP0ww4QNWQIDAQAB");




            Key pubKeyA = KeyManager.getInstance().serachKey(uid, KeyManager.SYSTEM_P);
            String userPubA = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxH2aTjVO6Owk8yyi75U5/XLOPhEp4dArekEl0jyEey6oIX+thZI2nAk1rBIWvqiqVqB5TYfi16c2nFJGHg7M9fLfHLVlLMb9olNEy7EcefYRoc0ZZMXTF2slqLS1MNqnPR3ZaF5nQ3PzrcfUO6AQLQ8skkqEdEONcrHNNJewaEAL/zgu7ZduJKRe8XFTrTMkd9F+Y1fHsNVJbTVsYsTtJZuNKotrgrhlc2HufRU1TWKV43iJy/ZlM5SXAt7ML1alrd5awGrsl3ctyLqqU6qrEu1EraE4Do/h9dNA4u7MMxkhDFjReaaySj4eyWlFOUDEX9Xw4HHHdtiYkiTWAxH1kwIDAQAB";
            if (null != pubKeyA && pubKeyA.getContent() != null) {
                userPubA = pubKeyA.getContent();
            }
            pubA = RSAKeyPair.getPublicKey(userPubA);

            Key priKeyB = KeyManager.getInstance().serachKey("open", KeyManager.SYSTEM_S);
            String platformPriB = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDLPCDOatsxQA0/RzVWu5ve0jRXIq8hKETknAYBdgOiVJjJA7DBaGoiEp3e2gDfPsa7PotsxR997AimXJ06YubkADkOvQmDzIr42LRqcwBJ0s0xMKqLdjyjp40IpKJI+uVleQcm7me/eN/wuM0JwECpmISGhux1N9Pm/vBn7wXsgS0btmgDuByysfiogG3Bu5oFeuWd5qibnyNiO7YKkaSKuF90Nzwwl3dJnjyrE64GtTV70fGoQ+ExPgpvy65H9ezEvNiN39PhJrKbv+ILycD0CKEQlSyg8/AtdS89bS+1AotAJGMhZh3i/6dxOi9o8J6e3Ha/ABbfkqJt+BdnjPYVAgMBAAECggEAbRCNgnixvJJlCYpLQ4pWsZMY55IDbcXQguaACpSsipbfjzBd0M0Z98MJ2aggQ63KtH1yESMQLWFXmv+kmMquobjE5fXof0RrZ/PTjjS8+OEZoMY/H6yKCkFGXFcNiCCsrnHutqPGJWjWtLZXwl3b1ae1pwQNLTKSkypJFV2eTjZnexTbQeTMKIajk1zyNDhV5sHvdlh57Mk1twNQAEbPpGHbZZRhmNRU/RM0MRErAWilG+sGfv9yolMJTsVmHTKZ/3dhqTydePTFkdxVbMaauesWXkIl/vgpCvYX5fm/CXKZGYiTKdK/hkkrUvXrwVFBapLZwsXCsDlLpP4PUiIrGQKBgQDqAD9kCatvQIO0Zerczckf9MMuEuDrMRLwRdUQveTxWead+Lb3hQSm4h+pJCVMslbsDzIEbyksUcKBkTjvLxe85lurbCfdUPwaxcWmwmpv2UsH7YLiWPedo/v9uVKSBJMm7pA/1obZZXJf2PNMW6y6qh7Rvh8PGMG2bnIW2KYHMwKBgQDeV20g1uvGwKRNEU1VUL/m5FT4KdHtL5g4hlwzSQs1z0kLZZQaH5IwfZAQy6YhBfZTSlrCXVMe9TDTjXsVYJMh6/dy6uf/uSFjgqJowd4XHd810D99j3auX9gedZPrVQdqZyQnWek62HmolA+Epk38/y1FlFMUoZ7yGIV9iTptlwKBgQCQSNrNelDk5mjPYVByGcl8FNYMoMNjAf1lfgmkqxJx5zcWDPf0o2Z86E0RZCJDqmuCXtQ+e7RtiXRfpAXxJZ6DcZ7wkl36TydD8tKuwo4SrW9gUgk0PBtibmzAw/av5nY9uTfkxQidH7poc0OBv70/1Anw1kQccO1w6aUbhTbKlQKBgEjmgXFzBnt0Tmq2cZS3lTQvSWzUyW/rWw2tgC2QlYxfGqQR+i1WjKg5gzL+ksnhGpLUnk18K/TKj+fNS74GnDdTFx8mxkqieMAX5QhUCG2r7bPHRAn3MAKcik/yXT2b84J/rur4H/CbachMQ4pKvOdnA4D80QDCvUrDn4E5rV6hAoGAWygmfGLWwaZe2x8q2eLCnsoeXch4Y4rbpW/jSJmW1yq9532d5J3BGJ20oL203ubJuX5SamzeSrdD1WtR+LlDzfuxxLSUVAdYIVO5tNlFuo1XIGIKMmXEPLH+1tRQnfnPNsaNzDgLi4i25yM34vNXsp3g2GYdeeQNJNwMHumJX8k=";
            if (null != priKeyB && priKeyB.getContent() != null) {
                platformPriB = priKeyB.getContent();
            }
            priB = RSAKeyPair.getPrivateKey(platformPriB);


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
            priB = RSAKeyPair.getPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDLPCDOatsxQA0/RzVWu5ve0jRXIq8hKETknAYBdgOiVJjJA7DBaGoiEp3e2gDfPsa7PotsxR997AimXJ06YubkADkOvQmDzIr42LRqcwBJ0s0xMKqLdjyjp40IpKJI+uVleQcm7me/eN/wuM0JwECpmISGhux1N9Pm/vBn7wXsgS0btmgDuByysfiogG3Bu5oFeuWd5qibnyNiO7YKkaSKuF90Nzwwl3dJnjyrE64GtTV70fGoQ+ExPgpvy65H9ezEvNiN39PhJrKbv+ILycD0CKEQlSyg8/AtdS89bS+1AotAJGMhZh3i/6dxOi9o8J6e3Ha/ABbfkqJt+BdnjPYVAgMBAAECggEAbRCNgnixvJJlCYpLQ4pWsZMY55IDbcXQguaACpSsipbfjzBd0M0Z98MJ2aggQ63KtH1yESMQLWFXmv+kmMquobjE5fXof0RrZ/PTjjS8+OEZoMY/H6yKCkFGXFcNiCCsrnHutqPGJWjWtLZXwl3b1ae1pwQNLTKSkypJFV2eTjZnexTbQeTMKIajk1zyNDhV5sHvdlh57Mk1twNQAEbPpGHbZZRhmNRU/RM0MRErAWilG+sGfv9yolMJTsVmHTKZ/3dhqTydePTFkdxVbMaauesWXkIl/vgpCvYX5fm/CXKZGYiTKdK/hkkrUvXrwVFBapLZwsXCsDlLpP4PUiIrGQKBgQDqAD9kCatvQIO0Zerczckf9MMuEuDrMRLwRdUQveTxWead+Lb3hQSm4h+pJCVMslbsDzIEbyksUcKBkTjvLxe85lurbCfdUPwaxcWmwmpv2UsH7YLiWPedo/v9uVKSBJMm7pA/1obZZXJf2PNMW6y6qh7Rvh8PGMG2bnIW2KYHMwKBgQDeV20g1uvGwKRNEU1VUL/m5FT4KdHtL5g4hlwzSQs1z0kLZZQaH5IwfZAQy6YhBfZTSlrCXVMe9TDTjXsVYJMh6/dy6uf/uSFjgqJowd4XHd810D99j3auX9gedZPrVQdqZyQnWek62HmolA+Epk38/y1FlFMUoZ7yGIV9iTptlwKBgQCQSNrNelDk5mjPYVByGcl8FNYMoMNjAf1lfgmkqxJx5zcWDPf0o2Z86E0RZCJDqmuCXtQ+e7RtiXRfpAXxJZ6DcZ7wkl36TydD8tKuwo4SrW9gUgk0PBtibmzAw/av5nY9uTfkxQidH7poc0OBv70/1Anw1kQccO1w6aUbhTbKlQKBgEjmgXFzBnt0Tmq2cZS3lTQvSWzUyW/rWw2tgC2QlYxfGqQR+i1WjKg5gzL+ksnhGpLUnk18K/TKj+fNS74GnDdTFx8mxkqieMAX5QhUCG2r7bPHRAn3MAKcik/yXT2b84J/rur4H/CbachMQ4pKvOdnA4D80QDCvUrDn4E5rV6hAoGAWygmfGLWwaZe2x8q2eLCnsoeXch4Y4rbpW/jSJmW1yq9532d5J3BGJ20oL203ubJuX5SamzeSrdD1WtR+LlDzfuxxLSUVAdYIVO5tNlFuo1XIGIKMmXEPLH+1tRQnfnPNsaNzDgLi4i25yM34vNXsp3g2GYdeeQNJNwMHumJX8k=");
            pubA = RSAKeyPair.getPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxH2aTjVO6Owk8yyi75U5/XLOPhEp4dArekEl0jyEey6oIX+thZI2nAk1rBIWvqiqVqB5TYfi16c2nFJGHg7M9fLfHLVlLMb9olNEy7EcefYRoc0ZZMXTF2slqLS1MNqnPR3ZaF5nQ3PzrcfUO6AQLQ8skkqEdEONcrHNNJewaEAL/zgu7ZduJKRe8XFTrTMkd9F+Y1fHsNVJbTVsYsTtJZuNKotrgrhlc2HufRU1TWKV43iJy/ZlM5SXAt7ML1alrd5awGrsl3ctyLqqU6qrEu1EraE4Do/h9dNA4u7MMxkhDFjReaaySj4eyWlFOUDEX9Xw4HHHdtiYkiTWAxH1kwIDAQAB");


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
     * 发送的返回值
     *
     * @param ctx    返回
     * @param status 状态
     */
    public static void sendResp(ChannelHandlerContext ctx, Map<String, String> dataMap, SDKRequestHead sdkRequestHead, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(dataMap.get("msg"), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        response.headers().set("key", dataMap.get("key"));
        response.headers().set("sign", dataMap.get("sign"));
        response.headers().set("RqsSrlNo", sdkRequestHead.getRqsSrlNo());
        response.headers().set("offset", sdkRequestHead.getOffset());
        response.headers().set("lastPiece", sdkRequestHead.isLastPiece());
        response.headers().set("fileSize", sdkRequestHead.getFileSize());
        response.headers().set("retcode",sdkRequestHead.getRetCode());
        response.headers().set("retmsg",sdkRequestHead.getRetMsg());

//        response.headers().set("md5",sdkRequestHead.getMd5());


        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    /**
     * 发送的返回值
     *
     * @param ctx    返回
     * @param status 状态
     */
    public static void sendRespContext(ChannelHandlerContext ctx, String  context, SDKRequestHead sdkRequestHead, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");


        response.headers().set("RqsSrlNo", sdkRequestHead.getRqsSrlNo());
        response.headers().set("offset", sdkRequestHead.getOffset());
        response.headers().set("lastPiece", sdkRequestHead.isLastPiece());
        response.headers().set("fileSize", sdkRequestHead.getFileSize());
        response.headers().set("retcode",sdkRequestHead.getRetCode());
        response.headers().set("retmsg",sdkRequestHead.getRetMsg());
        if (sdkRequestHead.isMd5()) {
            response.headers().set("md5",sdkRequestHead.getMd5());
            response.headers().set("isMd5",sdkRequestHead.isMd5());
        }
        response.headers().set("isSign",sdkRequestHead.isSign());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
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
}
