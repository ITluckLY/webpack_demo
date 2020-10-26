package com.dc.smarteam.modules.servicenode.web;

import com.dc.smarteam.common.config.Cfg;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.CacheUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sys.entity.OptTag;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础配置-节点管理-当前系统：切换当前节点组
 * <p>
 * Created by huangzbb on 2017/11/2.
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/servicenode/ftServiceNode")
public class FtNodeGroupController{

    @Resource(name = "FtSysInfoServiceImpl")
    private FtSysInfoService ftSysInfoService;
    @Resource
    private Cfg cfg;

    private String nameNodeIP;
    private String nameNodePort;
    private String nameNodeName;
    private String nameNodeType;

    @PostConstruct
    public void init() {
        nameNodeIP = cfg.getNameNodeIP();
        nameNodePort = cfg.getNameNodePort();
        nameNodeName = cfg.getNameNodeName();
        nameNodeType = cfg.getNameNodeType();
    }

//    @RequiresPermissions("servicenode:ftServiceNodeSet:view")
    @RequestMapping(value = "nodeList")
    public Object nodeList(HttpServletRequest request, HttpServletResponse response) {

        nameNodeIP = cfg.getNameNodeIP();
        nameNodePort = cfg.getNameNodePort();
        nameNodeName = cfg.getNameNodeName();

        FtServiceNode ftServiceNodeNameNode = new FtServiceNode();
        ftServiceNodeNameNode.setName(nameNodeName);
        ftServiceNodeNameNode.setIpAddress(nameNodeIP);
        ftServiceNodeNameNode.setCmdPort(nameNodePort);
        ftServiceNodeNameNode.setType(nameNodeType);
        request.getSession().setAttribute("ftServiceNodeNameNode", ftServiceNodeNameNode);//NOSONAR

        List<FtSysInfo> ftSysInfoList = ftSysInfoService.findList(new FtSysInfo());

        String loginUsername = (String) request.getSession().getAttribute("loginUsername");
        if (null == loginUsername || loginUsername.isEmpty()) {
            return ResultDtoTool.buildError("session失效,请重新登录");
        }
        List<FtSysInfo> ftSysInfoList2 = new ArrayList<>();
        for (FtSysInfo fsi : ftSysInfoList) {
            if (null != fsi.getAdmin() && !"".equals(fsi.getAdmin()) && fsi.getAdmin().contains(loginUsername)) {
                ftSysInfoList2.add(fsi);
            }
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("ftSysInfoList", ftSysInfoList2);
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode != null) {
            resultMap.put("ftServiceNode", ftServiceNode);//NOSONAR
            ftServiceNodeNameNode.setSystemName(ftServiceNode.getSystemName());
        } else {
            resultMap.put("ftServiceNode", ftServiceNodeNameNode);
        }
        OptTag optTag = (OptTag) CacheUtils.get("tag_type");
        String message = "";
        if(optTag==null){
            message = "当前未设置操作标签";
        }else{
            message = "当前操作标签: "+optTag.getName();
        }
        return ResultDtoTool.buildSucceed(message,resultMap);
    }

    @RequiresPermissions("servicenode:ftServiceNodeSet:view")
    @RequestMapping(value = "set")
    public String set(FtServiceNode ftServiceNode, FtSysInfo ftSysInfo, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        nameNodeIP = cfg.getNameNodeIP();
        nameNodePort = cfg.getNameNodePort();
        nameNodeName = cfg.getNameNodeName();

        //共享一个数据:用于后面使用这个主节点配置
        FtServiceNode ftServiceNodeTemp = new FtServiceNode();
        if (this.nameNodeIP != null) {
            ftServiceNodeTemp.setIpAddress(this.nameNodeIP);
            ftServiceNodeTemp.setCmdPort(this.nameNodePort);
        }
        request.getSession().setAttribute("ftServiceNodeBigAmount", ftServiceNodeTemp);

        if (this.nameNodeName != null && this.nameNodeIP != null && this.nameNodePort != null) {
            ftServiceNode.setName(this.nameNodeName);
            ftServiceNode.setIpAddress(this.nameNodeIP);
            ftServiceNode.setCmdPort(this.nameNodePort);
            ftServiceNode.setType(nameNodeType);
        }
        if (!((null == ftServiceNode.getSystemName()) || "".equals(ftServiceNode.getSystemName()))) {
            model.addAttribute("message", "设置成功");
        } else {
            model.addAttribute("ftServiceNode", ftServiceNode);
            return "modules/servicenode/ftServiceNodeSet";
        }
        ftSysInfo.setName(ftServiceNode.getSystemName());
        request.getSession().setAttribute("ftSysInfo", ftSysInfo);
        request.getSession().setAttribute("ftServiceNode", ftServiceNode);
        model.addAttribute("ftServiceNode", ftServiceNode);
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        if (ftServiceNodeNameNode == null) {
            request.getSession().setAttribute("ftServiceNodeNameNode", ftServiceNode);
        } else {
            ftServiceNodeNameNode.setSystemName(ftServiceNode.getSystemName());
        }
        CurrNameNodeHelper.setCurrSysname(request, ftServiceNode.getSystemName());
        OptTag optTag = (OptTag) CacheUtils.get("tag_type");
        if(optTag==null){
            model.addAttribute("tagMsg","当前未设置操作标签");
        }else{
            model.addAttribute("tagMsg","当前操作标签: "+optTag.getName());
        }
        return "modules/servicenode/ftServiceNodeSet";
    }
}
