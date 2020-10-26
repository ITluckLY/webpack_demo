/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service.impl;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeAlarmLogMonitorMapper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLogMonitor;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeAlarmLogMonitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 节点监控Service
 *
 * @author
 * @version 2016-06-26
 */
@Service("FtNodeAlarmLogMonitorServiceImpl")
@Transactional(readOnly = true)
public class FtNodeAlarmLogMonitorServiceImpl extends CrudService<FtNodeAlarmLogMonitorMapper, FtNodeAlarmLogMonitor> implements FtNodeAlarmLogMonitorService {

    public FtNodeAlarmLogMonitor get(String id) {
        return super.get(id);
    }

    public List<String> findNodeNameList() {
        return dao.findNodeNameList();
    }


    public Long findListByTime(Map map) {
        return dao.findListByTime(map);
    }

    public Long findListByTimeByComm(Map map) {
        return dao.findListByTimeByComm(map);
    }

    public Long findListByTimeBySeri(Map map) {
        return dao.findListByTimeBySeri(map);
    }

    public List<Map<String, String>> findAlarmOftenRecord(Map map) {
        return dao.findAlarmOftenRecord(map);
    }
}