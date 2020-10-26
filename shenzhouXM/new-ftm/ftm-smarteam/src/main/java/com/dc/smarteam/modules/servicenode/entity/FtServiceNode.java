/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.servicenode.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 节点管理Entity
 *
 * @author liwang
 * @version 2016-01-11
 */
public class FtServiceNode extends DataEntity<FtServiceNode> {

    public static final String NODEMAIN = "ms-m";
    public static final String NODESTANDBY = "ms-s";
    private static final String WAITING = "waiting";
    private static final String RUNNING = "running";
    private static final long serialVersionUID = 1L;
    private String name;            // 节点名称

    private String systemName;      //20160629新增节点对应的节点组
    private String type;            //节点类型
    private String des;             // 描述
    private String ipAddress;            // IP地址

    private String cmdPort;             // 端口
    private String ftpServPort;         //服务端口
    private String ftpManagePort;       //管理端口
    private String receivePort;         //接收端口

    private String state;               // 状态:RUNNING  正在使用，WAITING 未启用
    private String isolState;           //隔离状态  0：正常状态  1：隔离状态（isolation：隔离）
    private String nodeModel;           //节点模式:主备模式-主(ms-m),主备模式-备(ms-s)

    private String sysNodeModel;        //节点模式      单节点模式（single），多节点并行模式(more)，主备模式(ms) [设置后不可改]
    private String switchModel;         //主备切换      自动模式(auto)、手动模式(handle)
    private String storeModel;          //文件存储模式  单点（single）、同步(sync)、异步(async)
    private int addOrUpdate;
    //ADD 20170908 任务数量
    private String taskCount;           //当前节点任务数量

    public FtServiceNode() {
        super();
    }

    public FtServiceNode(String id) {
        super(id);
    }

    public String getIsolState() {
        return isolState;
    }

    public void setIsolState(String isolState) {
        this.isolState = isolState;
    }

    public int getAddOrUpdate() {
        return addOrUpdate;
    }

    public void setAddOrUpdate(int addOrUpdate) {
        this.addOrUpdate = addOrUpdate;
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //20160629新增节点对应的系统
    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Length(min = 0, max = 256, message = "描述长度必须介于 0 和 256 之间")
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Length(min = 0, max = 256, message = "IP地址长度必须介于 0 和 256 之间")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Length(min = 0, max = 256, message = "IP地址长度必须介于 0 和 256 之间")
    public String getCmdPort() {
        return cmdPort;
    }

    public void setCmdPort(String cmdPort) {
        this.cmdPort = cmdPort;
    }

    @Length(min = 0, max = 256, message = "IP地址长度必须介于 0 和 256 之间")
    public String getFtpServPort() {
        return ftpServPort;
    }

    public void setFtpServPort(String ftpServPort) {
        this.ftpServPort = ftpServPort;
    }

    @Length(min = 0, max = 256, message = "IP地址长度必须介于 0 和 256 之间")
    public String getFtpManagePort() {
        return ftpManagePort;
    }

    public void setFtpManagePort(String ftpManagePort) {
        this.ftpManagePort = ftpManagePort;
    }

    @Length(min = 0, max = 256, message = "IP地址长度必须介于 0 和 256 之间")
    public String getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(String receivePort) {
        this.receivePort = receivePort;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getNodeModel() {
        return nodeModel;
    }

    public void setNodeModel(String nodeModel) {
        this.nodeModel = nodeModel;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getSysNodeModel() {
        return sysNodeModel;
    }

    public void setSysNodeModel(String sysNodeModel) {
        this.sysNodeModel = sysNodeModel;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getSwitchModel() {
        return switchModel;
    }

    public void setSwitchModel(String switchModel) {
        this.switchModel = switchModel;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getStoreModel() {
        return storeModel;
    }

    public void setStoreModel(String storeModel) {
        this.storeModel = storeModel;
    }

    public String getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(String taskCount) {
        this.taskCount = taskCount;
    }

}
