/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.nodeparam.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.nodeparam.dao.FtNodeParamDao;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 节点参数Service
 *
 * @author liwang
 * @version 2016-01-11
 */
@Service
@Transactional(readOnly = true)
public class FtNodeParamService extends CrudService<FtNodeParamDao, FtNodeParam> {

    public FtNodeParam get(String id) {
        return super.get(id);
    }

    public List<FtNodeParam> findList(FtNodeParam ftNodeParam) {
        return super.findList(ftNodeParam);
    }

    public Page<FtNodeParam> findPage(Page<FtNodeParam> page, FtNodeParam ftNodeParam) {
        return super.findPage(page, ftNodeParam);
    }

    @Transactional(readOnly = false)
    public void save(FtNodeParam ftNodeParam) {
        super.save(ftNodeParam);
    }

    @Transactional(readOnly = false)
    public void delete(FtNodeParam ftNodeParam) {
        super.delete(ftNodeParam);
    }
}