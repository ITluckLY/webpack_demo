/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.syslyr.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.syslyr.entity.EamAaLayer;

/**
 * 系统架构层次管理DAO接口
 *
 * @author yangqjb
 * @version 2015-12-24
 */
@Mapper
public interface EamAaLayerDao extends CrudDao<EamAaLayer> {

}