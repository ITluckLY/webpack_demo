/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysformat.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.sysformat.entity.EamMsgFormat;

/**
 * 报文格式DAO接口
 *
 * @author zhanghaor
 * @version 2015-12-29
 */
@Mapper
public interface EamMsgFormatDao extends CrudDao<EamMsgFormat> {

}