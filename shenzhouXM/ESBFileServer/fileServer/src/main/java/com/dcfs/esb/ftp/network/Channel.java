package com.dcfs.esb.ftp.network;

import com.dcfs.esc.ftp.comm.constant.UnitCons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Channel {
    private static final Logger log = LoggerFactory.getLogger(Channel.class);
    private int currSpeed;
    private String channelName;
    private boolean isUse;
    private int maxSpeed;
    private long startTime;
    private long endTime;
    private int dataSize;
    private int sleepTime;

    public Channel(String name) {
        this.currSpeed = 0;
        this.channelName = name;
        this.isUse = true;
        this.startTime = 0;
        this.endTime = 0;
        this.sleepTime = ControlUtil.getInstance().getSleepTime();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    //计算当前渠道的网速，单位为KB/s
    public int getCurrSpeed() {
        if ((this.endTime - this.startTime) >= UnitCons.ONE_SECONDS_MILLIS) {
            this.currSpeed = dataSize / UnitCons.ONE_KB;
            dataSize = 0;
            startTime = endTime;
        }
        return currSpeed;
    }

    public int getSpeed() {
        return this.currSpeed;
    }

    //设置当前渠道是否睡眠
    public void sleep() {
        if (!this.isUse) {
            try {
                this.dataSize = 0;
                this.currSpeed = 0;
                //log.info(this.channelName + "开始休眠:" + this.sleepTime + "ms");//NOSONAR
                Thread.sleep(this.sleepTime);
                setIsUse(true);
                //log.info(this.channelName + "结束休眠");//NOSONAR
            } catch (InterruptedException e) {
                log.error("", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean getIsUse() {
        return this.isUse;
    }

    public void setIsUse(boolean flag) {
        synchronized (ControlUtil.SLEEP_LOCK) {
            this.isUse = flag;
        }
    }

    public void setDataSize(int size) {
        this.dataSize += size;
    }
}
