package com.dc.smarteam.modules.client.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.client.dao.ClientSynDao;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientSynService extends CrudService<ClientSynDao, ClientSyn> {
    private ClientSynDao ClientSynDao;

    public List<ClientSyn> findSingleList(ClientSyn clientSyn){
       return dao.findSingleList(clientSyn);
    }

    public int findRestartTimes(ClientSyn clientSyn){
        return dao.findRestartTimes(clientSyn);
    }

}
