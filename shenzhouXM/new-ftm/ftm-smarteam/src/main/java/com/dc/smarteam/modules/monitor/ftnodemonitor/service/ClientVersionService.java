package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.ClientVersionLog;

import java.util.List;
import java.util.Map;

/**
 * @Author: gaoyang
 * @Date: 2020/10/26  15:49
 * @Description:
 */

public interface ClientVersionService {
    Page<ClientVersionLog> findPage(Page<ClientVersionLog> clientVersionLogPage, ClientVersionLog clientVersionLog);

    List<ClientVersionLog> findClientLog(Map<String, String> map);
}
