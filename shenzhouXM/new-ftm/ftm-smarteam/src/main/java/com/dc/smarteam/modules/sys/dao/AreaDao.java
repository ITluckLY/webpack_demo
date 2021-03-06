/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.dao;

import com.dc.smarteam.common.persistence.TreeDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.sys.entity.Area;

/**
 * 区域DAO接口
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Mapper
public interface AreaDao extends TreeDao<Area> {

}
