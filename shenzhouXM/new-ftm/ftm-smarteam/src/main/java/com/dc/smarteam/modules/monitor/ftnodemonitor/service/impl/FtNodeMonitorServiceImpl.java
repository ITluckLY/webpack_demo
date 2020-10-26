package com.dc.smarteam.modules.monitor.ftnodemonitor.service.impl;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeMonitorMapper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorService;
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
@Service("FtNodeMonitorServiceImpl")
@Transactional(readOnly = true)
public class FtNodeMonitorServiceImpl extends CrudService<FtNodeMonitorMapper, FtNodeMonitor> implements FtNodeMonitorService {

    @Autowired
    private FtNodeMonitorMapper ftNodeMonitorMapper;

    public FtNodeMonitor get(String id) {
        return super.get(id);
    }

    @Transactional(readOnly = false)
    public void setThreshold(FtNodeMonitor ftNodeMonitor) {
        ftNodeMonitorMapper.setThreshold(ftNodeMonitor);
    }

    @Transactional(readOnly = false)
    public void insertNodeInfo(FtNodeMonitor ftNodeMonitor) {
        ftNodeMonitorMapper.insertNodeInfo(ftNodeMonitor);
    }

    public List<FtNodeMonitor> findList(FtNodeMonitor ftNodeMonitor) {
        return super.findList(ftNodeMonitor);
    }

    public List<FtNodeMonitor> findLog(FtNodeMonitor ftNodeMonitor) {
        return ftNodeMonitorMapper.findLog(ftNodeMonitor);
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
        ftNodeMonitorMapper.setNodeInfo(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public void setNetState(FtNodeMonitor FtNodeMonitor) {
        ftNodeMonitorMapper.setNetState(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public FtNodeMonitor getNode(FtNodeMonitor FtNodeMonitor) {
        return ftNodeMonitorMapper.getNode(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public List<FtNodeMonitor> getNodeLog(FtNodeMonitor FtNodeMonitor) {
        return ftNodeMonitorMapper.getNodeLog(FtNodeMonitor);
    }

    @Transactional(readOnly = false)
    public List<FtNodeMonitor> getNodeLogList(Map map) {
        return ftNodeMonitorMapper.getNodeLogList(map);
    }

    public List<String> findSystemNameList() {
        return ftNodeMonitorMapper.findSystemNameList(new FtNodeMonitor());
    }

    public List<String> findNodeNameList() {
        return ftNodeMonitorMapper.findNodeNameList(new FtNodeMonitor());
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
