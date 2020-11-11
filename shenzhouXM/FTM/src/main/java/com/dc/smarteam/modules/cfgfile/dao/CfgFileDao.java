package com.dc.smarteam.modules.cfgfile.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;

/**
 * Created by mocg on 2017/3/15.
 */
@MyBatisDao
public interface CfgFileDao extends LongCrudDao<CfgFile> {

    int updateByFileNameAndNodeType(CfgFile cfgFile);
}
