package com.dc.smarteam.modules.cfgfile.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;

/**
 * Created by mocg on 2017/3/15.
 */
@Mapper
public interface CfgFileMapper extends LongCrudDao<CfgFile> {

    int updateByFileNameAndNodeType(CfgFile cfgFile);
}
