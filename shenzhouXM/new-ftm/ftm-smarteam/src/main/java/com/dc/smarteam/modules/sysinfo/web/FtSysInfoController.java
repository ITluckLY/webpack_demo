/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysinfo.web;

import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sys.entity.User;
import com.dc.smarteam.modules.sys.service.SystemService;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import com.dc.smarteam.service.NodesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统管理Controller
 *
 * @author lvchuan
 * @version 2016-06-24
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/sysInfo/ftSysInfo")
public class FtSysInfoController extends BaseController {

    @Resource(name = "FtSysInfoServiceImpl")
    private FtSysInfoService ftSysInfoService;

    @Resource(name ="SystemServiceImpl")
    private SystemService systemService;

    @Resource(name = "NodesServiceImpl")
    private NodesService nodesService;

    @ModelAttribute
    public FtSysInfo get(@RequestParam(required = false) String id) {
        FtSysInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = ftSysInfoService.get(id);
        }
        if (entity == null) {
            entity = new FtSysInfo();
        }
        return entity;
    }
//
//    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = {"list", ""})
    public ResultDto<Map<String,Object>> list(FtSysInfo ftSysInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
        log.debug("ftSysInfo:{}, pageNo:{}, pageSize:{}, repage:{}, orderBy:{}",ftSysInfo,request.getParameter("pageNo"),
                request.getParameter("pageSize"), request.getParameter("repage"), request.getParameter("orderBy"));
        Page<FtSysInfo> page = ftSysInfoService.findPage(new Page<FtSysInfo>(request, response), ftSysInfo);
        ftSysInfo.setPage(page);
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("page", page);
        log.debug("业务系统管理列表信息:{}", resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = "addPage")
    public ResultDto<Map<String,Object>> addPage(FtSysInfo ftSysInfo) {
        log.debug("ftSysInfo:{}", ftSysInfo);
        ftSysInfo = new FtSysInfo();
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("ftSysInfo", ftSysInfo);
        log.debug("resultMap:{}", resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

////    @RequiresPermissions("sysInfo:ftSysInfo:setAdmin")
    @RequestMapping(value = "addAdmin")
    public ResultDto<Map<String,Object>> addAdmin(FtSysInfo ftSysInfo, HttpServletRequest request, HttpServletResponse response) {
        log.debug("ftStsInfo:{}", ftSysInfo);
        ftSysInfo = ftSysInfoService.get(ftSysInfo);
        User user = new User();
        Page<User> page = systemService.findUser(new Page<User>(request, response), user);
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("ftSysInfo", ftSysInfo);
        resultMap.put("page", page);
        log.debug("resultMap:{}", resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

////    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = "form")
    public ResultDto<Map<String,Object>> form(FtSysInfo ftSysInfo, Model model, HttpServletRequest request) {
        log.debug("ftSysInfo:{}",ftSysInfo);
        if (null != ftSysInfo.getId() && !ftSysInfo.getId().isEmpty()) {
            ftSysInfo = ftSysInfoService.get(ftSysInfo);
        }
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("ftSysInfo", ftSysInfo);
        log.debug("节点组信息:{}",ftSysInfo);
        return ResultDtoTool.buildSucceed(resultMap);
    }

////    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "save")
    public ResultDto save(FtSysInfo ftSysInfo, Model model, HttpServletRequest request) {
        if (!beanValidator(model, ftSysInfo)) {
            return form(ftSysInfo, model, request);
        }
        if (null != ftSysInfo && !(ftSysInfo.getSysNodeModel().equals("ms"))) {
            ftSysInfo.setSwitchModel(null);
        }
        ftSysInfoService.save(ftSysInfo);
        return ResultDtoTool.buildSucceed("保存系统管理成功",null);
    }


//    @RequiresPermissions("sysInfo:ftSysInfo:setAdmin")
    @RequestMapping(value = "saveSystem")
    public ResultDto saveSystem(FtSysInfo ftSysInfo, Model model, HttpServletRequest request) {
        log.debug("ftSysInfo:{}", ftSysInfo);
        if (!beanValidator(model, ftSysInfo)) {
            return form(ftSysInfo, model, request);
        }
        String message = "";
        if (request.getParameterValues("admin") == null) {
            message = "无管理员添加记录，请重新添加！";
        } else {
            FtSysInfo ftSysInfoTemp = ftSysInfoService.get(ftSysInfo);
            ftSysInfoTemp.setAdmin(ftSysInfo.getAdmin());
            ftSysInfoService.save(ftSysInfoTemp);
            message = "保存系统管理成功";
        }
        log.debug("message:{}", message);
        return ResultDtoTool.buildSucceed(message,null);
    }

//    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "saveEdit")
    public ResultDto saveEdit(FtSysInfo ftSysInfo, Model model, HttpServletRequest request) {
        log.debug("ftSysInfo:{}",ftSysInfo);
        if (!beanValidator(model, ftSysInfo)) {
            return form(ftSysInfo, model, request);
        }
        ftSysInfoService.save(ftSysInfo);
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setSystemName(ftSysInfo.getName());
        ftServiceNode.setSwitchModel(ftSysInfo.getSwitchModel());
        ftServiceNode.setStoreModel(ftSysInfo.getStoreModel());
        ResultDto<Boolean> resultDto = nodesService.switchSystemModel(ftServiceNode);

        String message = "";
        if (ResultDtoTool.isSuccess(resultDto)) {
            message = "修改系统成功";
        } else {
            message = resultDto.getMessage();
        }
        log.debug("message:{}",message);
        return ResultDtoTool.buildSucceed(message,null);
    }

//    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "delete")
    public ResultDto delete(FtSysInfo ftSysInfo) {
        log.debug("ftSysInfo:{}", ftSysInfo);
        ftSysInfoService.delete(ftSysInfo);
        String message = "删除系统目录成功";
        log.debug("message:{}", message);
        return  ResultDtoTool.buildSucceed(message,null);
    }

}