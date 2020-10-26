/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysinfo.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.sysinfo.entity.FtRelation;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;

import java.util.List;

/**
 * 系统管理DAO接口
 *
 * @author lvchuan
 * @version 2016-06-21
 */
@Mapper
public interface FtSysInfoMapper extends CrudDao<FtSysInfo> {
    void updateSystem(FtSysInfo ftSysInfo);

    void setRelation(FtRelation ftRelation);

    void deleteRelation(FtRelation ftRelation);

    FtSysInfo getByName(String name);

    List<String> findSystemNameList(FtSysInfo ftSysInfo);

}