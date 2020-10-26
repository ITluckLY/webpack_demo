package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

/**
 * Created by xuchuang on 2018/6/8.
 * 交易码下载超时设置
 */
public class FtTranCodeAlarmLine extends DataEntity<FtTranCodeAlarmLine> {

    private String tranCode;
    private String putUser;
    private String getUser;
    private String fileName;
    private int timeout;

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getPutUser() {
        return putUser;
    }

    public void setPutUser(String putUser) {
        this.putUser = putUser;
    }

    public String getGetUser() {
        return getUser;
    }

    public void setGetUser(String getUser) {
        this.getUser = getUser;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "FtTranCodeAlarmLine{" +
                "tranCode='" + tranCode + '\'' +
                ", putUser='" + putUser + '\'' +
                ", getUser='" + getUser + '\'' +
                ", fileName='" + fileName + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
