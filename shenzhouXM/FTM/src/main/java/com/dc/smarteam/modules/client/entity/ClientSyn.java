package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 *
 * 客户端概况
 * liuyfal  20180327
 *
 */
public class ClientSyn extends DataEntity<ClientSyn> {

    private static final long serialVersionUID = 30100136181910980L;
    private String status;
    private String name;
    private String ip;
    private String userDomainName;
    private String port;
    private String cmdPort;
    private String username;
    private String password;
    private String sysname;
    private String clientVersion;
    private Date startTime;
    private Date endTime;
    private Date createdTime;
    private Date modifiedTime;
    private String connectServer;
    private String errCode;
    private String errMsg;
    private int rebootTimes;
    private Date beginDate;
    private Date endDate;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
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

    public String getConnectServer() {
        return connectServer;
    }

    public void setConnectServer(String connectServer) {
        this.connectServer = connectServer;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public int getRebootTimes() {
        return rebootTimes;
    }

    public void setRebootTimes(int rebootTimes) {
        this.rebootTimes = rebootTimes;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCmdPort() {
        return cmdPort;
    }

    public void setCmdPort(String cmdPort) {
        this.cmdPort = cmdPort;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUserDomainName() {
        return userDomainName;
    }

    public void setUserDomainName(String userDomainName) {
        this.userDomainName = userDomainName;
    }

    @Override
    public String toString() {
        return "ClientSyn{" +
                "status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", userDomainName='" + userDomainName + '\'' +
                ", port='" + port + '\'' +
                ", cmdPort='" + cmdPort + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +//NOSONAR
                ", sysname='" + sysname + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                ", connectServer='" + connectServer + '\'' +
                ", errCode='" + errCode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", rebootTimes=" + rebootTimes +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                '}';
    }
}
