/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.FtFileUploadLogDao;
import com.dc.smarteam.modules.file.entity.FtFileUploadLog;
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
public class FtFileUploadLogService extends CrudService<FtFileUploadLogDao, FtFileUploadLog> {
    //TODO 从配置文件中读取
    @Autowired
    private FtFileUploadLogDao ftFileUploadLogDao;

    public FtFileUploadLog get(String id) {
        return super.get(id);
    }

    public List<FtFileUploadLog> findAll(FtFileUploadLog ftFileUploadLog) {
        return ftFileUploadLogDao.findAll(ftFileUploadLog);
    }

    public void save(FtFileUploadLog ftFileUploadLog) {
        ftFileUploadLogDao.save(ftFileUploadLog);
    }

    public int update(FtFileUploadLog ftFileUploadLog) {
        return ftFileUploadLogDao.update(ftFileUploadLog);
    }

    public List<FtFileUploadLog> findList(FtFileUploadLog ftFileUploadLog) {
        return super.findList(ftFileUploadLog);
    }
}