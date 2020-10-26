package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorLog;

/**
 * @Author: gaoyang
 * @Date: 2020/10/26  14:58
 * @Description:
 */

public interface FtNodeMonitorLogService {
    Page<FtNodeMonitorLog> findPage(Page<FtNodeMonitorLog> ftNodeMonitorLogPage, FtNodeMonitorLog ftNodeMonitorLog);
}
