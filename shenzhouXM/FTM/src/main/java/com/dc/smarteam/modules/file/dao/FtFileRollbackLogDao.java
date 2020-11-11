/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.file.entity.FtFileRollbackLog;

import java.util.List;

/**
 * 文件管理DAO接口
 *
 * @author liwang
 * @version 2016-01-12
 */
@MyBatisDao
public interface FtFileRollbackLogDao extends CrudDao<FtFileRollbackLog> {

    List<FtFileRollbackLog> findAll(FtFileRollbackLog ftFileRollbackLog);

    void save(FtFileRollbackLog ftFileRollbackLog);

}