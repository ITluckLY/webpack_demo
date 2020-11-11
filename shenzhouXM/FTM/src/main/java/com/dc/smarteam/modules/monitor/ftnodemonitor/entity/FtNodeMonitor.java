/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 节点监控Entity
 *
 * @author lvchuan
 * @version 2016-06-26
 */
public class FtNodeMonitor extends DataEntity<FtNodeMonitor> {

    private static final long serialVersionUID = 1L;
    private String system;      // 系统名称
    private String node;        // 节点名称
    private Date time;          // 硬件状态采集时间
    private String ip;          // 节点IP
    private String port;        //节点端口
    private String state;       // 节点状态
    private String catalog;     // 节点目录
    private String filenumber;  // 文件数量
    private String storage;     // 存储空间
    private String cpu;         // CPU
    private String disk;        // 磁盘
    private String flowrate;    // 流量
    private String network;     // 网络状态
    private String memory;      // 内存
    private String filenumberline;  // 文件数量阀值
    private String storageline;     // 存储空间阀值
    private String cpuline;         // CUP阀值
    private String diskline;        // 磁盘阀值
    private String networkline;     // 网络状态阀值
    private String memoryline;      // 内存阀值
    private String filenumberwarn;  // 文件数量警告
    private String storagewarn;     // 存储空间警告
    private String cpuwarn;         // CUP警告
    private String diskwarn;        // 磁盘警告
    private String networkwarn;     // 网络状态警告
    private String memorywarn;      // 内存阀值
    private String periods;         // 传输间隔
    private Date stateTime;         // 节点状态变化时间

    public FtNodeMonitor() {
        super();
    }

    public FtNodeMonitor(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "目标目录长度必须介于 0 和 256 之间")
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Length(min = 0, max = 256, message = "用户长度必须介于 0 和 256 之间")
    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    @Length(min = 0, max = 256, message = "用户长度必须介于 0 和 256 之间")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Length(min = 0, max = 256, message = "用户长度必须介于 0 和 256 之间")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Length(min = 0, max = 256, message = "用户长度必须介于 0 和 256 之间")
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Length(min = 0, max = 256, message = "保留时间长度必须介于 0 和 256 之间")
    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    @Length(min = 0, max = 256, message = "文件归档长度必须介于 0 和 256 之间")
    public String getFilenumber() {
        return filenumber;
    }

    public void setFilenumber(String filenumber) {
        this.filenumber = filenumber;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getFlowrate() {
        return flowrate;
    }

    public void setFlowrate(String flowrate) {
        this.flowrate = flowrate;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    @Length(min = 0, max = 256, message = "文件归档长度必须介于 0 和 256 之间")
    public String getFilenumberline() {
        return filenumberline;
    }

    public void setFilenumberline(String filenumberline) {
        this.filenumberline = filenumberline;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public String getStorageline() {
        return storageline;
    }

    public void setStorageline(String storageline) {
        this.storageline = storageline;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getCpuline() {
        return cpuline;
    }

    public void setCpuline(String cpuline) {
        this.cpuline = cpuline;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getDiskline() {
        return diskline;
    }

    public void setDiskline(String diskline) {
        this.diskline = diskline;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getNetworkline() {
        return networkline;
    }

    public void setNetworkline(String networkline) {
        this.networkline = networkline;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getMemoryline() {
        return memoryline;
    }

    public void setMemoryline(String memoryline) {
        this.memoryline = memoryline;
    }

    @Length(min = 0, max = 256, message = "文件归档长度必须介于 0 和 256 之间")
    public String getFilenumberwarn() {
        return filenumberwarn;
    }

    public void setFilenumberwarn(String filenumberwarn) {
        this.filenumberwarn = filenumberwarn;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public String getStoragewarn() {
        return storagewarn;
    }

    public void setStoragewarn(String storagewarn) {
        this.storagewarn = storagewarn;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getCpuwarn() {
        return cpuwarn;
    }

    public void setCpuwarn(String cpuwarn) {
        this.cpuwarn = cpuwarn;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getDiskwarn() {
        return diskwarn;
    }

    public void setDiskwarn(String diskwarn) {
        this.diskwarn = diskwarn;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getNetworkwarn() {
        return networkwarn;
    }

    public void setNetworkwarn(String networkwarn) {
        this.networkwarn = networkwarn;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getMemorywarn() {
        return memorywarn;
    }

    public void setMemorywarn(String memorywarn) {
        this.memorywarn = memorywarn;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public Date getStateTime() {
        return stateTime;
    }

    public void setStateTime(Date stateTime) {
        this.stateTime = stateTime;
    }
}
