/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.sys.entity.Log;

/**
 * 日志DAO接口
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Mapper
public interface LogDao extends CrudDao<Log> {

}
