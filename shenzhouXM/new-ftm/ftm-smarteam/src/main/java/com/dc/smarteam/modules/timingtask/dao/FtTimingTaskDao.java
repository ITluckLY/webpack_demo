/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.timingtask.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.timingtask.entity.FtTimingTask;

/**
 * 定时任务DAO接口
 *
 * @author liwang
 * @version 2016-01-11
 */
@Mapper
public interface FtTimingTaskDao extends CrudDao<FtTimingTask> {

}