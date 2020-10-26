/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.auth.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.auth.entity.FtAuth;

/**
 * 用户管理DAO接口
 *
 * @author liwang
 * @version 2016-01-12
 */
@Mapper
public interface FtAuthDao extends CrudDao<FtAuth> {

}