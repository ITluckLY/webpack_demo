package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mocg on 2016/7/21.
 */
public class NodeListGetRecord implements Serializable {
    /* 以客户端方式  */
    private boolean byClient;
    /* 客户端IP */
    private String clientIp;
    /* 文件服务器IP地址  */
    private String serverIp;
    /* 系统名称 */
    private String sysname;
    /* 用户ID */
    private String uid;
    private String apiVersion;
    private String serverApiVersion;
    private String clientNodelistVersion;
    private String serverNodelistVersion;
    /* 数据节点列表，格式：nodeName#ip:port,... eg:FS01#127.0.0.1:5001,FS02#127.0.0.2:5001 */
    private String nodeList;
    /* 认证标志  */
    private boolean auth;
    /*true:表示客户端启动时的请求*/
    private boolean byClientStart;

    private Date startTime;
    private Date endTime;
    private boolean suss;
    private String nodeName;
    private String tags;
    private String errCode;
    private String errMsg;
    private Long nano;
    /*客户端版本号*/
    private String clientVersion;

    public boolean isByClient() {
        return byClient;
    }

    public void setByClient(boolean byClient) {
        this.byClient = byClient;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getServerApiVersion() {
        return serverApiVersion;
    }

    public void setServerApiVersion(String serverApiVersion) {
        this.serverApiVersion = serverApiVersion;
    }

    public String getClientNodelistVersion() {
        return clientNodelistVersion;
    }

    public void setClientNodelistVersion(String clientNodelistVersion) {
        this.clientNodelistVersion = clientNodelistVersion;
    }

    public String getServerNodelistVersion() {
        return serverNodelistVersion;
    }

    public void setServerNodelistVersion(String serverNodelistVersion) {
        this.serverNodelistVersion = serverNodelistVersion;
    }

    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isByClientStart() {
        return byClientStart;
    }

    public void setByClientStart(boolean byClientStart) {
        this.byClientStart = byClientStart;
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

    public boolean isSuss() {
        return suss;
    }

    public void setSuss(boolean suss) {
        this.suss = suss;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public Long getNano() {
        return nano;
    }

    public void setNano(Long nano) {
        this.nano = nano;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}
