package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by chenhuae on 2016/9/7.
 */
public class FtNodeAlarmLogMonitor extends DataEntity<FtNodeAlarmLogMonitor> {

    private static final long serialVersionUID = 1L;
    private Date time;
    private String nodeNameTemp;
    private String message;
    private Date createTime;
    private String msgType;
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

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getNodeNameTemp() {
        return nodeNameTemp;
    }

    public void setNodeNameTemp(String nodeNameTemp) {
        this.nodeNameTemp = nodeNameTemp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
