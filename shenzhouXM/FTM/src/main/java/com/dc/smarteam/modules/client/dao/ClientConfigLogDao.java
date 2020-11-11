package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.client.entity.ClientConfigLog;

@MyBatisDao
public interface ClientConfigLogDao extends LongCrudDao<ClientConfigLog> {

}
