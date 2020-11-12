package com.dcfs.esb.ftp.server.config;

import com.dcfs.esb.ftp.interfases.context.CachedContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统上下文
 * Created by mocg on 2016/10/19.
 */
public class SysContent {
    private static final Logger log = LoggerFactory.getLogger(SysContent.class);
    private static SysContent ourInstance = new SysContent();
    private String runState = "running";
    //是否是主节点
    private boolean isMaster = false;
    private String apiVersion;
    private String nodelistVersion;
    //key tranCode value taskCount
    private Map<String, AtomicInteger> currTaskCountMap = new ConcurrentHashMap<>();
    //key taskPriority value tokenCount
    private Map<Integer, AtomicInteger> currTaskPriorityTokenCountMap = new ConcurrentHashMap<>();
    //network ctrl
    private AtomicLong currTotalNetworkSpeed = new AtomicLong();//byte/s

    private static final Object lock = new Object();//task锁 add 20171016
    private static final Object lock1 = new Object();//PriorityToken锁 add 20171016

    private SysContent() {
    }

    public static SysContent getInstance() {
        return ourInstance;
    }

    //methods
    //--synchronized
    public int incrementTaskCount(String task) {
        AtomicInteger atomicInteger = currTaskCountMap.get(task);
        if (atomicInteger == null) {
            synchronized (lock) {
                atomicInteger = currTaskCountMap.get(task);
                if (atomicInteger == null) {
                    atomicInteger = new AtomicInteger();
                    currTaskCountMap.put(task, atomicInteger);
                }
            }
        }
        int count = atomicInteger.incrementAndGet();
        log.debug("increment#task:{}, count:{}", task, count);
        return count;
    }

    public int decrementTaskCount(String task) {
        AtomicInteger atomicInteger = currTaskCountMap.get(task);
        if (atomicInteger == null) return -1;
        int count = atomicInteger.decrementAndGet();
        log.debug("decrement#task:{}, count:{}", task, count);
        return count;
    }

    public int incrementTaskPriorityTokenCount(int priority) {
        AtomicInteger atomicInteger = currTaskPriorityTokenCountMap.get(priority);
        if (atomicInteger == null) {
            synchronized (lock1) {
                atomicInteger = currTaskPriorityTokenCountMap.get(priority);
                if (atomicInteger == null) {
                    atomicInteger = new AtomicInteger();
                    currTaskPriorityTokenCountMap.put(priority, atomicInteger);
                }
            }
        }
        int count = atomicInteger.incrementAndGet();
        log.debug("increment#priority:{}, count:{}", priority, count);
        return count;
    }

    public int decrementTaskPriorityTokenCount(int priority) {
        AtomicInteger atomicInteger = currTaskPriorityTokenCountMap.get(priority);
        if (atomicInteger == null) return -1;
        int count = atomicInteger.decrementAndGet();
        log.debug("decrement#priority:{}, count:{}", priority, count);
        return count;
    }

    public int getTaskPriorityTokenCount(int priority) {
        AtomicInteger atomicInteger = currTaskPriorityTokenCountMap.get(priority);
        if (atomicInteger == null) return -1;
        return atomicInteger.get();
    }

    public void incrementTaskPriorityTokenCount(CachedContext context, int currTaskPriority) {
        incrementTaskPriorityTokenCount(currTaskPriority);
        context.getCxtBean().setCurrTaskPriority(currTaskPriority);
    }

    public void decrementTaskPriorityTokenCount(CachedContext context) {
        int currTaskPriority = context.getCxtBean().getCurrTaskPriority();
        if (currTaskPriority > 0) {
            decrementTaskPriorityTokenCount(currTaskPriority);
            context.getCxtBean().setCurrTaskPriority(0);
        }
    }

    public int incrementTaskCount(CachedContext context, String tranCode) {
        int taskCount = incrementTaskCount(tranCode);
        context.getCxtBean().setCurrTaskCount(taskCount);
        return taskCount;
    }

    public void decrementTaskCount(CachedContext context) {
        int currTaskCount = context.getCxtBean().getCurrTaskCount();
        if (currTaskCount > 0) {
            decrementTaskCount(context.getCxtBean().getTranCode());
            context.getCxtBean().setCurrTaskCount(0);
        }
    }

    //network speed
    public void addNetworkSpeed(CachedContext context, long speed) {
        long currSpeed = currTotalNetworkSpeed.addAndGet(speed);
        context.getCxtBean().addTaskNetworkSpeed(speed);
        log.trace("curr network speed is {} b/s", currSpeed);
    }

    public void minusNetworkSpeed(CachedContext context) {
        long speed = context.getCxtBean().getCurrTaskNetworkSpeed();
        if (speed > 0) {
            long currSpeed = currTotalNetworkSpeed.addAndGet(-speed);
            context.getCxtBean().minusTaskNetworkSpeed(speed);
            log.trace("curr network speed is {} b/s", currSpeed);
        }
    }

    public long currNetworkSpeed() {
        return currTotalNetworkSpeed.get();
    }

    //getter setter

    public String getRunState() {
        return runState;
    }

    public void setRunState(String runState) {
        this.runState = runState;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getNodelistVersion() {
        return nodelistVersion;
    }

    public void setNodelistVersion(String nodelistVersion) {
        this.nodelistVersion = nodelistVersion;
    }

    public Map<String, AtomicInteger> getCurrTaskCountMap() {
        return currTaskCountMap;
    }

    public Map<Integer, AtomicInteger> getCurrTaskPriorityTokenCountMap() {
        return currTaskPriorityTokenCountMap;
    }
}
