package com.dcfs.esc.ftp.datanode.context;

import com.dcfs.esb.ftp.common.error.FtpErrCode;

import java.util.Date;

/**
 * Created by mocg on 2017/6/3.
 * 客户端信息
 */
public abstract class ContextBean {
    protected long nano;
    /*以客户端方式*/
    private boolean byClient;
    /*客户端api版本*/
    private String apiVersion;
    private String userIp;
    /* 用户ID */
    protected String uid;
    /* 用户 密码 */
    protected String passwd;
    /* 系统名称 */
    protected String sysname;
    /* 文件服务端的文件名称,客户端传给的，与svrFilePath(服务端生成的)可能不同  */
    protected String fileName;
    /* 客户端本地的文件名称  */
    protected String clientFileName;
    protected boolean auth;

    private final Date startTime = new Date();
    private Date endTime;

    private String errCode;
    private String errMsg;
    /*流水号*/
    private String flowNo;

    public abstract void clean();

    public final void errCode(String errCode) {
        this.errCode = errCode;
        this.errMsg = FtpErrCode.getCodeMsg(errCode);
    }

    //getter setter

    public final long getNano() {
        return nano;
    }

    public final void setNano(long nano) {
        this.nano = nano;
    }

    public boolean isByClient() {
        return byClient;
    }

    public void setByClient(boolean byClient) {
        this.byClient = byClient;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public final String getErrCode() {
        return errCode;
    }

    public final void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public final String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
