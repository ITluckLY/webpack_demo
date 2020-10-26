/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 节点监控DAO接口
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Mapper
public interface FtNodeAlarmLineMapper {

      int insert(FtNodeAlarmLine ftNodeAlarmLine);

      int update(FtNodeAlarmLine ftNodeAlarmLine);
}