package com.dc.smarteam.modules.serviceinfo.entity;

/**
 * Created by xuchuang on 2018/5/30.
 */
public class FtServiceFlowVo {

    private String transCode;
    private String producer;
    private String customer;
    private String des;
    private String fileName;
    private FtServiceInfo ftServiceInfo;

    public FtServiceInfo getFtServiceInfo() {
        return ftServiceInfo;
    }

    public void setFtServiceInfo(FtServiceInfo ftServiceInfo) {
        this.ftServiceInfo = ftServiceInfo;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FtServiceFlowVo{" +
                "transCode='" + transCode + '\'' +
                ", producer='" + producer + '\'' +
                ", customer='" + customer + '\'' +
                ", des='" + des + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ftServiceInfo=" + ftServiceInfo +
                '}';
    }
}
