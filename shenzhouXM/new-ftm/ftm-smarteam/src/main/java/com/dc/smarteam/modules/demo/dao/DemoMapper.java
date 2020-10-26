package com.dc.smarteam.modules.demo.dao;

import com.dc.smarteam.modules.demo.entity.FtSysInfos;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper {

    FtSysInfos getFtSysInfoByName(String name);
}
