package com.dc.smarteam.modules.file.entity;

/**
 * Created by huangzbb on 2016/11/28.
 */
public class FtFileSend {
    private static final long serialVersionUID = 1L;
    private String monitorNodeIp;
    private int monitorNodePort;
    private String dataNodeName;
    private String dataNodeIp;
    private int dataNodePort;
    private String bakFileName;    //dataNode原文件的备份路径

    public String getMonitorNodeIp() {
        return monitorNodeIp;
    }

    public void setMonitorNodeIp(String monitorNodeIp) {
        this.monitorNodeIp = monitorNodeIp;
    }

    public int getMonitorNodePort() {
        return monitorNodePort;
    }

    public void setMonitorNodePort(int monitorNodePort) {
        this.monitorNodePort = monitorNodePort;
    }

    public String getDataNodeName() {
        return dataNodeName;
    }

    public void setDataNodeName(String dataNodeName) {
        this.dataNodeName = dataNodeName;
    }

    public String getDataNodeIp() {
        return dataNodeIp;
    }

    public void setDataNodeIp(String dataNodeIp) {
        this.dataNodeIp = dataNodeIp;
    }

    public int getDataNodePort() {
        return dataNodePort;
    }

    public void setDataNodePort(int dataNodePort) {
        this.dataNodePort = dataNodePort;
    }

    public String getBakFileName() {
        return bakFileName;
    }

    public void setBakFileName(String bakFileName) {
        this.bakFileName = bakFileName;
    }
}
