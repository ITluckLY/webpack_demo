/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeMonitorDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FtNodeMonitorService extends CrudService<FtNodeMonitorDao, FtNodeMonitor> {

    @Autowired
    private FtNodeMonitorDao ftNodeMonitorDao;

    public FtNodeMonitor get(String id) {
        return super.get(id);
    }

    @Transactional(readOnly = false)
    public void setThreshold(FtNodeMonitor ftNodeMonitor) {
        ftNodeMonitorDao.setThreshold(ftNodeMonitor);
    }

    @Transactional(readOnly = false)
    public void insertNodeInfo(FtNodeMonitor ftNodeMonitor) {
        ftNodeMonitorDao.insertNodeInfo(ftNodeMonitor);
    }

    public List<FtNodeMonitor> findList(FtNodeMonitor ftNodeMonitor) {
        return super.findList(ftNodeMonitor);
    }

    public List<FtNodeMonitor> findLog(FtNodeMonitor ftNodeMonitor) {
        return ftNodeMonitorDao.findLog(ftNodeMonitor);
    }


    public Page<FtNodeMonitor> findPage(Page<FtNodeMonitor> page, FtNodeMonitor ftNodeMonitor) {
        return super.findPage(page, ftNodeMonitor);
    }


    @Transactional(readOnly = false)
    public void save(FtNodeMonitor FtNodeMonitor) {
        super.save(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public void delete(FtNodeMonitor ftNodeMonitor) {
        super.delete(ftNodeMonitor);
    }

    @Transactional(readOnly = false)
    public void setNodeInfo(FtNodeMonitor FtNodeMonitor) {
        ftNodeMonitorDao.setNodeInfo(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public void setNetState(FtNodeMonitor FtNodeMonitor) {
        ftNodeMonitorDao.setNetState(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public FtNodeMonitor getNode(FtNodeMonitor FtNodeMonitor) {
        return ftNodeMonitorDao.getNode(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public List<FtNodeMonitor> getNodeLog(FtNodeMonitor FtNodeMonitor) {
        return ftNodeMonitorDao.getNodeLog(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public List<FtNodeMonitor> getNodeLogList(Map map) {
        return ftNodeMonitorDao.getNodeLogList(map);
    }

    public List<String> findSystemNameList() {
        return ftNodeMonitorDao.findSystemNameList(new FtNodeMonitor());
    }

    public List<String> findNodeNameList() {
        return ftNodeMonitorDao.findNodeNameList(new FtNodeMonitor());
    }

    @Transactional(readOnly = false)
    public void deleteNode(FtNodeMonitor ftNodeMonitor){
        dao.deleteNode(ftNodeMonitor);
    }

    @Transactional(readOnly = false)
    public void updateNode(FtNodeMonitor ftNodeMonitor){
        dao.updateNode(ftNodeMonitor);
    }

}