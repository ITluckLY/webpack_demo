/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysinfo.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.sysinfo.dao.FtSysInfoDao;
import com.dc.smarteam.modules.sysinfo.entity.FtRelation;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;
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
@Service
@Transactional(readOnly = true)
public class FtSysInfoService extends CrudService<FtSysInfoDao, FtSysInfo> {
    @Autowired
    private FtSysInfoDao ftSysInfoDao;

    public FtSysInfo get(String id) {
        return super.get(id);
    }

    public FtSysInfo getByName(String name) {
        return ftSysInfoDao.getByName(name);
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
        ftSysInfoDao.updateSystem(ftSysInfo);
    }

    @Transactional(readOnly = false)
    public void setRelation(FtRelation ftRelation) {
        ftSysInfoDao.setRelation(ftRelation);
    }

    @Transactional(readOnly = false)
    public void deleteRelation(FtRelation ftRelation) {
        ftSysInfoDao.deleteRelation(ftRelation);
    }

    public List<String> findSystemNameList() {
        return ftSysInfoDao.findSystemNameList(new FtSysInfo());
    }


}