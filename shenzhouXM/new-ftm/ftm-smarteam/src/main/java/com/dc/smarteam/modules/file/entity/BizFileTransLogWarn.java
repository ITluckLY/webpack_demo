package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BizFileTransLogWarn extends DataEntity<BizFileTransLogWarn> {
    private Long id;
    //流水号
    private String flowNo;
    //交易码
    private String tranCode;
    //上传或下载的用户
    private String operator;
    //记录进行的操作 1-上传 2-下载 3-重发 4-下载完成后客户端操作
    private String operateType;
    //上传或下载的用户IP
    private String operateIp;
    //服务端文件名
    private String fileName;
    //服务端压缩包文件列表
    private String zipFileList;
    //文件大小
    private Integer fileSize;
    //错误交易码
    private String errCode;
    //错误交易码
    private String errMsg;
    //传输节点的名称
    private String nodeName;
    //文件传输的开始时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    //文件传输结束的时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    //传输是否成功
    private boolean suss;
    //通知类型：下载成功0: 金融网关通知；下载失败1：短信通知；
    private int noticeType;
    //通知时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date noticeTime;
    //通知状态
    private boolean noticeStat;
    // //要通知的人
    private String noticeSendTo;
    //通知次数
    private int noticeNum;

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getOperateIp() {
        return operateIp;
    }

    public void setOperateIp(String operateIp) {
        this.operateIp = operateIp;
    }

    public String getZipFileList() {
        return zipFileList;
    }

    public void setZipFileList(String zipFileList) {
        this.zipFileList = zipFileList;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public int getNoticeNum() {
        return noticeNum;
    }

    public void setNoticeNum(int noticeNum) {
        this.noticeNum = noticeNum;
    }

    public String getNoticeSendTo() {
        return noticeSendTo;
    }

    public void setNoticeSendTo(String noticeSendTo) {
        this.noticeSendTo = noticeSendTo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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

    public Date getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(Date noticeTime) {
        this.noticeTime = noticeTime;
    }


    public boolean isSuss() {
        return suss;
    }

    public void setSuss(boolean suss) {
        this.suss = suss;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public boolean isNoticeStat() {
        return noticeStat;
    }

    public void setNoticeStat(boolean noticeStat) {
        this.noticeStat = noticeStat;
    }

}
