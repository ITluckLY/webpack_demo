/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.fileclean.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.fileclean.dao.FtFileCleanDao;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件清理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class FtFileCleanService extends CrudService<FtFileCleanDao, FtFileClean> {

    public FtFileClean get(String id) {
        return super.get(id);
    }

    public List<FtFileClean> findList(FtFileClean ftFileClean) {
        return super.findList(ftFileClean);
    }

    public Page<FtFileClean> findPage(Page<FtFileClean> page, FtFileClean ftFileClean) {
        return super.findPage(page, ftFileClean);
    }

    @Transactional(readOnly = false)
    public void save(FtFileClean ftFileClean) {
        super.save(ftFileClean);
    }

    @Transactional(readOnly = false)
    public void delete(FtFileClean ftFileClean) {
        super.delete(ftFileClean);
    }

}