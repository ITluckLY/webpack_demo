package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;
import com.dc.smarteam.modules.client.entity.ClientCfgFile;

/**
 * Created by mocg on 2017/3/15.
 */
@MyBatisDao
public interface ClientCfgFileDao extends LongCrudDao<ClientCfgFile> {

    int updateByFileNameAndNodeType(ClientCfgFile clientCfgFile);
    ClientCfgFile findByNameAndFile(ClientCfgFile clientCfgFile);
}
