package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/26.
 */
public class TranCodeMonitor extends DataEntity<TranCodeMonitor> {
    private String tranCode;
    private Date uploadTime;
    private Date downloadTime;
    private String describe;
    private String oriFilename;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(Date downloadTime) {
        this.downloadTime = downloadTime;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getOriFilename() {
        return oriFilename;
    }

    public void setOriFilename(String oriFilename) {
        this.oriFilename = oriFilename;
    }


}
