/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.user.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.user.dao.FtUserDao;
import com.dc.smarteam.modules.user.entity.FtUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户管理Service
 *
 * @author liwang
 * @version 2016-01-12,v
 */
@Service
@Transactional(readOnly = true)
public class FtUserService extends CrudService<FtUserDao, FtUser> {

    public FtUser get(String id) {
        return super.get(id);
    }

    public List<FtUser> findList(FtUser ftUser) {
        return super.findList(ftUser);
    }

    public Page<FtUser> findPage(Page<FtUser> page, FtUser ftUser) {
        return super.findPage(page, ftUser);
    }

    @Transactional(readOnly = false)
    public void save(FtUser ftUser) {
        super.save(ftUser);
    }

    @Transactional(readOnly = false)
    public void delete(FtUser ftUser) {
        super.delete(ftUser);
    }

}