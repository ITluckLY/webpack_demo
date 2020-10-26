package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by Administrator on 2019/9/4.
 */
public class Archive extends DataEntity<Archive> {
    /* 客户端IP */
    private String clientIp = null;
    /* 文件服务器的文件名称  */
    private String fileName = null;
    /* 客户端本地的文件名称  */
    private String clientFileName = null;
    /*用户名称*/
    private String user = null;
    /*交易码 */
    private String tranCode = null;
    /*文件上传成功标识 */
    private String UploadFlag = null;
    /*文件删除成功标识 */
    private String clearFlag  = null;


    /*归档开始时间 */
    private String uploadStartTime = null;

    /*归档结束时间 */
    private String uploadEndTime = null;

//    private Date startTime;
//    private Date endTime;

    private Date createdTime;


    private Date beginDate;
    private Date endDate;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
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

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }
//
//    public Date getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(Date startTime) {
//        this.startTime = startTime;
//    }
//
//    public Date getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(Date endTime) {
//        this.endTime = endTime;
//    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUploadFlag() {
        return UploadFlag;
    }

    public void setUploadFlag(String uploadFlag) {
        UploadFlag = uploadFlag;
    }
    public String getUploadStartTime() {
        return uploadStartTime;
    }

    public void setUploadStartTime(String uploadStartTime) {
        this.uploadStartTime = uploadStartTime;
    }

    public String getUploadEndTime() {
        return uploadEndTime;
    }

    public void setUploadEndTime(String uploadEndTime) {
        this.uploadEndTime = uploadEndTime;
    }

    public String getClearFlag() {
        return clearFlag;
    }

    public void setClearFlag(String clearFlag) {
        this.clearFlag = clearFlag;
    }

}
