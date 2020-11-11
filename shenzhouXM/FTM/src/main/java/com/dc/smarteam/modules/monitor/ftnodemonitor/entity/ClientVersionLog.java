package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by liwjx on 2017/9/11.
 * clientIp	客户端IP
 * uid	用户名
 * clientVersion	客户端版本号
 * beginDate	开始时间
 * endDate	结束时间
 */
public class ClientVersionLog extends DataEntity<ClientVersionLog>{
    private String clientIp;        //客户端IP
    private Date startTime;         //开始时间
    private String uid;             //用户名
    private String clientVersion;   //客户端版本号
    private String port;            //端口号
    private Date createdTime;       //该版本最近记录时间
    private Date beginDate;         //结束时间

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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}
