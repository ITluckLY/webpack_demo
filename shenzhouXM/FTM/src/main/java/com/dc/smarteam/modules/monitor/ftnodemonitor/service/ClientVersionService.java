package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.ClientVersionDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.ClientVersionLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by liwjx on 2017/9/11.
 */
@Service
@Transactional(readOnly = true)
public class ClientVersionService extends CrudService<ClientVersionDao,ClientVersionLog>{

    @Autowired
    private ClientVersionDao clientVersionDao;

    public List<ClientVersionLog> findClientList(){return super.findList(new ClientVersionLog());}
    public List<ClientVersionLog> findClientLog(Map map){
        return clientVersionDao.findClientLog(map);
    }
}
