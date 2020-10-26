package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorPush;

/**
 * @Author: gaoyang
 * @Date: 2020/10/23  17:57
 * @Description:
 */

public interface FtNodeMonitorPushService {
    Page<FtNodeMonitorPush> findPage(Page<FtNodeMonitorPush> ftNodeMonitorPushPage, FtNodeMonitorPush ftNodeMonitorPush);

    FtNodeMonitorPush get(FtNodeMonitorPush ftNodeMonitorPush);

    String repush2Datenode(FtNodeMonitorPush ftNodeMonitorPushTemp);
}
