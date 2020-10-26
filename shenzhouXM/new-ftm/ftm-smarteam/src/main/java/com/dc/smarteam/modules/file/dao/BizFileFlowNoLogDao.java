/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.BizFileFlowNoLog;

/**
 * 文件传输记录DAO接口
 *
 * @author hudja
 * @version 2016-01-12
 */
@Mapper
public interface BizFileFlowNoLogDao extends CrudDao<BizFileFlowNoLog> {

}