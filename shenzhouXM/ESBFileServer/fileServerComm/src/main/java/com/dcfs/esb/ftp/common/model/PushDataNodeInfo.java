package com.dcfs.esb.ftp.common.model;

/**
 * Created by mocg on 2016/8/22.
 */
public class PushDataNodeInfo {
    private String nodeName;
    private String addr;//ip:port
    private String sysName;
    private String ms;//主备:m s -
    private Integer apiCmdPort;

    public PushDataNodeInfo(String nodeName, String addr, String sysName, String ms, Integer apiCmdPort) {
        this.nodeName = nodeName;
        this.addr = addr;
        this.sysName = sysName;
        this.ms = ms;
        this.apiCmdPort = apiCmdPort;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getMs() {
        return ms;
    }

    public void setMs(String ms) {
        this.ms = ms;
    }

    public Integer getApiCmdPort() {
        return apiCmdPort;
    }

    public void setApiCmdPort(Integer apiCmdPort) {
        this.apiCmdPort = apiCmdPort;
    }
}
