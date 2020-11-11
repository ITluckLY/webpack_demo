/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeMonitorLogDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 节点监控Service
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Service
@Transactional(readOnly = true)
public class FtNodeMonitorLogService extends CrudService<FtNodeMonitorLogDao, FtNodeMonitorLog> {

    public FtNodeMonitorLog get(String id) {
        return super.get(id);
    }

//	public List<FtNodeMonitorLog> findList(FtNodeMonitorLog ftNodeMonitorLog) {
//		return super.findList(ftNodeMonitorLog);
//	}

    public List<String> findSystemNameList() {
        return dao.findSystemNameList(new FtNodeMonitorLog());
    }

    public List<String> findNodeNameList() {
        return dao.findNodeNameList(new FtNodeMonitorLog());
    }


}