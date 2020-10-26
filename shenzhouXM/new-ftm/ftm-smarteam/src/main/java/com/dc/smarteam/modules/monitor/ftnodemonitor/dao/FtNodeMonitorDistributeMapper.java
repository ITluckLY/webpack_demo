/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorDistribute;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 节点监控DAO接口
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Mapper
public interface FtNodeMonitorDistributeMapper extends CrudDao<FtNodeMonitorDistribute> {

    public List<FtNodeMonitorDistribute> findList();

}