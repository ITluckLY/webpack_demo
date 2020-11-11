/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.serviceinfo.dao.GetAuthEntityDao;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
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
public class GetAuthEntityService extends CrudService<GetAuthEntityDao, GetAuthEntity> {

    public GetAuthEntity get(String id) {
        return super.get(id);
    }

    public List<GetAuthEntity> findList(GetAuthEntity getAuthEntity) {
        return super.findList(getAuthEntity);
    }

    public Page<GetAuthEntity> findPage(Page<GetAuthEntity> page, GetAuthEntity getAuthEntity) {
        return super.findPage(page, getAuthEntity);
    }

    @Transactional(readOnly = false)
    public void save(GetAuthEntity getAuthEntity) {
        super.save(getAuthEntity);
    }

    @Transactional(readOnly = false)
    public void delete(GetAuthEntity getAuthEntity) {
        super.delete(getAuthEntity);
    }

}