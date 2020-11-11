package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/8.
 */
public class FtNodeMonitorDistribute extends DataEntity<FtNodeMonitorDistribute> {

    private Date createdTime;          //创建时间
    private Date modifiedTime;         //修改时间
    private String fileName;            //文件名
    private String fileVersion;         //文件版本
    private String nodeNamels;            //节点名
    private Date optTime;               //操作时间
    private String realFileName;        //真实文件名
    private String sysnamels;             //节点组
    private String state;                  //状态
    private String oriFilename;         //原始文件名
    private String tranCode;               //交易码
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

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getNodeNamels() {
        return nodeNamels;
    }

    public void setNodeNamels(String nodeNamels) {
        this.nodeNamels = nodeNamels;
    }

    public Date getOptTime() {
        return optTime;
    }

    public void setOptTime(Date optTime) {
        this.optTime = optTime;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public String getSysnamels() {
        return sysnamels;
    }

    public void setSysnamels(String sysnamels) {
        this.sysnamels = sysnamels;
    }

    public String getState() {return state;}

    public void setState(String state) {this.state = state;}

    public String getOriFilename() {
        return oriFilename;
    }

    public void setOriFilename(String oriFilename) {
        this.oriFilename = oriFilename;
    }
}
