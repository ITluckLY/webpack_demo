/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.client.web;

import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.client.entity.ClientMonitorLog;
import com.dc.smarteam.modules.client.service.ClientMonitorLogService;
import com.dc.smarteam.service.impl.ServiceInfoServiceImpl;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 节点监控Controller
 *
 * @author lvchuan
 * @version 2016-06-27
 */
@Controller
@RequestMapping(value = "${adminPath}/client/ClientMonitor")
public class ClientMonitorController extends BaseController {
    @Autowired
    private ClientMonitorLogService clientMonitorLogService;
    @Autowired
    private ServiceInfoServiceImpl serviceInfoService;

    @Value("${monitorPeriods}")
    private String monitorPeriods;
    @Value("${monitorHistoryDayNum}")
    private String monitorHistoryDayNum;

    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = {"reboot"})
    public String reboot(ClientMonitorLog clientMonitorLog, HttpServletRequest request, HttpServletResponse response, Model model) {

        return "modules/client/clientReboot";
    }
    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = {"resource"})
    public String resource(ClientMonitorLog clientMonitorLog, HttpServletRequest request, HttpServletResponse response, Model model) {

        return "modules/client/clientResource";
    }
    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = {"snap"})
    public String snap(ClientMonitorLog clientMonitorLog, HttpServletRequest request, HttpServletResponse response, Model model) {

        return "modules/client/clientSnap";
    }

}