/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.test.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.test.entity.TestData;

/**
 * 单表生成DAO接口
 *
 * @author ThinkGem
 * @version 2015-04-06
 */
@Mapper
public interface TestDataDao extends CrudDao<TestData> {

}