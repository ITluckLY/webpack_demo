package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import java.util.List;
import java.util.Map;

public interface FtNodeMonitorService {
    
     FtNodeMonitor get(String id);
    
     void setThreshold(FtNodeMonitor ftNodeMonitor);
    
     void insertNodeInfo(FtNodeMonitor ftNodeMonitor);

     List<FtNodeMonitor> findList(FtNodeMonitor ftNodeMonitor);

     List<FtNodeMonitor> findLog(FtNodeMonitor ftNodeMonitor);

     Page<FtNodeMonitor> findPage(Page<FtNodeMonitor> page, FtNodeMonitor ftNodeMonitor);

     void save(FtNodeMonitor FtNodeMonitor);

     void delete(FtNodeMonitor ftNodeMonitor);

     void setNodeInfo(FtNodeMonitor FtNodeMonitor);

     void setNetState(FtNodeMonitor FtNodeMonitor);
    
     FtNodeMonitor getNode(FtNodeMonitor FtNodeMonitor);
    
     List<FtNodeMonitor> getNodeLog(FtNodeMonitor FtNodeMonitor);
    
     List<FtNodeMonitor> getNodeLogList(Map map);

     List<String> findSystemNameList();
    
     List<String> findNodeNameList();

     void deleteNode(FtNodeMonitor ftNodeMonitor);
    
     void updateNode(FtNodeMonitor ftNodeMonitor);

}
