/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysmanager.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.sysmanager.dao.FtSysManagerDao;
import com.dc.smarteam.modules.sysmanager.entity.FtSysManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统管理员Service
 *
 * @author lvchuan
 * @version 2016-06-23
 */
@Service
@Transactional(readOnly = true)
public class FtSysManagerService extends CrudService<FtSysManagerDao, FtSysManager> {
    @Autowired
    private FtSysManagerDao ftSysManagerDao;

    public FtSysManager get(String id) {
        return super.get(id);
    }

    public List<FtSysManager> findList(FtSysManager FtSysManager) {
        return super.findList(FtSysManager);
    }

    public Page<FtSysManager> findPage(Page<FtSysManager> page, FtSysManager FtSysManager) {
        return super.findPage(page, FtSysManager);
    }

    @Transactional(readOnly = false)
    public void save(FtSysManager FtSysManager) {
        super.save(FtSysManager);
    }

    @Transactional(readOnly = false)
    public void delete(FtSysManager FtSysManager) {
        super.delete(FtSysManager);
    }

    @Transactional(readOnly = false)
    public List<String> findSystem(FtSysManager ftSysManager) {
        return ftSysManagerDao.findSystem(ftSysManager);
    }
}