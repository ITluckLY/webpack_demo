/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 节点监控DAO接口
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Mapper
public interface FtNodeMonitorMapper extends CrudDao<FtNodeMonitor> {
    public FtNodeMonitor getNode(FtNodeMonitor FtNodeMonitor);

    public List<FtNodeMonitor> getNodeLogList(Map map);

    public List<FtNodeMonitor> getNodeLog(FtNodeMonitor FtNodeMonitor);

    public void setNodeInfo(FtNodeMonitor ftNodeMonitor);

    public void setNetState(FtNodeMonitor ftNodeMonitor);

    public void setThreshold(FtNodeMonitor ftNodeMonitor);

    public void insertNodeInfo(FtNodeMonitor FtNodeMonitor);

    public void updateNodeInfo(FtNodeMonitor FtNodeMonitor);

    public List<FtNodeMonitor> findLog(FtNodeMonitor FtNodeMonitor);

    public List<String> findSystemNameList(FtNodeMonitor ftNodeMonitor);

    public List<String> findNodeNameList(FtNodeMonitor ftNodeMonitor);

    public long findListByTotal();

    public long findListByDisable();

    public void deleteNode(FtNodeMonitor ftNodeMonitor);

    public void updateNode(FtNodeMonitor ftNodeMonitor);

}