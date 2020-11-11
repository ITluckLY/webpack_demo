/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.serviceinfo.dao.FtServiceInfoDao;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
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
public class FtServiceInfoService extends CrudService<FtServiceInfoDao, FtServiceInfo> {

    public FtServiceInfo get(String id) {
        return super.get(id);
    }

    public List<FtServiceInfo> findList(FtServiceInfo ftServiceInfo) {
        return super.findList(ftServiceInfo);
    }

    public Page<FtServiceInfo> findPage(Page<FtServiceInfo> page, FtServiceInfo ftServiceInfo) {
        return super.findPage(page, ftServiceInfo);
    }

    @Transactional(readOnly = false)
    public void save(FtServiceInfo ftServiceInfo) {
        super.save(ftServiceInfo);
    }

    @Transactional(readOnly = false)
    public void delete(FtServiceInfo ftServiceInfo) {
        super.delete(ftServiceInfo);
    }

}