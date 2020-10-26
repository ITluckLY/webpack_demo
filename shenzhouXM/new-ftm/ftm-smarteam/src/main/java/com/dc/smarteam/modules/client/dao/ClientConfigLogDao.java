package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.client.entity.ClientConfigLog;

@Mapper
public interface ClientConfigLogDao extends LongCrudDao<ClientConfigLog> {

}
