/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.route2.web;

import com.dc.smarteam.cfgmodel.*;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.RouteService;
import com.dc.smarteam.service.ServiceInfoService;
import com.dc.smarteam.service.SysService;
import com.dc.smarteam.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户管理Controller
 *
 * @author yangxc
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/route/ftRoute")
public class FtRouteController extends BaseController {

    @Autowired
    private ServiceInfoService serviceInfoService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysService sysService;

    @RequiresPermissions("route:ftRoute:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtRoute ftRoute, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName() || ftServiceNode.getSystemName().isEmpty()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        List<FtServiceInfo> ftServiceInfoList = serviceInfoService.getFtServiceInfoList();

        ResultDto<List<RouteModel.Route>> resultDto2 = routeService.listAll();
        List<FtRoute> list = new ArrayList<FtRoute>();
        List<FtRoute> endList = new ArrayList<FtRoute>();
        if (ResultDtoTool.isSuccess(resultDto2)) {
            List<RouteModel.Route> routes = resultDto2.getData();
            for (RouteModel.Route route : routes) {
                FtRoute ftRoute2 = new FtRoute();
                CfgModelConverter.convertTo(route, ftRoute2);
                ftRoute2.setId(String.valueOf(list.size()));
                if (StringUtils.isNoneEmpty(ftRoute.getTran_code()) && !StringUtils.containsIgnoreCase(ftRoute2.getTran_code(), ftRoute.getTran_code()))
                    continue;
                if (StringUtils.isNoneEmpty(ftRoute.getUser()) && !StringUtils.containsIgnoreCase(ftRoute2.getUser(), ftRoute.getUser()))
                    continue;
                list.add(ftRoute2);
            }

            for (FtRoute ftTemp : list) {
                for (FtServiceInfo fsi : ftServiceInfoList) {
                    if (fsi.getTrancode().equals(ftTemp.getTran_code()) && fsi.getSystemName().equalsIgnoreCase(ftServiceNode.getSystemName())) {
                        endList.add(ftTemp);
                    }
                }
            }
        } else {
            String data = resultDto2.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        PageHelper.getInstance().getPage(ftRoute.getClass(), request, response, model, endList);
        return "modules/route2/ftRouteList";
    }

    @RequiresPermissions("route:ftRoute:view")
    @RequestMapping(value = "addPage")
    public String addPage(FtRoute ftRoute, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        FtServiceInfo ftServiceInfo = new FtServiceInfo();
        ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
        ResultDto<List<ServiceModel.Service>> dto = serviceInfoService.listAll();
        List<FtServiceInfo> ftServiceInfoList = new ArrayList<FtServiceInfo>();
        if (ResultDtoTool.isSuccess(dto)) {
            List<ServiceModel.Service> services = dto.getData();
            for (ServiceModel.Service service : services) {
                if (service.getSysname().equalsIgnoreCase(ftServiceNode.getSystemName())) {
                    FtServiceInfo ftServiceInfo2 = new FtServiceInfo();
                    CfgModelConverter.convertToWithoutAuth(service, ftServiceInfo2);
                    ftServiceInfoList.add(ftServiceInfo2);
                }
            }
        } else {
            String data = dto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/route/ftRoute/?repage";
        }

        ResultDto<List<UserModel.UserInfo>> userDto = userService.listAll();
        List<FtUser> ftUserList = new ArrayList<FtUser>();
        if (ResultDtoTool.isSuccess(dto)) {
            List<UserModel.UserInfo> userInfos = userDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                FtUser user = new FtUser();
                CfgModelConverter.convertTo(userInfo, user);
                ftUserList.add(user);
            }
        }

        ResultDto<List<SystemModel.System>> sysDto = sysService.listAll();
        List<SysProtocol> sysProtocolList = new ArrayList<>();
        if (ResultDtoTool.isSuccess(sysDto)) {
            List<SystemModel.System> systems = sysDto.getData();
            for (SystemModel.System system : systems) {
                SysProtocol sysProtocol = new SysProtocol();
                CfgModelConverter.convertTo(system, sysProtocol);
                sysProtocolList.add(sysProtocol);
            }
        }

        model.addAttribute("ftServiceInfoList", ftServiceInfoList);
        model.addAttribute("ftUserList", ftUserList);
        model.addAttribute("ftRoute", ftRoute);
        model.addAttribute("sysProtocolList", sysProtocolList);
        return "modules/route2/ftRouteForm";
    }

    @RequiresPermissions("route:ftRoute:view")
    @RequestMapping(value = "form")
    public String form(FtRoute ftRoute, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<RouteModel.Route> dto = routeService.selByTranscodeAndUser(ftRoute);
        List<String> routeNameList = new ArrayList<>();
        if (ResultDtoTool.isSuccess(dto)) {
            RouteModel.Route route = dto.getData();
            CfgModelConverter.convertTo(route, ftRoute);
            String destination = ftRoute.getDestination();
            String[] split = destination.split(",");
            routeNameList.addAll(Arrays.asList(split));
        } else {
            String data = dto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/route/ftRoute/?repage";
        }

        ResultDto<List<SystemModel.System>> sysDto = sysService.listAll();
        List<String> routeNameListTmp = new ArrayList<>();
        if (ResultDtoTool.isSuccess(sysDto)) {
            List<SystemModel.System> systems = sysDto.getData();
            for (SystemModel.System system : systems) {
                SysProtocol sysProtocol = new SysProtocol();
                CfgModelConverter.convertTo(system, sysProtocol);
                routeNameListTmp.add(sysProtocol.getName());
            }
        }
        routeNameListTmp.removeAll(routeNameList);

        model.addAttribute("ftRoute", ftRoute);
        model.addAttribute("routeNameList", routeNameList);// 路由目标-已选中
        model.addAttribute("routeNameListTmp", routeNameListTmp);// 路由目标-未选中
        return "modules/route2/ftRouteEditForm";
    }

    @RequiresPermissions("route:ftRoute:edit")
    @RequestMapping(value = "save")
    public String save(FtRoute ftRoute, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> dto = routeService.add(ftRoute);
        if (!ResultDtoTool.isSuccess(dto)) {
            String message = dto.getMessage();
            addMessage(redirectAttributes, message);
        } else if (ResultDtoTool.isSuccess(dto)) {
            addMessage(redirectAttributes, "保存路由管理成功");
        }
        return "redirect:" + Global.getAdminPath() + "/route/ftRoute/?repage";
    }

    @RequiresPermissions("route:ftRoute:edit")
    @RequestMapping(value = "saveEdit")
    public String saveEdit(FtRoute ftRoute, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> dto = routeService.update(ftRoute);
        if (!ResultDtoTool.isSuccess(dto)) {
            String message = dto.getMessage();
            addMessage(redirectAttributes, message);
        } else if (ResultDtoTool.isSuccess(dto)) {
            addMessage(redirectAttributes, "修改路由管理成功");
        }
        return "redirect:" + Global.getAdminPath() + "/route/ftRoute/?repage";
    }

    @RequiresPermissions("route:ftRoute:edit")
    @RequestMapping(value = "delete")
    public String delete(FtRoute ftRoute, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> dto = routeService.del(ftRoute);
        ResultDto<String> resultDto = serviceInfoService.del(ftRoute);
        if (!ResultDtoTool.isSuccess(dto)) {
            String message = dto.getMessage();
            addMessage(redirectAttributes, message);
        } else if (ResultDtoTool.isSuccess(resultDto) && ResultDtoTool.isSuccess(dto)) {
            addMessage(redirectAttributes, "删除路由管理成功");
        }
        return "redirect:" + Global.getAdminPath() + "/route/ftRoute/?repage";
    }

    @RequiresPermissions("route:ftRoute:view")
    @RequestMapping(value = "confComp")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("route", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().route(new FtRoute(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/route2/ftRouteConfComp";
    }

    @RequiresPermissions("route:ftRoute:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }


    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().route(new FtRoute(), "print");//生成查询报文

        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/route2/ftRouteOtherConf";
    }
}