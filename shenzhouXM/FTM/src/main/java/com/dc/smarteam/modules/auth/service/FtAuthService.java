/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.auth.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.auth.dao.FtAuthDao;
import com.dc.smarteam.modules.auth.entity.FtAuth;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户管理Service
 *
 * @author yangxc
 * @version 2016-04-13
 */
@Service
@Transactional(readOnly = true)
public class FtAuthService extends CrudService<FtAuthDao, FtAuth> {

    public FtAuth get(String id) {
        return super.get(id);
    }

    public List<FtAuth> findList(FtAuth ftAuth) {
        return super.findList(ftAuth);
    }

    public Page<FtAuth> findPage(Page<FtAuth> page, FtAuth ftAuth) {
        return super.findPage(page, ftAuth);
    }

    @Transactional(readOnly = false)
    public void save(FtAuth ftAuth) {
        super.save(ftAuth);
    }

    @Transactional(readOnly = false)
    public void delete(FtAuth ftAuth) {
        super.delete(ftAuth);
    }

}