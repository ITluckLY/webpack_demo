/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeStateLogMonitorDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeStateLogMonitor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 节点监控Service
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Service
@Transactional(readOnly = true)
public class FtNodeStateLogMonitorService extends CrudService<FtNodeStateLogMonitorDao, FtNodeStateLogMonitor> {


    public FtNodeStateLogMonitor get(String id) {
        return super.get(id);
    }

    public List<String> findNodeNameList() {
        return dao.findNodeNameList();
    }

    public List<String> findNodeTypeList() {
        return dao.findNodeTypeList();
    }

    public List<FtNodeStateLogMonitor> findListByTime(Map map) {
        return dao.findListByTime(map);
    }

    public FtNodeStateLogMonitor findStateLogOftenRecord(Map map) {
        return dao.findStateLogOftenRecord(map);
    }
}