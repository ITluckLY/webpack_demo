/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.ipconfig.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;

/**
 * IP控制DAO接口
 *
 * @author liwang
 * @version 2016-01-12
 */
@MyBatisDao
public interface FtUserIpDao extends CrudDao<FtUserIp> {

}