package com.dc.smarteam.model;

import java.util.List;

/**
 * 一个节点只有一个系统
 * Created by mocg on 2016/7/15.
 */
public class Node {
    private String name;
    private String type;
    private String ip;
    private int cmdPort;
    private int ftpServPort;
    private int ftpManagePort;
    private int receivePort;
    private int state;//节点状态 1-正常，0-不正常
    private List<String> systems;
    private String model;
    private String storeModel;
    private String switchModel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCmdPort() {
        return cmdPort;
    }

    public void setCmdPort(int cmdPort) {
        this.cmdPort = cmdPort;
    }

    public int getFtpServPort() {
        return ftpServPort;
    }

    public void setFtpServPort(int ftpServPort) {
        this.ftpServPort = ftpServPort;
    }

    public int getFtpManagePort() {
        return ftpManagePort;
    }

    public void setFtpManagePort(int ftpManagePort) {
        this.ftpManagePort = ftpManagePort;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<String> getSystems() {
        return systems;
    }

    public void setSystems(List<String> systems) {
        this.systems = systems;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStoreModel() {
        return storeModel;
    }

    public void setStoreModel(String storeModel) {
        this.storeModel = storeModel;
    }

    public String getSwitchModel() {
        return switchModel;
    }

    public void setSwitchModel(String switchModel) {
        this.switchModel = switchModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;
        //节点名称相同或ip:cmdport相同
        return name != null && name.equals(node.name) || cmdPort == node.cmdPort && ip != null && ip.equals(node.ip);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + cmdPort;
        return result;
    }
}
