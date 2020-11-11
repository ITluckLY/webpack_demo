/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import java.util.Date;

/**
 * 文件管理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class ClientMonitorLog extends DataEntity<ClientMonitorLog> {

    private static final long serialVersionUID = 1L;
    private String id;
    private String tranCode;        // 文件名称
    private String flowNo;        // 上传节点组
    private String fromUid;        // 文件大小
    private String toUid;        // 文件路径
    private String fileName;
    private Date startTime;
    private Date endTime;
    private String errMsg;
    private String state;
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

    public ClientMonitorLog() {
        super();
    }

    public ClientMonitorLog(String id) {
        super(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}