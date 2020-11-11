package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.LongDataEntity;

import java.util.Date;

/**
 *
 * 客户端概况
 * liuyfal  20180327
 *
 */
public class ClientConfigLog extends LongDataEntity<ClientConfigLog>{
    private String ip;
    private String port;
    private String clientName;
    private String beforeModifyValue;
    private String afterModifyValue;
    private String cfgFileName;
    private Date modifiedDate;
    private String remarks;

    public ClientConfigLog() {

    }


    public String getBeforeModifyValue() {
        return beforeModifyValue;
    }

    public void setBeforeModifyValue(String beforeModifyValue) {
        this.beforeModifyValue = beforeModifyValue;
    }

    public String getAfterModifyValue() {
        return afterModifyValue;
    }

    public void setAfterModifyValue(String afterModifyValue) {
        this.afterModifyValue = afterModifyValue;
    }

    public String getCfgFileName() {
        return cfgFileName;
    }

    public void setCfgFileName(String cfgFileName) {
        this.cfgFileName = cfgFileName;
    }


    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "ClientConfigLog{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", clientName='" + clientName + '\'' +
                ", beforeModifyValue='" + beforeModifyValue + '\'' +
                ", afterModifyValue='" + afterModifyValue + '\'' +
                ", cfgFileName='" + cfgFileName + '\'' +
                ", modifiedDate=" + modifiedDate +
                ", remarks='" + remarks + '\'' +
                '}';
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

}
