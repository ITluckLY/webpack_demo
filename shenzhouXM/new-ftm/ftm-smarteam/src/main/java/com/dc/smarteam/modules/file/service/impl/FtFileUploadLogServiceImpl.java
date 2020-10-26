/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.service.impl;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.FtFileUploadDao;
import com.dc.smarteam.modules.file.dao.FtFileUploadLogDao;
import com.dc.smarteam.modules.file.entity.FtFileRollbackLog;
import com.dc.smarteam.modules.file.entity.FtFileUpload;
import com.dc.smarteam.modules.file.entity.FtFileUploadLog;
import com.dc.smarteam.modules.file.service.FtFileUploadLogService;
import com.dc.smarteam.modules.file.service.FtFileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service("FtFileUploadLogServiceImpl")
@Transactional(readOnly = false)
public class FtFileUploadLogServiceImpl extends CrudService<FtFileUploadLogDao, FtFileUploadLog> implements FtFileUploadLogService {
    //TODO 从配置文件中读取
    @Resource
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

    @Override
    public int getFtFileRollbackLogTotal() {
        return 0;
    }
}