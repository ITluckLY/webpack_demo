package com.dcfs.esc.ftp.comm.scrt.bean;


import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.IOException;
import java.util.*;

/**
 * 由SDK自动填充
 * 用于http header
 *
 * @author zhudp
 */
//TODO 根据使用场景看，有些set方法可以去掉
public class SDKRequestHead {
    public SDKRequestHead sdkRequestHead = null;

    public SDKRequestHead(FullHttpRequest request) {
        getHeaderInit(request);
    }

    public SDKRequestHead(String uri) {
        getHeaderByUriInit(uri);
    }

    public SDKRequestHead() {

    }

    /**
     * uid
     */
    private String uid;
    /**
     * passwdId
     */
    private String passwdId;
    /**
     * key
     */
    private String key;
    /**
     * sign
     */
    private String sign;
    /**
     * 文件总大小
     */
    private long fileSize;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 偏移量
     */
    private long offset;
    /**
     * 请求流水号
     */
    private String rqsSrlNo;
    /**
     * 系统
     */
    private String systemGroup;
    /**
     * 交易码
     */
    private String tranCode;
    private String servFileName;
    /**
     * 是否最后一个分片
     */
    private boolean lastPiece;

    private String md5;
    private boolean isSign;
    private boolean isMd5;
    private boolean isSDK;
    private int pieceNum;
    /**
     * 规则”Basic “ + Base64.getEncoder().encode(userid + ":" + passid) Basic后面有个空格
     */
    private String authorization;

    private String retCode;
    private String userIp;
    private boolean succ;
    private Date startTime;
    private Date endTime;
    private String retMsg;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }


    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isLastPiece() {
        return lastPiece;
    }

    public void setLastPiece(boolean lastPiece) {
        this.lastPiece = lastPiece;
    }


    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
    }

    public boolean isMd5() {
        return isMd5;
    }

    public void setMd5(boolean md5) {
        isMd5 = md5;
    }

    public boolean isSDK() {
        return isSDK;
    }

    public void setSDK(boolean SDK) {
        isSDK = SDK;
    }

    public int getPieceNum() {
        return pieceNum;
    }

    public void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPasswdId() {
        return passwdId;
    }

    public void setPasswdId(String passwdId) {
        this.passwdId = passwdId;
    }

    public String getRqsSrlNo() {
        return rqsSrlNo;
    }

    public void setRqsSrlNo(String rqsSrlNo) {
        this.rqsSrlNo = rqsSrlNo;
    }

    public String getSystemGroup() {
        return systemGroup;
    }

    public void setSystemGroup(String systemGroup) {
        this.systemGroup = systemGroup;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {

        this.authorization = authorization;
    }

    public void setDesAuthorization(String authorization) {
        this.authorization = authorization;
        // BASE64解密
        String enAuthorization = authorization.substring(6);
//        System.out.println("enAuthorization:"+enAuthorization);
        byte[] bytes = new byte[0];
        try {
            bytes = Base64.getDecoder().decode(enAuthorization);
            String deAuthorization = new String(bytes, "UTF-8");
            String[] splitStr = deAuthorization.split(":", 2);
            String userId = splitStr[0];
            String passwd = splitStr[1];
            this.uid = userId;
            this.passwdId = passwd;


//            System.out.println("BASE64解密：" + deAuthorization);
//            System.out.println("userId：" + uid);
//            System.out.println("passwdId：" + passwdId);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
     * @param request
     * @return
     */
    private void getHeaderInit(FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        List<Map.Entry<String, String>> headerMap = headers.entries();
        for (Map.Entry<String, String> header : headerMap) {
            if (header.getKey().equalsIgnoreCase("key")) {
                this.key = header.getValue();
                continue;
            }

            if (header.getKey().equalsIgnoreCase("offset")) {
                this.offset = Long.parseLong(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("sign")) {
                this.sign = header.getValue();
                continue;
            }

            if (header.getKey().equalsIgnoreCase("uid")) {
                this.uid = header.getValue();
                continue;
            }

            if (header.getKey().equalsIgnoreCase("passwdid")) {
                this.passwdId = header.getValue();
                continue;
            }

            if (header.getKey().equalsIgnoreCase("fileSize")) {
                this.fileSize = Long.parseLong(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("RqsSrlNo")) {
                this.rqsSrlNo = header.getValue();
                continue;

            }

            if (header.getKey().equalsIgnoreCase("System")) {
                this.systemGroup = header.getValue();
                continue;
            }

            if (header.getKey().equalsIgnoreCase("fileName")) {
                this.fileName = header.getValue();
                continue;
            }

            if (header.getKey().equalsIgnoreCase("tranCode")) {
                this.tranCode = header.getValue();
                continue;

            }
            if (header.getKey().equalsIgnoreCase("lastPiece")) {
                this.lastPiece = Boolean.valueOf(header.getValue());
                continue;

            }
            if (header.getKey().equalsIgnoreCase("Authorization")) {

                this.setDesAuthorization(header.getValue());
                continue;

            }
            if (header.getKey().equalsIgnoreCase("isSign")) {
                this.isSign = Boolean.valueOf(header.getValue());
                continue;

            }
            if (header.getKey().equalsIgnoreCase("isMd5")) {
                this.isMd5 = Boolean.valueOf(header.getValue());
                continue;

            }
            if (header.getKey().equalsIgnoreCase("isSDK")) {
                this.isSDK = Boolean.valueOf(header.getValue());
                continue;

            }
            if (header.getKey().equalsIgnoreCase("md5")) {
                this.md5 = header.getValue();
                continue;

            }

        }
    }

    /**
     * 获取body参数
     *
     * @return
     */
    private SDKRequestHead getHeaderByUri(String uri) {
        SDKRequestHead sdkRequestHead = new SDKRequestHead();
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
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
     * 获取body参数
     *
     * @return
     */
    private void getHeaderByUriInit(String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parame = decoder.parameters();
        Iterator<Map.Entry<String, List<String>>> iterator = parame.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> next = iterator.next();

            if (next.getKey().equalsIgnoreCase("userid")) {
                this.uid = next.getValue().get(0);
                continue;
            }

            if (next.getKey().equalsIgnoreCase("passid")) {
                this.passwdId = next.getValue().get(0);
                continue;
            }

            if (next.getKey().equalsIgnoreCase("RqsSrlNo")) {
                this.rqsSrlNo = next.getValue().get(0);
                continue;

            }

            if (next.getKey().equalsIgnoreCase("fileName")) {
                this.fileName = next.getValue().get(0);

                continue;
            }

            if (next.getKey().equalsIgnoreCase("tranCode")) {
                this.tranCode = next.getValue().get(0);
                continue;

            }

        }

    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }


    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getServFileName() {
        return servFileName;
    }

    public void setServFileName(String servFileName) {
        this.servFileName = servFileName;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }
}
