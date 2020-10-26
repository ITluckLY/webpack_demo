package com.dc.smarteam.modules.sysinfo.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.sysinfo.entity.FtRelation;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FtSysInfoService {

     FtSysInfo get(String id);

     FtSysInfo get(FtSysInfo entity);

     FtSysInfo getByName(String name);
    
     List<FtSysInfo> findList(FtSysInfo ftSysInfo);

     Page<FtSysInfo> findPage(Page<FtSysInfo> page, FtSysInfo ftSysInfo);
    
     void save(FtSysInfo ftSysInfo);
    
     void delete(FtSysInfo ftSysInfo);
    
     void saveSystem(FtSysInfo ftSysInfo);
    
     void setRelation(FtRelation ftRelation);
    
     void deleteRelation(FtRelation ftRelation);

     List<String> findSystemNameList();

}
