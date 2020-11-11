/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.route2.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.route2.dao.FtRouteDao;
import com.dc.smarteam.modules.route2.entity.FtRoute;
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
public class FtRouteService extends CrudService<FtRouteDao, FtRoute> {

    public FtRoute get(String id) {
        return super.get(id);
    }

    public List<FtRoute> findList(FtRoute ftRoute) {
        return super.findList(ftRoute);
    }

    public Page<FtRoute> findPage(Page<FtRoute> page, FtRoute ftRoute) {
        return super.findPage(page, ftRoute);
    }

    @Transactional(readOnly = false)
    public void save(FtRoute ftRoute) {
        super.save(ftRoute);
    }

    @Transactional(readOnly = false)
    public void delete(FtRoute ftRoute) {
        super.delete(ftRoute);
    }

}