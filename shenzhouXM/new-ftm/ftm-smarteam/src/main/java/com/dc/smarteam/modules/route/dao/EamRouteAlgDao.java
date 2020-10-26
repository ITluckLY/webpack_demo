/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.route.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.route.entity.EamRouteAlg;

/**
 * 交易路由DAO接口
 *
 * @author kern
 * @version 2015-12-24
 */
@Mapper
public interface EamRouteAlgDao extends CrudDao<EamRouteAlg> {

}