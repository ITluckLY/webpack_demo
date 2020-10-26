/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorMsgEmail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 节点监控DAO接口
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Mapper
@Repository
public interface FtNodeMonitorMsgEmailMapper  {

    List<FtNodeMonitorMsgEmail> getAddMsgAndEmailList(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize);

    int getAddMsgAndEmailTotal();

    FtNodeMonitorMsgEmail getFtNodeMonitorMsgEmail(@Param("id") String id);

    int insert(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail);

    int delete(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail);
}