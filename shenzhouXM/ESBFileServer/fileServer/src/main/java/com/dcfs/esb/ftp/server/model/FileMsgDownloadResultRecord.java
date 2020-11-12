package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;

/**
 * Created by hudja
 */
public class FileMsgDownloadResultRecord implements Serializable {
    /* 客户端IP */
    private String clientIp = null;
    /* 文件服务器IP地址  */
    private String serverIp = null;
    private String toUid;
    private String tranCode;
    private String sysname;
    //服务器上的文件路径(平台内绝对)
    private String serverFileName;
    //客户端上的文件路径（以配置路径为根路径）
    private String clientFileName;
    /*流水号,由生产方客户端生成*/
    private String flowNo;
    private String errCode;
    private String errMsg;
    /* 文件服务器的名称  */
    private String serverName;
    //返回结果成功失败标志
    private Boolean resultsucc;

    private long nano;
    private long msgId;

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getServerFileName() {
        return serverFileName;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public Boolean getResultsucc() {
        return resultsucc;
    }

    public void setResultsucc(Boolean resultsucc) {
        this.resultsucc = resultsucc;
    }
}
