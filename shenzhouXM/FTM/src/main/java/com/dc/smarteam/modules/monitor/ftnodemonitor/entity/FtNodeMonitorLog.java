/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * 节点监控Entity
 *
 * @author lvchuan
 * @version 2016-06-26
 */
public class FtNodeMonitorLog extends DataEntity<FtNodeMonitorLog> {

    private static final long serialVersionUID = 1L;
    private String time;        //采集时间
    private String system;      //节点组
    private String node;        //节点名
    private String ip;          //IP地址
    private String state;       //采集时间
    private String catalog;     //分类
    private String periods;     //周期
    private String filenumber;  //文件数
    private String storage;     //存储
    private String cpu;         //CPU
    private String disk;        //硬盘
    private String memory;      //内存
    private String flowrate;    //流量
    private String network;     //网络
    private String port;        //端口
    private Date beginDate;     //开始时间
    private Date endDate;       //结束时间




    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getFilenumber() {
        return filenumber;
    }

    public void setFilenumber(String filenumber) {
        this.filenumber = filenumber;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getFlowrate() {
        return flowrate;
    }

    public void setFlowrate(String flowrate) {
        this.flowrate = flowrate;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
