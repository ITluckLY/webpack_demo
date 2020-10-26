package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;

/**
 * 文件回滚
 * Created by huangzbb on 2016/12/16.
 */
public class FtFileRollbackLog extends DataEntity<FtFileRollbackLog> {
    private static final long serialVersionUID = 1L;

    private String dataNodeName;    //数据节点名称
    private String bakFileName;        //服务端原文件的备份路径
    private String monitorIp;        //监控端地址
    private String monitorCmdPort;  //监控端外部调用端口

    private String retCode;         //响应代码
    private String retData;         //响应数据
    private String retMsg;          //响应消息

    public String getDataNodeName() {
        return dataNodeName;
    }

    public void setDataNodeName(String dataNodeName) {
        this.dataNodeName = dataNodeName;
    }

    public String getBakFileName() {
        return bakFileName;
    }

    public void setBakFileName(String bakFileName) {
        this.bakFileName = bakFileName;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    public String getMonitorCmdPort() {
        return monitorCmdPort;
    }

    public void setMonitorCmdPort(String monitorCmdPort) {
        this.monitorCmdPort = monitorCmdPort;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetData() {
        return retData;
    }

    public void setRetData(String retData) {
        this.retData = retData;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
}
