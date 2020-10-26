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
import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorDistribute;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorPush;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorDistributeService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorPushService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorService;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.ServiceInfoServiceI;
import com.dc.smarteam.service.UserServiceI;
import com.dc.smarteam.service.impl.SysServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 节点监控Controller
 *
 * @author lvchuan
 * @version 2016-06-27
 */
@Slf4j
@RestController
@RequestMapping(value = "${adminPath}/monitor/FtNodeMonitor/PushAndDistribute")
public class FtNodeMonitorForPushAndDistributeController {
    @Autowired
    @Qualifier("FtNodeMonitorPushServiceImpl")
    private FtNodeMonitorPushService ftNodeMonitorPushService;
    @Autowired
    @Qualifier("FtNodeMonitorDistributeServiceImpl")
    private FtNodeMonitorDistributeService ftNodeMonitorDistributeService;
    @Autowired
    @Qualifier("FtNodeMonitorServiceImpl")
    private FtNodeMonitorService ftNodeMonitorService;
    @Autowired
    @Qualifier("FtSysInfoServiceImpl")
    private FtSysInfoService ftSysInfoService;
    @Autowired
    @Qualifier("ServiceInfoServiceImpl")
    private ServiceInfoServiceI serviceInfoService;
    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;
    @Autowired
    private SysServiceImpl sysService;


    @Value("${monitorPeriods}")
    private String monitorPeriods;
    @Value("${monitorHistoryDayNum}")
    private String monitorHistoryDayNum;

    //文件推送记录
    /*@RequiresPermissions("NodeMonitor:monitorHistory:view")*/
    @GetMapping(value = "/monitorlogForPush")
    public ResultDto<Map<String, Object>> monitorlogForPush(FtNodeMonitorPush ftNodeMonitorPush, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> systemNameList = ftSysInfoService.findSystemNameList();
        resultMap.put("systemNameList", systemNameList);
        resultMap.put("ftServiceInfoList", serviceInfoService.getFtServiceInfoList());
        resultMap.put("ftUserInfoList", getftUserinfolist());
        resultMap.put("sysNameList", getSystemNamelist());
        if (ftNodeMonitorPush.getBeginDate() == null && ftNodeMonitorPush.getEndDate() == null) {
            ftNodeMonitorPush.setBeginDate(DateHelper.getStartDate());
            ftNodeMonitorPush.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (ftNodeMonitorPush.getBeginDate() != null) {
            resultMap.put("beginDate", sdf.format(ftNodeMonitorPush.getBeginDate()));
        }
        if (ftNodeMonitorPush.getEndDate() != null) {
            resultMap.put("endDate", sdf.format(ftNodeMonitorPush.getEndDate()));
        }
        Page<FtNodeMonitorPush> page = ftNodeMonitorPushService.findPage(new Page<FtNodeMonitorPush>(request, response), ftNodeMonitorPush);
        resultMap.put("page", page);
        return ResultDtoTool.buildSucceed("success", resultMap);
    }

    //文件分发记录2.0
    /*@RequiresPermissions("NodeMonitor:monitorHistory:view")*/
    @GetMapping(value = "/monitorlogForDistribute")
    public ResultDto<Map<String, Object>> monitorlogForDistributet(FtNodeMonitorDistribute ftNodeMonitorDistribute, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> systemNameList = ftSysInfoService.findSystemNameList();
        resultMap.put("systemNameList", systemNameList);

        List<String> nodeNameList = ftNodeMonitorService.findNodeNameList();
        resultMap.put("nodeNameList", nodeNameList);

        resultMap.put("ftServiceInfoList", serviceInfoService.getFtServiceInfoList());

        if (ftNodeMonitorDistribute.getBeginDate() == null && ftNodeMonitorDistribute.getEndDate() == null) {
            ftNodeMonitorDistribute.setBeginDate(DateHelper.getStartDate());
            ftNodeMonitorDistribute.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (ftNodeMonitorDistribute.getBeginDate() != null) {
            resultMap.put("beginDate", sdf.format(ftNodeMonitorDistribute.getBeginDate()));
        }
        if (ftNodeMonitorDistribute.getEndDate() != null) {
            resultMap.put("endDate", sdf.format(ftNodeMonitorDistribute.getEndDate()));
        }
        Page<FtNodeMonitorDistribute> page = ftNodeMonitorDistributeService.findPage(new Page<FtNodeMonitorDistribute>(request, response), ftNodeMonitorDistribute);
        resultMap.put("page", page);
        return ResultDtoTool.buildSucceed("success", resultMap);
    }

    //文件推送详情
    /*@RequiresPermissions("NodeMonitor:monitorHistory:view")*/
    @GetMapping(value = "/monitorlogForPushOne")
    public ResultDto<? extends Object> monitorlogForPushOne(FtNodeMonitorPush ftNodeMonitorPush) {
        FtNodeMonitorPush ftNodeMonitorPushTemp = null;
        try {
            ftNodeMonitorPushTemp = ftNodeMonitorPushService.get(ftNodeMonitorPush);
        } catch (Exception e) {
            return ResultDtoTool.buildError("9999","查询文件信息失败！");
        }

        return ResultDtoTool.buildSucceed("success", ftNodeMonitorPushTemp);
    }

    //文件重新推送
    @RequiresPermissions("NodeMonitor:monitorHistory:repush")
    @RequestMapping(value = "monitorlogForRePushOne")
    public String monitorlogForRePushOne(FtNodeMonitorPush ftNodeMonitorPush, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtNodeMonitorPush ftNodeMonitorPushTemp = null;
        try {
            ftNodeMonitorPushTemp = ftNodeMonitorPushService.get(ftNodeMonitorPush);
        } catch (Exception e) {
            //addMessage(redirectAttributes, "查询文件信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String result = ftNodeMonitorPushService.repush2Datenode(ftNodeMonitorPushTemp);
        //addMessage(redirectAttributes, result);
        return "redirect:" + Global.getAdminPath() + "/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForPush";
    }

    //文件分发详情
    /*@RequiresPermissions("NodeMonitor:monitorHistory:view")*/
    @GetMapping(value = "/monitorlogForDistributeOne")
    public ResultDto<? extends Object> monitorlogForDistributeOne(FtNodeMonitorDistribute ftNodeMonitorDistribute) {
        FtNodeMonitorDistribute ftNodeMonitorDistributeTemp = null;
        try {
            ftNodeMonitorDistributeTemp = ftNodeMonitorDistributeService.get(ftNodeMonitorDistribute);
        } catch (Exception e) {
            //addMessage(redirectAttributes, "查询文件信息失败！详情：" + e.getMessage());
            return ResultDtoTool.buildError("9999","查询文件信息失败！");
        }

        return ResultDtoTool.buildSucceed("success",ftNodeMonitorDistributeTemp);
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