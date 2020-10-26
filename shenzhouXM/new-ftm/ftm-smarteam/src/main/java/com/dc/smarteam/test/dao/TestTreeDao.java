/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.test.dao;

import com.dc.smarteam.common.persistence.TreeDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.test.entity.TestTree;

/**
 * 树结构生成DAO接口
 *
 * @author ThinkGem
 * @version 2015-04-06
 */
@Mapper
public interface TestTreeDao extends TreeDao<TestTree> {

}