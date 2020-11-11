package com.dc.smarteam.modules.client.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.client.dao.ClientMonitorLogDao;
import com.dc.smarteam.modules.client.entity.ClientMonitorLog;
import com.dc.smarteam.modules.file.entity.BizFileUploadLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Service
@Transactional(readOnly = true)
public class ClientMonitorLogService extends CrudService<ClientMonitorLogDao, ClientMonitorLog> {
    @Override
    public List<ClientMonitorLog> findList(ClientMonitorLog clientMonitorLog) {
        return super.findList(clientMonitorLog);
    }

    public List<String> findFileNameList() {
        return dao.findFileNameList(new ClientMonitorLog());
    }

    public List<String> findFlowNoList() {
        return dao.findFlowNoList(new ClientMonitorLog());
    }

    public Long findListByTime(Map map) {
        return dao.findListByTime(map);
    }

    public List<BizFileUploadLog> findListByTranCodeAndFileName(Map map){return  dao.findListByTranCodeAndFileName(map);}

}
