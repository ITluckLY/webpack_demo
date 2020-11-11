/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLogMonitor;

import java.util.List;
import java.util.Map;

/**
 * 节点监控DAO接口
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@MyBatisDao
public interface FtNodeAlarmLogMonitorDao extends CrudDao<FtNodeAlarmLogMonitor> {

    public List<String> findNodeNameList();

    public Long findListByTime(Map map);

    public Long findListByTimeByComm(Map map);

    public Long findListByTimeBySeri(Map map);

    public List<Map<String, String>> findAlarmOftenRecord(Map map);
}