/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeStateLogMonitor;

import java.util.List;
import java.util.Map;

/**
 * 节点监控DAO接口
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@MyBatisDao
public interface FtNodeStateLogMonitorDao extends CrudDao<FtNodeStateLogMonitor> {

    public List<String> findNodeNameList();

    public List<String> findNodeTypeList();

    public List<FtNodeStateLogMonitor> findListByTime(Map map);

    public FtNodeStateLogMonitor findStateLogOftenRecord(Map map);
}