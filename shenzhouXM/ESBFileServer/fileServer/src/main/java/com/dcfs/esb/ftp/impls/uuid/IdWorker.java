package com.dcfs.esb.ftp.impls.uuid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 64位(bit)ID (42(毫秒)+0(数据中心ID|业务编码)+13(机器ID)+9(重复累加))
 * <p>
 * 业务编码无业务则用5位随机数
 *
 * @author Polim
 */
public class IdWorker {
    private static final Logger log = LoggerFactory.getLogger(IdWorker.class);

    private static final long TWEPOCH = 1479830400000L;//2016-11-23
    // 机器标识位数
    private static final int WORKER_ID_BITS = 13;
    // 数据中心标识位数
    private static final int DATACENTER_ID_BITS = 0;
    // 机器ID最大值
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    // 数据中心ID最大值
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    // 毫秒内自增位
    private static final int SEQUENCE_BITS = 9;
    // 机器ID偏左移12位
    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;
    // 数据中心ID左移22位
    private static final int DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    // 时间毫秒左移22位
    private static final int TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static long lastTimestamp = -1L;
    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;

    public IdWorker(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("worker Id can't be greater than %d or less than 0");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenter Id can't be greater than %d or less than 0");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        //从下一秒开始，保证与之前的不同
        try {
            final int sleeptime = 1008;
            Thread.sleep(sleeptime);
        } catch (InterruptedException e) {
            log.error("", e);
            Thread.currentThread().interrupt();
        }
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            if (log.isErrorEnabled()) {//NOSONAR
                log.error("Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            }
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}