/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.protocol.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.cons.GlobalCons;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.protocol.service.SysProtocolService;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.modules.sys.service.DictService;
import com.dc.smarteam.service.RouteService;
import com.dc.smarteam.service.ServiceInfoService;
import com.dc.smarteam.service.SysService;
import com.dc.smarteam.util.CollectionUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kern
 * @version 2015-12-24
 */
@Controller
@RequestMapping(value = "${adminPath}/protocol/sysProtocol")
public class SysProtocolController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SysProtocolController.class);

    @Autowired
    private SysProtocolService sysProtocolService;
    @Autowired
    private DictService dictService;
    @Autowired
    private SysService sysService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private ServiceInfoService serviceInfoService;

    @RequiresPermissions("protocol:sysProtocol:view")
    @RequestMapping(value = {"list", ""})
    public String list(SysProtocol sysProtocol, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";//NOSONAR
        }
        List<SysProtocol> list = new ArrayList<>();

        ResultDto<List<SystemModel.System>> dto = sysService.listAll();
        if (ResultDtoTool.isSuccess(dto)) {
            List<SystemModel.System> systems = dto.getData();
            String sysProtocolName = sysProtocol.getName();
            for (SystemModel.System system : systems) {
                if (StringUtils.isNoneEmpty(sysProtocolName) && !StringUtils.containsIgnoreCase(system.getName(), sysProtocolName))
                    continue;
                SysProtocol newSysProtocol = new SysProtocol();
                CfgModelConverter.convertTo(system, newSysProtocol);
                newSysProtocol.setId(String.valueOf(list.size()));
                list.add(newSysProtocol);
            }
        } else {
            addMessage(redirectAttributes, dto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        PageHelper.getInstance().getPage(sysProtocol.getClass(), request, response, model, list);
        return "modules/protocol/sysProtocolList";
    }

    @RequiresPermissions("protocol:sysProtocol:view")
    @RequestMapping(value = "addPage")
    public String addPage(SysProtocol sysProtocol, HttpServletRequest request, Model model) {
        request.getSession().setAttribute("new", "new");
        Dict dict = new Dict();
        dict.setType(GlobalCons.PROTOCAL_TYPE);
        List<Dict> dictList = this.dictService.findList(dict);
        model.addAttribute("dictList", dictList);
        return "modules/protocol/sysProtocolForm";
    }

    @RequiresPermissions("protocol:sysProtocol:edit")
    @RequestMapping(value = "save")
    public String save(SysProtocol sysProtocol, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

        ResultDto<String> resultDto = sysService.add(sysProtocol);
//        ResultDto<String> resultDto2 = routeService.addRoutes(sysProtocol.getName());
        List<String> ftServiceInfoTranCodeList = serviceInfoService.selAllTranCodeBySameSys(sysProtocol.getUsername().trim());
        ResultDto<List<RouteModel.Route>> routeDto = routeService.listAll();
        for(RouteModel.Route route:routeDto.getData()){
            if(ftServiceInfoTranCodeList.contains(route.getTranCode())){
                FtRoute ftRoute = new FtRoute();
                CfgModelConverter.convertTo(route,ftRoute);
                ftRoute.setDestination(CollectionUtil.addOne(ftRoute.getDestination(),sysProtocol.getName(),","));
                routeService.update(ftRoute);
            }
        }
        if (ResultDtoTool.isSuccess(resultDto) ) {
            addMessage(redirectAttributes, "保存路由目标成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/protocol/sysProtocol/?repage";//NOSONAR
    }

    @RequiresPermissions("protocol:sysProtocol:edit")
    @RequestMapping(value = "saveEdit")
    public String saveEdit(SysProtocol sysProtocol, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        ResultDto<String> resultDto = sysService.update(sysProtocol);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存系统信息成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/protocol/sysProtocol/?repage";
    }

    @RequiresPermissions("protocol:sysProtocol:edit")
    @RequestMapping(value = "delete")
    public String delete(SysProtocol sysProtocol, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (sysProtocol.getName() != null) {
            ResultDto<String> resultDto = sysService.del(sysProtocol);
            //ResultDto<String> resultDto2 =routeService.delRoutes(sysProtocol.getName());
            List<FtRoute> ftRouteList = routeService.selByDestination(sysProtocol.getName());
            ResultDto<String> resultDto2 = new ResultDto<>();
            for(FtRoute ftRoute:ftRouteList){
                ftRoute.setDestination(CollectionUtil.removeOne(ftRoute.getDestination(),sysProtocol.getName(),","));
                resultDto2 = routeService.update(ftRoute);
                if(!ResultDtoTool.isSuccess(resultDto2)){
                    addMessage(redirectAttributes, resultDto2.getMessage());
                }
            }
            if (ResultDtoTool.isSuccess(resultDto)  ) {
                addMessage(redirectAttributes, "删除路由目标成功");
            } else {
                addMessage(redirectAttributes, resultDto.getMessage());
            }
        }
        return "redirect:" + Global.getAdminPath() + "/protocol/sysProtocol/?repage";
    }

    @RequiresPermissions("protocol:sysProtocol:view")
    @RequestMapping(value = "form")
    public String form(SysProtocol sysProtocol, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.isEmpty(sysProtocol.getName())) {
            return "modules/protocol/sysProtocolEditForm";
        }
        ResultDto<SystemModel.System> dto = sysService.selByName(sysProtocol);
        SysProtocol newSysProtocol = new SysProtocol();
        if (ResultDtoTool.isSuccess(dto)) {
            SystemModel.System system = dto.getData();
            CfgModelConverter.convertTo(system, newSysProtocol);
        } else {
            addMessage(redirectAttributes, dto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/protocol/sysProtocol/?repage";
        }
        model.addAttribute("sysProtocol", newSysProtocol);
        return "modules/protocol/sysProtocolEditForm";
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().system(new SysProtocol(), "print");//生成查询报文

        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/protocol/sysProtocolOtherConf";
    }

    @RequiresPermissions("protocol:sysProtocol:view")
    @RequestMapping(value = "telnet")
    @ResponseBody
    public String telnet(String ip, String port) throws IOException, InterruptedException {
        SocketAddress socketAddress = new InetSocketAddress(ip, Integer.valueOf(port));
        try (Socket socket = new Socket()) {
            socket.connect(socketAddress, 1000);
            OutputStream out = socket.getOutputStream();
            byte[] bytes = new byte[]{0, 0, 0, 0};
            out.write(bytes);
            out.flush();
            Thread.sleep(1000);
            log.info("连通成功{}:{}", ip, port);
            return "连通成功";
        } catch (Exception e) {
            log.warn("连通失败{}:{},{}", ip, port, e.getMessage());
            return e.getMessage();
        }
    }
}