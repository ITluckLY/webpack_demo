package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLogMonitor;

import java.util.List;
import java.util.Map;

/**
 * @Author: gaoyang
 * @Date: 2020/10/23  17:09
 * @Description:
 */

public interface FtNodeAlarmLogMonitorService {
    Page<FtNodeAlarmLogMonitor> findPage(Page<FtNodeAlarmLogMonitor> ftNodeAlarmLogMonitorPage, FtNodeAlarmLogMonitor ftNodeAlarmLogMonitor);

    List<Map<String, String>> findAlarmOftenRecord(Map<String, Object> map);

}
