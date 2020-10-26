package com.dc.smarteam.modules.sysinfo.service.impl;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.sysinfo.dao.FtSysInfoMapper;
import com.dc.smarteam.modules.sysinfo.entity.FtRelation;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * 系统管理Service
 *
 * @author lvchuan
 * @version 2016-06-23
 */
@Service("FtSysInfoServiceImpl")
@Transactional(readOnly = true)
public class FtSysInfoServiceImpl extends CrudService<FtSysInfoMapper, FtSysInfo> implements FtSysInfoService{

    @Autowired
    private FtSysInfoMapper ftSysInfoMapper;

    public FtSysInfo get(String id) {
        return super.get(id);
    }


    public FtSysInfo getByName(String name) {
        return ftSysInfoMapper.getByName(name);
    }


    public List<FtSysInfo> findList(FtSysInfo ftSysInfo) {
        return super.findList(ftSysInfo);
    }

    public Page<FtSysInfo> findPage(Page<FtSysInfo> page, FtSysInfo ftSysInfo) {
        return super.findPage(page, ftSysInfo);
    }

    @Transactional(readOnly = false)
    public void save(FtSysInfo ftSysInfo) {
        super.save(ftSysInfo);
    }

    @Transactional(readOnly = false)
    public void delete(FtSysInfo ftSysInfo) {
        super.delete(ftSysInfo);
    }

    @Transactional(readOnly = false)
    public void saveSystem(FtSysInfo ftSysInfo) {
        ftSysInfoMapper.updateSystem(ftSysInfo);
    }

    @Transactional(readOnly = false)
    public void setRelation(FtRelation ftRelation) {
        ftSysInfoMapper.setRelation(ftRelation);
    }

    @Transactional(readOnly = false)
    public void deleteRelation(FtRelation ftRelation) {
        ftSysInfoMapper.deleteRelation(ftRelation);
    }

    public List<String> findSystemNameList() {
        return ftSysInfoMapper.findSystemNameList(new FtSysInfo());
    }


}
