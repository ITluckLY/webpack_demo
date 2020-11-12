package com.dcfs.esc.ftp.datanode.context;

import io.netty.channel.ChannelHandlerContext;

import java.sql.Timestamp;


/**
 * Created on 20180331
 * 获取客户端的请求
 */
public class HeartBeatContextBean extends ContextBean {
    private String nodeType;
    /**
     * 客户端唯一标识
     */
    private String nodeId;
    private String nodeName;
    /**
     * 客户端IP
     */
    private String nodeIp;
    /**
     * 客户端端口
     */
    private String nodePort;
    private String sysName;
    private String apiversion;
    private String serverApiVersion;
//    private Timestamp startTime;
//    private Timestamp endTime;
    /**
     * 客户端状态
     */
    private String status;
//    private Long nano;
    private String connectServerId;
    private String remarks1;
    private String remarks2;
    private String remarks3;

    /**
     * 当前渠道上下文句柄
     */
    ChannelHandlerContext ctx;
    /**
     * 当前时间
     */
    private long currTime;
    @Override
    public void clean() {
        //nothing
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public String getNodePort() {
        return nodePort;
    }

    public void setNodePort(String nodePort) {
        this.nodePort = nodePort;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getApiversion() {
        return apiversion;
    }

    public void setApiversion(String apiversion) {
        this.apiversion = apiversion;
    }

    public String getServerApiVersion() {
        return serverApiVersion;
    }

    public void setServerApiVersion(String serverApiVersion) {
        this.serverApiVersion = serverApiVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConnectServerId() {
        return connectServerId;
    }

    public void setConnectServerId(String connectServerId) {
        this.connectServerId = connectServerId;
    }

    public String getRemarks1() {
        return remarks1;
    }

    public void setRemarks1(String remarks1) {
        this.remarks1 = remarks1;
    }

    public String getRemarks2() {
        return remarks2;
    }

    public void setRemarks2(String remarks2) {
        this.remarks2 = remarks2;
    }

    public String getRemarks3() {
        return remarks3;
    }

    public void setRemarks3(String remarks3) {
        this.remarks3 = remarks3;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public long getCurrTime() {
        return currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }
}
