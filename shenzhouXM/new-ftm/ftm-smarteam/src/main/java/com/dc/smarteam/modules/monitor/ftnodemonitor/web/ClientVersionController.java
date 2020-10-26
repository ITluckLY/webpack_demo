package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.ClientVersionLog;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.ClientVersionService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.impl.ClientVersionServiceImpl;
import com.dc.smarteam.service.SysServiceI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liwjx on 2017/9/11.
 */
@Slf4j
@RestController
@RequestMapping(value = "${adminPath}/monitor/ClientVersion")
public class ClientVersionController {
    @Resource(name = "ClientVersionServiceImpl")
    private ClientVersionService clientVersionService;
    @Resource(name = "SysServiceImpl")
    private SysServiceI sysService;

    /*@RequiresPermissions("NodeMonitor:clientVersion:view")*/
    @GetMapping(value = "/clientVersion")
    public ResultDto<Map<String, Object>> clientVersionList(ClientVersionLog clientVersionLog, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        ResultDto<List<SystemModel.System>> resultDto = sysService.listAll();
        List<SystemModel.System> portlist = resultDto.getData();
        List<ClientVersionLog> clientLogList = new ArrayList<>();
        for (SystemModel.System system : portlist) {
            ClientVersionLog clientVersion = new ClientVersionLog();
            clientVersion.setUid(system.getUsername());
            clientVersion.setPort(system.getPort());
            clientLogList.add(clientVersion);
        }
        if (clientVersionLog.getBeginDate() == null && clientVersionLog.getEndDate() == null) {
            clientVersionLog.setBeginDate(DateHelper.getStartDate());
            clientVersionLog.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (clientVersionLog.getBeginDate() != null) {
            resultMap.put("beginDate", sdf.format(clientVersionLog.getBeginDate()));
        }
        if (clientVersionLog.getEndDate() != null) {
            resultMap.put("endDate", sdf.format(clientVersionLog.getEndDate()));
        }
        Page<ClientVersionLog> page = clientVersionService.findPage(new Page<ClientVersionLog>(request, response), clientVersionLog);
        List<ClientVersionLog> clientVersionServiceClientList = page.getList();
        for (ClientVersionLog client : clientVersionServiceClientList) {
            for (ClientVersionLog clientxml : clientLogList) {
                if (client.getUid().equals(clientxml.getUid())) {
                    client.setPort(clientxml.getPort());
                    break;
                }
            }
        }
        page.setList(clientVersionServiceClientList);
        resultMap.put("page", page);
        return ResultDtoTool.buildSucceed("success", resultMap);
    }

    /*@RequiresPermissions("NodeMonitor:clientVersion:view")*/
    @GetMapping(value = "/clientVersionLog")
    public ResultDto<Map<String, String>> clientVersionlog(ClientVersionLog clientVersionLog, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        map.put("clientIp", clientVersionLog.getClientIp());
        map.put("uid", clientVersionLog.getUid());
        List<ClientVersionLog> clientVersionLogList = clientVersionService.findClientLog(map);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("clientIp", clientVersionLog.getClientIp());
        resultMap.put("uid", clientVersionLog.getUid());
        PageHelper.getInstance().getPage(ClientVersionLog.class, request, response, resultMap, clientVersionLogList);
        return ResultDtoTool.buildSucceed("success", resultMap);
    }
}
