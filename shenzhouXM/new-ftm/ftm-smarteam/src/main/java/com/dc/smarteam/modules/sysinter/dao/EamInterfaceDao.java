/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysinter.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.sysinter.entity.EamInterface;

/**
 * 接口清单DAO接口
 *
 * @author zhanghaor
 * @version 2015-12-29
 */
@Mapper
public interface EamInterfaceDao extends CrudDao<EamInterface> {

}