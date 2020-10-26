package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by chenhuae on 2016/9/7.
 */
public class FtNodeStateLogMonitor extends DataEntity<FtNodeStateLogMonitor> {

    private static final long serialVersionUID = 1L;
    private String nodeNameTemp;
    private String nodeType;
    private String sysName;
    private Date sendTime;
    private String hostName;
    private String hostAddress;
    private String portInfo;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNodeNameTemp() {
        return nodeNameTemp;
    }

    public void setNodeNameTemp(String nodeNameTemp) {
        this.nodeNameTemp = nodeNameTemp;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getPortInfo() {
        return portInfo;
    }

    public void setPortInfo(String portInfo) {
        this.portInfo = portInfo;
    }
}
