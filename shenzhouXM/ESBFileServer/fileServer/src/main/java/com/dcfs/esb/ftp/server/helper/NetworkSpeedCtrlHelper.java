package com.dcfs.esb.ftp.server.helper;

import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esc.ftp.svr.comm.cons.ServerCfgCons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网速控制
 * Created by mocg on 2016/11/23.
 */
public class NetworkSpeedCtrlHelper {
    private static final Logger log = LoggerFactory.getLogger(NetworkSpeedCtrlHelper.class);
    private static final int MIN_PRIORITY = ServerCfgCons.TRAN_CODE_MIN_PRIORITY;
    private static final int MAX_PRIORITY = ServerCfgCons.TRAN_CODE_MAX_PRIORITY;

    private NetworkSpeedCtrlHelper() {
    }

    public static int getPieceNum(CachedContext context) {
        FtpConfig ftpConfig = FtpConfig.getInstance();
        int priority = context.getCxtBean().getTaskPriority();//从1开始
        int pieceNum = ftpConfig.getPieceNum();
        //没有交易码时
        if (priority < MIN_PRIORITY) return pieceNum;
        //有交易码时 超过网络流量控制阀值时,启用分片大小分级制,级数越大分片大小越大
        long currNetworkSpeed = SysContent.getInstance().currNetworkSpeed();
        long netCtrlThreshold = ftpConfig.getNetworkCtrlThreshold();
        if (currNetworkSpeed < netCtrlThreshold) return pieceNum;

        if (priority > MAX_PRIORITY) priority = MAX_PRIORITY;
        return pieceNum * priority / MAX_PRIORITY;
    }

    public static long getSleepTime(CachedContext context) {
        FtpConfig ftpConfig = FtpConfig.getInstance();
        ContextBean cxtBean = context.getCxtBean();
        int priority = cxtBean.getTaskPriority();//从1开始
        long sleepTime = ftpConfig.getNetworkCtrlSleepTime();
        long currNetworkSpeed = SysContent.getInstance().currNetworkSpeed();
        long netCtrlThreshold = ftpConfig.getNetworkCtrlThreshold();
        long maxNetworkSpeed = ftpConfig.getMaxNetworkSpeed();
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#currNetworkSpeed:{}, netCtrlThreshold:{}, maxNetworkSpeed:{}, priority:{}",
                    cxtBean.getNano(), cxtBean.getFlowNo(), currNetworkSpeed, netCtrlThreshold, maxNetworkSpeed, priority);
        }
        //不超过网络流量控制阀值时
        if (currNetworkSpeed < netCtrlThreshold) return 0L;
        //阀值与最大值间，睡眠时间减半
        if (priority < MIN_PRIORITY) {//没有交易码时
            if (currNetworkSpeed >= maxNetworkSpeed) return sleepTime;
            else return sleepTime / 2;//NOSONAR
        } else {//有交易码时 超过网络流量控制阀值时,启用睡眠时间分级制,级数越大睡眠越小
            if (priority > MAX_PRIORITY) priority = MAX_PRIORITY;
            if (currNetworkSpeed < maxNetworkSpeed) {
                //在网速小于最大值时，最高级别的任务不需要睡眠
                if (priority == MAX_PRIORITY) return 0;
                //在网速小于最大值时，睡眠时间减半
                sleepTime /= 2;//NOSONAR
            }
            return sleepTime * (MAX_PRIORITY - priority) / MAX_PRIORITY;
        }
    }

    public static void sleep(CachedContext context) {
        long sleepTime = getSleepTime(context);
        if (sleepTime <= 0) return;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
