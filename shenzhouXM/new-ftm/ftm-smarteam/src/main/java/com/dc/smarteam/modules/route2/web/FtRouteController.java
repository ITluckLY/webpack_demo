/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.route2.web;

import com.dc.smarteam.cfgmodel.*;
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
import com.dc.smarteam.service.impl.RouteServiceImpl;
import com.dc.smarteam.service.impl.ServiceInfoServiceImpl;
import com.dc.smarteam.service.impl.SysServiceImpl;
import com.dc.smarteam.service.impl.UserServiceImpl;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *  路由管理-交易码路由管理
 */
@RestController
@RequestMapping(value = "${adminPath}/route/ftRoute")
public class FtRouteController extends BaseController {

    @Autowired
    private ServiceInfoServiceImpl serviceInfoService;
    @Autowired
    private RouteServiceImpl routeService;
    @Resource(name = "UserServiceImpl")
    private UserServiceImpl userService;
    @Autowired
    private SysServiceImpl sysService;

    @RequestMapping(value = {"list" ,""})
    public Object list(FtRoute ftRoute, HttpServletRequest request, HttpServletResponse response, Map map) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName() || ftServiceNode.getSystemName().isEmpty()) {
            return   ResultDtoTool.buildError("请先设置节点组！！！");
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
            return    ResultDtoTool.buildError(resultDto2.getMessage());
        }
        PageHelper.getInstance().getPage(ftRoute.getClass(), request, response, map, endList);
        return  ResultDtoTool.buildSucceed("成功",endList);
    }

    @RequestMapping(value = "addPage", produces = "application/json;charset=UTF-8")
    public Object addPage(FtRoute ftRoute, HttpServletRequest request) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        FtServiceInfo ftServiceInfo = new FtServiceInfo();
        ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
        ResultDto<List<ServiceModel.Service>> dto = serviceInfoService.listAll();
        List<FtServiceInfo> ftServiceInfoList = new ArrayList<FtServiceInfo>();
        Map<String,Object> res = new HashMap<>();
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
            return   ResultDtoTool.buildError(dto.getMessage());
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
        res.put("ftServiceInfoList", ftServiceInfoList);
        res.put("ftUserList", ftUserList);
        res.put("ftRoute", ftRoute);
        res.put("sysProtocolList", sysProtocolList);
        return  ResultDtoTool.buildSucceed("成功",res);
    }


    @RequestMapping(value = "form", produces = "application/json;charset=UTF-8")
    public Object form(FtRoute ftRoute) {
        ResultDto<RouteModel.Route> dto = routeService.selByTranscodeAndUser(ftRoute);
        List<String> routeNameList = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        if (ResultDtoTool.isSuccess(dto)) {
            RouteModel.Route route = dto.getData();
            CfgModelConverter.convertTo(route, ftRoute);
            String destination = ftRoute.getDestination();
            String[] split = destination.split(",");
            routeNameList.addAll(Arrays.asList(split));
        } else {
            return  ResultDtoTool.buildError(dto.getMessage());
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

        result.put("ftRoute", ftRoute);
        result.put("routeNameList", routeNameList);// 路由目标-已选中
        result.put("routeNameListTmp", routeNameListTmp);// 路由目标-未选中
        return   ResultDtoTool.buildSucceed("成功",result);
    }

    @RequestMapping(value = "save", produces = "application/json;charset=UTF-8")
    public Object save(FtRoute ftRoute ) {
        ResultDto<String> dto = routeService.add(ftRoute);
        String message="";
        if (!ResultDtoTool.isSuccess(dto)) {
             message = dto.getMessage();
        } else if (ResultDtoTool.isSuccess(dto)) {
            message = "保存路由管理成功";
        }
        return  ResultDtoTool.buildSucceed(message);
    }

    @RequestMapping(value = "saveEdit", produces = "application/json;charset=UTF-8")
    public Object saveEdit(FtRoute ftRoute ) {
        ResultDto<String> dto = routeService.update(ftRoute);
        String mes ="";
        if (!ResultDtoTool.isSuccess(dto)) {
             mes = dto.getMessage();
        } else if (ResultDtoTool.isSuccess(dto)) {
            mes ="修改路由管理成功";
        }
        return  ResultDtoTool.buildSucceed(mes);
    }

    @RequestMapping(value = "delete", produces = "application/json;charset=UTF-8")
    public Object delete(FtRoute ftRoute) {
        String message="";
        ResultDto<String> dto = routeService.del(ftRoute);
        ResultDto<String> resultDto = serviceInfoService.del(ftRoute);
        if (!ResultDtoTool.isSuccess(dto)) {
             message = dto.getMessage();
        } else if (ResultDtoTool.isSuccess(resultDto) && ResultDtoTool.isSuccess(dto)) {
            message = "删除路由管理成功";
        }
        return ResultDtoTool.buildSucceed(message);
    }

    /**
     *      以下 有点问题 ？？？
     * @param ftServiceNode
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "confComp", produces = "application/json;charset=UTF-8")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, Model model) {
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