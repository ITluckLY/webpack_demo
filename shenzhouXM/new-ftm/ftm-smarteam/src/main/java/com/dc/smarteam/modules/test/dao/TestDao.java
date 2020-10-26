/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.test.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.test.entity.Test;

/**
 * 测试DAO接口
 *
 * @author ThinkGem
 * @version 2013-10-17
 */
@Mapper
public interface TestDao extends CrudDao<Test> {

}
