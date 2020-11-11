/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.component.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.component.dao.FtComponentDao;
import com.dc.smarteam.modules.component.entity.FtComponent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 组件管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class FtComponentService extends CrudService<FtComponentDao, FtComponent> {

    public FtComponent get(String id) {
        return super.get(id);
    }

    public List<FtComponent> findList(FtComponent ftComponent) {
        return super.findList(ftComponent);
    }

    public Page<FtComponent> findPage(Page<FtComponent> page, FtComponent ftComponent) {
        return super.findPage(page, ftComponent);
    }

    @Transactional(readOnly = false)
    public void save(FtComponent ftComponent) {
        super.save(ftComponent);
    }

    @Transactional(readOnly = false)
    public void delete(FtComponent ftComponent) {
        super.delete(ftComponent);
    }

}