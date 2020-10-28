/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.protocol.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.cfgmodel.SystemModel;
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
import com.dc.smarteam.service.impl.RouteServiceImpl;
import com.dc.smarteam.service.impl.ServiceInfoServiceImpl;
import com.dc.smarteam.service.impl.SysServiceImpl;
import com.dc.smarteam.util.CollectionUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * lyy
 * 2020.10.22
 */
@RestController
@RequestMapping(value = "${adminPath}/protocol/sysProtocol")
public class SysProtocolController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SysProtocolController.class);

    @Autowired
    private SysProtocolService sysProtocolService;
    @Resource(name = "DictServiceImpl")
    private DictService dictService;
    @Autowired
    private SysServiceImpl sysService;
    @Autowired
    private RouteServiceImpl routeService;
    @Autowired
    private ServiceInfoServiceImpl serviceInfoService;

    @RequestMapping(value =  {"list", ""})
    public Object list(SysProtocol sysProtocol, HttpServletRequest request, HttpServletResponse response, Map map) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
          return   ResultDtoTool.buildError("请先设置节点组！！！");
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
            return   ResultDtoTool.buildError(dto.getMessage());
        }
        PageHelper.getInstance().getPage(sysProtocol.getClass(), request, response, map, list);
        return  ResultDtoTool.buildSucceed("成功。",list);
    }


    @RequestMapping(value = "addPage", produces = "application/json;charset=UTF-8")
    public Object addPage(HttpServletRequest request) {
        request.getSession().setAttribute("new", "new");
        Dict dict = new Dict();
        Map<String,Object> res = new HashMap<>();
        dict.setType(GlobalCons.PROTOCAL_TYPE);
        List<Dict> dictList = this.dictService.findList(dict);
        res.put("dictList", dictList);
        return  ResultDtoTool.buildSucceed("成功。",res);
    }

    @RequestMapping(value = "save", produces = "application/json;charset=UTF-8")
    public Object save(SysProtocol sysProtocol) {

        ResultDto<String> resultDto = sysService.add(sysProtocol);
        List<String> ftServiceInfoTranCodeList = serviceInfoService.selAllTranCodeBySameSys(sysProtocol.getUsername().trim());
        ResultDto<List<RouteModel.Route>> routeDto = routeService.listAll();
        String ms ="";
        for(RouteModel.Route route:routeDto.getData()){
            if(ftServiceInfoTranCodeList.contains(route.getTranCode())){
                FtRoute ftRoute = new FtRoute();
                CfgModelConverter.convertTo(route,ftRoute);
                ftRoute.setDestination(CollectionUtil.addOne(ftRoute.getDestination(),sysProtocol.getName(),","));
                routeService.update(ftRoute);
            }
        }
        if (ResultDtoTool.isSuccess(resultDto) ) {
            ms = "保存路由目标成功";
        } else {
            ms = resultDto.getMessage() ;
        }

        return ResultDtoTool.buildSucceed(ms);
    }

    @RequestMapping(value = "saveEdit", produces = "application/json;charset=UTF-8")
    public Object saveEdit(SysProtocol sysProtocol) {
        ResultDto<String> resultDto = sysService.update(sysProtocol);
        String ms ="";
        if (ResultDtoTool.isSuccess(resultDto)) {
            ms ="保存系统信息成功";
        } else {
            ms = resultDto.getMessage();
        }
        return ResultDtoTool.buildSucceed(ms);
    }

    @RequestMapping(value = "delete", produces = "application/json;charset=UTF-8")
    public Object delete(SysProtocol sysProtocol) {
        String messages ="";
        if (sysProtocol.getName() != null) {
            ResultDto<String> resultDto = sysService.del(sysProtocol);
            //ResultDto<String> resultDto2 =routeService.delRoutes(sysProtocol.getName());
            List<FtRoute> ftRouteList = routeService.selByDestination(sysProtocol.getName());
            ResultDto<String> resultDto2 = new ResultDto<>();
            for(FtRoute ftRoute:ftRouteList){
                ftRoute.setDestination(CollectionUtil.removeOne(ftRoute.getDestination(),sysProtocol.getName(),","));
                resultDto2 = routeService.update(ftRoute);
                if(!ResultDtoTool.isSuccess(resultDto2)){
                    messages =resultDto2.getMessage();
                }
            }
            if (ResultDtoTool.isSuccess(resultDto)  ) {
                messages = "删除路由目标成功" ;
            } else {
                messages =resultDto.getMessage() ;
            }
        }
        return  ResultDtoTool.buildSucceed(messages);
    }



    @RequestMapping(value = "form" , produces = "application/json;charset=UTF-8")
    public Object form(SysProtocol sysProtocol) {
        if (StringUtils.isEmpty(sysProtocol.getName())) {
            return  ResultDtoTool.buildError("请先选择数据！！！");
        }
        ResultDto<SystemModel.System> dto = sysService.selByName(sysProtocol);
        Map<String,Object> res =new HashMap<>();
        SysProtocol newSysProtocol = new SysProtocol();
        if (ResultDtoTool.isSuccess(dto)) {
            SystemModel.System system = dto.getData();
            CfgModelConverter.convertTo(system, newSysProtocol);
        } else {
            return ResultDtoTool.buildError(dto.getMessage());
        }
        res.put("sysProtocol", newSysProtocol);
        return  ResultDtoTool.buildSucceed("成功",res);
    }


//    /**
//     *   此处有待修改？？？？？？？？？？？？？？？
//     * @param request
//     * @param model
//     * @return
//     */
//    @RequiresPermissions("servicenode:ftServiceNode:view")
//    @RequestMapping(value = "otherConf")
//    public String otherConf(HttpServletRequest request,Model model) {
//        String getAllStr = MessageFactory.getInstance().system(new SysProtocol(), "print");//生成查询报文
//
//        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
//        return "modules/protocol/sysProtocolOtherConf";
//    }



    @RequestMapping(value = "telnet", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResultDto telnet(String ip, String port) throws IOException, InterruptedException {
        SocketAddress socketAddress = new InetSocketAddress(ip, Integer.valueOf(port));
        try (Socket socket = new Socket()) {
            socket.connect(socketAddress, 1000);
            OutputStream out = socket.getOutputStream();
            byte[] bytes = new byte[]{0, 0, 0, 0};
            out.write(bytes);
            out.flush();
            Thread.sleep(1000);
            log.info("连通成功{}:{}", ip, port);

            return   ResultDtoTool.buildSucceed("连通成功");
        } catch (Exception e) {
            log.warn("连通失败{}:{},{}", ip, port, e.getMessage());
            return  ResultDtoTool.buildError(e.getMessage());
        }
    }
}