/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorDistribute;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorPush;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorDistributeService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorPushService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorService;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.ServiceInfoService;
import com.dc.smarteam.service.SysService;
import com.dc.smarteam.service.UserService;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 节点监控Controller
 *
 * @author lvchuan
 * @version 2016-06-27
 */
@Controller
@RequestMapping(value = "${adminPath}/monitor/FtNodeMonitor/PushAndDistribute")
public class FtNodeMonitorForPushAndDistributeController extends BaseController {
    @Autowired
    private FtNodeMonitorPushService ftNodeMonitorPushService;
    @Autowired
    private FtNodeMonitorDistributeService ftNodeMonitorDistributeService;
    @Autowired
    private FtNodeMonitorService ftNodeMonitorService;
    @Autowired
    private FtSysInfoService ftSysInfoService;
    @Autowired
    private ServiceInfoService serviceInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysService sysService;


    @Value("${monitorPeriods}")
    private String monitorPeriods;
    @Value("${monitorHistoryDayNum}")
    private String monitorHistoryDayNum;

    //文件推送记录
    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = {"monitorlogForPush"})
    public String monitorlogForPush(FtNodeMonitorPush ftNodeMonitorPush, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<String> systemNameList = ftSysInfoService.findSystemNameList();
        model.addAttribute("systemNameList", systemNameList);
        model.addAttribute("ftServiceInfoList", serviceInfoService.getFtServiceInfoList());
        model.addAttribute("ftUserInfoList", getftUserinfolist());
        model.addAttribute("sysNameList", getSystemNamelist());
        if (ftNodeMonitorPush.getBeginDate() == null && ftNodeMonitorPush.getEndDate() == null) {
            ftNodeMonitorPush.setBeginDate(DateHelper.getStartDate());
            ftNodeMonitorPush.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (ftNodeMonitorPush.getBeginDate() != null) {
            model.addAttribute("beginDate", sdf.format(ftNodeMonitorPush.getBeginDate()));
        }
        if (ftNodeMonitorPush.getEndDate() != null) {
            model.addAttribute("endDate", sdf.format(ftNodeMonitorPush.getEndDate()));
        }
        Page<FtNodeMonitorPush> page = ftNodeMonitorPushService.findPage(new Page<FtNodeMonitorPush>(request, response), ftNodeMonitorPush);
        model.addAttribute("page", page);
        return "modules/monitor/nodeMonitor/ftMonitorlogForPush";
    }

    //文件分发记录2.0
    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = {"monitorlogForDistribute"})
    public String monitorlogForDistributet(FtNodeMonitorDistribute ftNodeMonitorDistribute, HttpServletRequest request, HttpServletResponse response, Model model) {

        List<String> systemNameList = ftSysInfoService.findSystemNameList();
        model.addAttribute("systemNameList", systemNameList);

        List<String> nodeNameList = ftNodeMonitorService.findNodeNameList();
        model.addAttribute("nodeNameList", nodeNameList);

        model.addAttribute("ftServiceInfoList", serviceInfoService.getFtServiceInfoList());

        if (ftNodeMonitorDistribute.getBeginDate() == null && ftNodeMonitorDistribute.getEndDate() == null) {
            ftNodeMonitorDistribute.setBeginDate(DateHelper.getStartDate());
            ftNodeMonitorDistribute.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (ftNodeMonitorDistribute.getBeginDate() != null) {
            model.addAttribute("beginDate", sdf.format(ftNodeMonitorDistribute.getBeginDate()));
        }
        if (ftNodeMonitorDistribute.getEndDate() != null) {
            model.addAttribute("endDate", sdf.format(ftNodeMonitorDistribute.getEndDate()));
        }
        Page<FtNodeMonitorDistribute> page = ftNodeMonitorDistributeService.findPage(new Page<FtNodeMonitorDistribute>(request, response), ftNodeMonitorDistribute);
        model.addAttribute("page", page);
        return "modules/monitor/nodeMonitor/ftMonitorlogForDistribute";
    }

    //文件推送详情
    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = "monitorlogForPushOne")
    public String monitorlogForPushOne(FtNodeMonitorPush ftNodeMonitorPush, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtNodeMonitorPush ftNodeMonitorPushTemp = null;
        try {
            ftNodeMonitorPushTemp = ftNodeMonitorPushService.get(ftNodeMonitorPush);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询文件信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        model.addAttribute("bizFilePushLogTemp", ftNodeMonitorPushTemp);
        return "modules/monitor/nodeMonitor/ftMonitorlogForPushOne";
    }

    //文件重新推送
    @RequiresPermissions("NodeMonitor:monitorHistory:repush")
    @RequestMapping(value = "monitorlogForRePushOne")
    public String monitorlogForRePushOne(FtNodeMonitorPush ftNodeMonitorPush, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtNodeMonitorPush ftNodeMonitorPushTemp = null;
        try {
            ftNodeMonitorPushTemp = ftNodeMonitorPushService.get(ftNodeMonitorPush);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询文件信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String result = ftNodeMonitorPushService.repush2Datenode(ftNodeMonitorPushTemp);
        addMessage(redirectAttributes, result);
        return "redirect:" + Global.getAdminPath() + "/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForPush";
    }

    //文件分发详情
    @RequiresPermissions("NodeMonitor:monitorHistory:view")
    @RequestMapping(value = "monitorlogForDistributeOne")
    public String monitorlogForDistributeOne(FtNodeMonitorDistribute ftNodeMonitorDistribute, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtNodeMonitorDistribute ftNodeMonitorDistributeTemp = null;
        try {
            ftNodeMonitorDistributeTemp = ftNodeMonitorDistributeService.get(ftNodeMonitorDistribute);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询文件信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        model.addAttribute("bizFileDistributeLogTemp", ftNodeMonitorDistributeTemp);
        return "modules/monitor/nodeMonitor/ftMonitorlogForDistributeOne";
    }

    public List getftUserinfolist() {
        ResultDto<List<UserModel.UserInfo>> userDto = userService.listAll();
        List<FtUser> ftUserList = new ArrayList<FtUser>();
        if (ResultDtoTool.isSuccess(userDto)) {
            List<UserModel.UserInfo> userInfos = userDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                FtUser user = new FtUser();
                CfgModelConverter.convertTo(userInfo, user);
                ftUserList.add(user);
            }
        }
        return ftUserList;
    }

    public List getSystemNamelist() {
        ResultDto<List<SystemModel.System>> routeDto = sysService.listAll();
        List<SysProtocol> systemNames = new ArrayList<SysProtocol>();
        if (ResultDtoTool.isSuccess(routeDto)) {
            List<SystemModel.System> systems = routeDto.getData();
            for (SystemModel.System system : systems) {
                SysProtocol sysProtocol = new SysProtocol();
                CfgModelConverter.convertTo(system, sysProtocol);
                systemNames.add(sysProtocol);
            }
        }
        return systemNames;
    }

}