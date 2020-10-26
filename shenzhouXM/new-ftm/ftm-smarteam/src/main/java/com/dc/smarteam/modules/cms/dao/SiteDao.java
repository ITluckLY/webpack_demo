/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.cms.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.cms.entity.Site;

/**
 * 站点DAO接口
 *
 * @author ThinkGem
 * @version 2013-8-23
 */
@Mapper
public interface SiteDao extends CrudDao<Site> {

}
