package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.ClientVersionLog;

import java.util.List;
import java.util.Map;

/**
 * Created by liwjx on 2017/9/11.
 */
@MyBatisDao
public interface ClientVersionDao extends CrudDao<ClientVersionLog> {

    public List<ClientVersionLog> findClientList();

    public List<ClientVersionLog> findClientLog(Map map);
}
