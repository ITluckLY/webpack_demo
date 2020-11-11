/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.nodeparam.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;

/**
 * 节点参数DAO接口
 *
 * @author liwang
 * @version 2016-01-11
 */
@MyBatisDao
public interface FtNodeParamDao extends CrudDao<FtNodeParam> {

}