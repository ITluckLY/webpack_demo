package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.client.entity.ClientSyn;

import java.util.List;

@MyBatisDao
public interface ClientSynDao extends CrudDao<ClientSyn> {
        public List<ClientSyn> findSingleList(ClientSyn clientSyn);
        public int findRestartTimes(ClientSyn clientSyn);
}
