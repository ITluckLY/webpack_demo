/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.serviceinfo.dao.PutAuthEntityDao;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class PutAuthEntityService extends CrudService<PutAuthEntityDao, PutAuthEntity> {

    public PutAuthEntity get(String id) {
        return super.get(id);
    }

    public List<PutAuthEntity> findList(PutAuthEntity putAuthEntity) {
        return super.findList(putAuthEntity);
    }

    public Page<PutAuthEntity> findPage(Page<PutAuthEntity> page, PutAuthEntity putAuthEntity) {
        return super.findPage(page, putAuthEntity);
    }

    @Transactional(readOnly = false)
    public void save(PutAuthEntity putAuthEntity) {
        super.save(putAuthEntity);
    }

    @Transactional(readOnly = false)
    public void delete(PutAuthEntity putAuthEntity) {
        super.delete(putAuthEntity);
    }

}