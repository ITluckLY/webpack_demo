package com.dcfs.esb.ftp.server.system;

/**
 * Created by mocg on 2017/6/26.
 */
public class FileRouteArgs {
    private String tranCode;
    private String svrFilePath;
    /*上传用户ID*/
    private String uploadUid;
    /*客户端接收消息与下载文件同步*/
    private boolean sync;
    private long nano;
    /*消息标识*/
    private long msgId;
    /*流水号*/
    private String flowNo;

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getSvrFilePath() {
        return svrFilePath;
    }

    public void setSvrFilePath(String svrFilePath) {
        this.svrFilePath = svrFilePath;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
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

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
