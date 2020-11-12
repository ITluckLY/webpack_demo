package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mocg on 2016/7/21.
 * 客户端请求流水信息。
 */
public class ClientRegisterRecord implements Serializable {
    private String nodeType;
    private String nodeId;
    /*配置文件FtpClisvrConfig中客户端名称*/
    private String nodeName;
    /*配置文件FtpClisvrConfig中客户端IP*/
    private String nodeIp;
    /*配置文件FtpClisvrConfig中客户端Port*/
    private String nodePort;
    /* 系统名称 */
    private String sysName;
    private String apiversion;
    private String serverApiVersion;
    private Date startTime;
    private Date endTime;
    private Boolean suss;
    private String status;
    private String errCode;
    private String errMsg;
    private Long nano;
    private String passwd;
    /*流水号,由生产方客户端生成 dtoversion is 1708181533*/
    private String flowNo;
    /* 文件服务器IP地址  */
    private String connectServerId;
    private String confName;
    private int cmdPort; //客户端监听端口
    private String userDomainName;//用户域名
    private String remarks1;
    private String remarks2;
    private String remarks3;

    /*配置文件FtpClisvrConfig中返回标志*/
    private boolean confFlag;
    /* 文件服务器IP地址  */
    private String serverPort;
    /* 客户端真实Port  */
    private int realClientPort;
    /* 服务端真实Port  */
    private int realServerPort;

    /*配置文件FtpClisvrConfig中客户端Port*/
    private boolean isAlive;
    /*配置文件FtpClisvrConfig中客户端Port*/
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public Boolean getSuss() {
        return suss;
    }

    public void setSuss(Boolean suss) {
        this.suss = suss;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getCmdPort() {
        return cmdPort;
    }

    public void setCmdPort(int cmdPort) {
        this.cmdPort = cmdPort;
    }

    public String getUserDomainName() {
        return userDomainName;
    }

    public void setUserDomainName(String userDomainName) {
        this.userDomainName = userDomainName;
    }
}
