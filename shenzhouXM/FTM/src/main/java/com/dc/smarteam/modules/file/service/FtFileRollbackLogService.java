/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.FtFileRollbackLogDao;
import com.dc.smarteam.modules.file.entity.FtFileRollbackLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = false)
public class FtFileRollbackLogService extends CrudService<FtFileRollbackLogDao, FtFileRollbackLog> {
    @Autowired
    private FtFileRollbackLogDao ftFileRollbackLogDao;

    public FtFileRollbackLog get(String id) {
        return super.get(id);
    }

    public List<FtFileRollbackLog> findAll(FtFileRollbackLog ftFileRollbackLog) {
        return ftFileRollbackLogDao.findAll(ftFileRollbackLog);
    }

    public void save(FtFileRollbackLog ftFileRollbackLog) {
        ftFileRollbackLogDao.save(ftFileRollbackLog);
    }

    public List<FtFileRollbackLog> findList(FtFileRollbackLog ftFileRollbackLog) {
        return super.findList(ftFileRollbackLog);
    }
}