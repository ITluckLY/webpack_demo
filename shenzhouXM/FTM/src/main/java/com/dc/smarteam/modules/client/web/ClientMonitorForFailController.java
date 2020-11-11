/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.client.web;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.modules.client.entity.ClientMonitorLog;
import com.dc.smarteam.modules.client.service.ClientMonitorLogService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorDistributeService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorService;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import com.dc.smarteam.service.ServiceInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * 节点监控Controller
 *
 * @author lvchuan
 * @version 2016-06-27
 */
@Controller
@RequestMapping(value = "${adminPath}/client/ClientMonitorForFail")
public class ClientMonitorForFailController extends BaseController {
    @Autowired
    private ClientMonitorLogService clientMonitorLogService;
    @Autowired
    private ServiceInfoService serviceInfoService;

    @Value("${monitorPeriods}")
    private String monitorPeriods;
    @Value("${monitorHistoryDayNum}")
    private String monitorHistoryDayNum;

    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = {"failState"})
    public String failState(ClientMonitorLog clientMonitorLog, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<String> fileNameList = clientMonitorLogService.findFileNameList();
        model.addAttribute("fileNameList", fileNameList);

        if (clientMonitorLog.getStartTime() == null && clientMonitorLog.getEndTime() == null) {
            clientMonitorLog.setStartTime(DateHelper.getStartDate());
            clientMonitorLog.setEndTime(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (clientMonitorLog.getStartTime() != null) {
            model.addAttribute("beginDate", sdf.format(clientMonitorLog.getStartTime()));
        }
        if (clientMonitorLog.getEndTime() != null) {
            model.addAttribute("endDate", sdf.format(clientMonitorLog.getEndTime()));
        }
        Page<ClientMonitorLog> page = clientMonitorLogService.findPage(new Page<ClientMonitorLog>(request, response), clientMonitorLog);
        model.addAttribute("page", page);
        model.addAttribute("ftServiceInfoList", serviceInfoService.getFtServiceInfoList());
        return "modules/client/failState";
    }


}