package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.client.entity.ClientMonitorLog;
import com.dc.smarteam.modules.file.entity.BizFileUploadLog;

import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@MyBatisDao
public interface ClientMonitorLogDao extends CrudDao<ClientMonitorLog> {
    public List<String> findFileNameList(ClientMonitorLog clientMonitorLog);

    public List<String> findFlowNoList(ClientMonitorLog clientMonitorLog);

    public Long findListByTime(Map map);

    public List<BizFileUploadLog> findListByTranCodeAndFileName(Map map);


}
