/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.fileclean.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;

/**
 * 文件清理DAO接口
 *
 * @author liwang
 * @version 2016-01-12
 */
@Mapper
public interface FtFileCleanDao extends CrudDao<FtFileClean> {

}