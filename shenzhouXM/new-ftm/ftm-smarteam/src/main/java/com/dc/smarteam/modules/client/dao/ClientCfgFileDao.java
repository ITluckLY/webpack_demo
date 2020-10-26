package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.client.entity.ClientCfgFile;

/**
 * Created by mocg on 2017/3/15.
 */
@Mapper
public interface ClientCfgFileDao extends LongCrudDao<ClientCfgFile> {

    int updateByFileNameAndNodeType(ClientCfgFile clientCfgFile);
    ClientCfgFile findByNameAndFile(ClientCfgFile clientCfgFile);
}
