package com.dcfs.esb.ftp.namenode.spring.core.entity.monitor;

import com.dcfs.esb.ftp.spring.core.entity.StringBaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by huangzbb on 2016/8/19.
 */
@Entity
@Table(name = "ft_node_monitor")
public class NodeMonitor extends StringBaseEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getFilenumberline() {
        return filenumberline;
    }

    public void setFilenumberline(String filenumberline) {
        this.filenumberline = filenumberline;
    }

    public String getStorageline() {
        return storageline;
    }

    public void setStorageline(String storageline) {
        this.storageline = storageline;
    }

    public String getCpuline() {
        return cpuline;
    }

    public void setCpuline(String cpuline) {
        this.cpuline = cpuline;
    }

    public String getDiskline() {
        return diskline;
    }

    public void setDiskline(String diskline) {
        this.diskline = diskline;
    }

    public String getNetworkline() {
        return networkline;
    }

    public void setNetworkline(String networkline) {
        this.networkline = networkline;
    }

    public String getMemoryline() {
        return memoryline;
    }

    public void setMemoryline(String memoryline) {
        this.memoryline = memoryline;
    }

    public String getFilenumberwarn() {
        return filenumberwarn;
    }

    public void setFilenumberwarn(String filenumberwarn) {
        this.filenumberwarn = filenumberwarn;
    }

    public String getStoragewarn() {
        return storagewarn;
    }

    public void setStoragewarn(String storagewarn) {
        this.storagewarn = storagewarn;
    }

    public String getCpuwarn() {
        return cpuwarn;
    }

    public void setCpuwarn(String cpuwarn) {
        this.cpuwarn = cpuwarn;
    }

    public String getDiskwarn() {
        return diskwarn;
    }

    public void setDiskwarn(String diskwarn) {
        this.diskwarn = diskwarn;
    }

    public String getNetworkwarn() {
        return networkwarn;
    }

    public void setNetworkwarn(String networkwarn) {
        this.networkwarn = networkwarn;
    }

    public String getMemorywarn() {
        return memorywarn;
    }

    public void setMemorywarn(String memorywarn) {
        this.memorywarn = memorywarn;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public Date getStateTime() {
        return stateTime;
    }

    public void setStateTime(Date stateTime) {
        this.stateTime = stateTime;
    }
}
