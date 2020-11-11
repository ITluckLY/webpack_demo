/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.FtFileUploadDao;
import com.dc.smarteam.modules.file.entity.FtFileUpload;
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
public class FtFileUploadService extends CrudService<FtFileUploadDao, FtFileUpload> {
    //TODO 从配置文件中读取
    @Autowired
    private FtFileUploadDao ftFileUploadDao;

    public FtFileUpload get(String id) {
        return super.get(id);
    }

    public List<FtFileUpload> findAll(FtFileUpload ftFileUpload) {
        return ftFileUploadDao.findAll(ftFileUpload);
    }

    public void save(FtFileUpload ftFileUpload) {
        ftFileUploadDao.save(ftFileUpload);
    }

    public void delOne(String id) {
        ftFileUploadDao.delOne(id);
    }

    public int update(FtFileUpload ftFileUpload) {
        return ftFileUploadDao.update(ftFileUpload);
    }
}