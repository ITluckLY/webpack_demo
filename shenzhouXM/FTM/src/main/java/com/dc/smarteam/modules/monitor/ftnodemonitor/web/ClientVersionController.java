package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.ClientVersionLog;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.ClientVersionService;
import com.dc.smarteam.service.SysService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liwjx on 2017/9/11.
 */
@Controller
@RequestMapping(value = "${adminPath}/monitor/ClientVersion")
public class ClientVersionController extends BaseController {
    @Autowired
    private ClientVersionService clientVersionService;
    @Resource
    private SysService sysService;

    @RequiresPermissions("NodeMonitor:clientVersion:view")
    @RequestMapping(value = {"clientVersion"})
    public String clientVersionList(ClientVersionLog clientVersionLog, HttpServletRequest request, HttpServletResponse response, Model model) {
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
            model.addAttribute("beginDate", sdf.format(clientVersionLog.getBeginDate()));
        }
        if (clientVersionLog.getEndDate() != null) {
            model.addAttribute("endDate", sdf.format(clientVersionLog.getEndDate()));
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
        model.addAttribute("page", page);
        return "modules/monitor/nodeMonitor/clientVersionList";
    }

    @RequiresPermissions("NodeMonitor:clientVersion:view")
    @RequestMapping(value = {"clientVersionLog"})
    public String clientVersionlog(ClientVersionLog clientVersionLog, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, String> map = new HashMap<>();
        map.put("clientIp", clientVersionLog.getClientIp());
        map.put("uid", clientVersionLog.getUid());
        List<ClientVersionLog> clientVersionLogList = clientVersionService.findClientLog(map);
        model.addAttribute("clientIp", clientVersionLog.getClientIp());
        model.addAttribute("uid", clientVersionLog.getUid());
        PageHelper.getInstance().getPage(ClientVersionLog.class, request, response, model, clientVersionLogList);
        return "modules/monitor/nodeMonitor/clientVersionLog";
    }
}
