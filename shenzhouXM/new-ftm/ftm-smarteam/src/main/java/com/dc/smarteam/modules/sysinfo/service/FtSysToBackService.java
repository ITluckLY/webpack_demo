/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysinfo.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.sysinfo.dao.FtSysToBackDao;
import com.dc.smarteam.modules.sysinfo.entity.FtSysToBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统管理Service
 *
 * @author lvchuan
 * @version 2016-06-23
 */
@Service
@Transactional(readOnly = true)
public class FtSysToBackService extends CrudService<FtSysToBackDao, FtSysToBack> {
    @Autowired
    private FtSysToBackDao ftSysToBackDao;


}