package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.client.entity.ClientSyn;

import java.util.List;

@Mapper
public interface ClientSynDao extends CrudDao<ClientSyn> {
        public List<ClientSyn> findSingleList(ClientSyn clientSyn);
        public int findRestartTimes(ClientSyn clientSyn);
}
