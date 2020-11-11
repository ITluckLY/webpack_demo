/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysmanager.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.sysmanager.entity.FtSysManager;

import java.util.List;

/**
 * 系统管理员DAO接口
 *
 * @author lvchuan
 * @version 2016-06-23
 */
@MyBatisDao
public interface FtSysManagerDao extends CrudDao<FtSysManager> {
    public List<String> findSystem(FtSysManager ftSysManager);
}