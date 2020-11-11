/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysinfo.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sys.entity.User;
import com.dc.smarteam.modules.sys.service.SystemService;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import com.dc.smarteam.service.NodesService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 系统管理Controller
 *
 * @author lvchuan
 * @version 2016-06-24
 */
@Controller
@RequestMapping(value = "${adminPath}/sysInfo/ftSysInfo")
public class FtSysInfoController extends BaseController {

    @Autowired
    private FtSysInfoService ftSysInfoService;
    @Autowired
    private SystemService systemService;
    @Autowired
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

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtSysInfo ftSysInfo, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        Page<FtSysInfo> page = ftSysInfoService.findPage(new Page<FtSysInfo>(request, response), ftSysInfo);
        ftSysInfo.setPage(page);
        model.addAttribute("page", page);
        return "modules/sysinfo/ftSysInfoList";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = "addPage")
    public String addPage(FtSysInfo ftSysInfo, Model model) {

        ftSysInfo = new FtSysInfo();
        model.addAttribute("ftSysInfo", ftSysInfo);
        return "modules/sysinfo/ftSysInfoForm";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:setAdmin")
    @RequestMapping(value = "addAdmin")
    public String addAdmin(FtSysInfo ftSysInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

        ftSysInfo = ftSysInfoService.get(ftSysInfo);
        User user = new User();
        Page<User> page = systemService.findUser(new Page<User>(request, response), user);
        model.addAttribute("ftSysInfo", ftSysInfo);
        model.addAttribute("page", page);
        return "modules/sysinfo/addManager";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = "form")
    public String form(FtSysInfo ftSysInfo, Model model, HttpServletRequest request) {

        if (null != ftSysInfo.getId() && !ftSysInfo.getId().isEmpty()) {
            ftSysInfo = ftSysInfoService.get(ftSysInfo);
        }
        model.addAttribute("ftSysInfo", ftSysInfo);
        return "modules/sysinfo/ftSysInfoEditForm";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "save")
    public String save(FtSysInfo ftSysInfo, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, ftSysInfo)) {
            return form(ftSysInfo, model, request);
        }
        if (null != ftSysInfo && !(ftSysInfo.getSysNodeModel().equals("ms"))) {
            ftSysInfo.setSwitchModel(null);
        }
        ftSysInfoService.save(ftSysInfo);
        addMessage(redirectAttributes, "保存系统管理成功");
        return "redirect:" + Global.getAdminPath() + "/sysInfo/ftSysInfo/?repage";
    }


    @RequiresPermissions("sysInfo:ftSysInfo:setAdmin")
    @RequestMapping(value = "saveSystem")
    public String saveSystem(FtSysInfo ftSysInfo, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, ftSysInfo)) {
            return form(ftSysInfo, model, request);
        }
        if (request.getParameterValues("admin") == null) {
            addMessage(redirectAttributes, "无管理员添加记录，请重新添加！");
            return "redirect:" + Global.getAdminPath() + "/sysInfo/ftSysInfo/?repage";
        } else {
            FtSysInfo ftSysInfoTemp = ftSysInfoService.get(ftSysInfo);
            ftSysInfoTemp.setAdmin(ftSysInfo.getAdmin());
            ftSysInfoService.save(ftSysInfoTemp);
            addMessage(redirectAttributes, "保存系统管理成功");
        }
        return "redirect:" + Global.getAdminPath() + "/sysInfo/ftSysInfo/?repage";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "saveEdit")
    public String saveEdit(FtSysInfo ftSysInfo, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, ftSysInfo)) {
            return form(ftSysInfo, model, request);
        }
        ftSysInfoService.save(ftSysInfo);
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setSystemName(ftSysInfo.getName());
        ftServiceNode.setSwitchModel(ftSysInfo.getSwitchModel());
        ftServiceNode.setStoreModel(ftSysInfo.getStoreModel());
        ResultDto<Boolean> resultDto = nodesService.switchSystemModel(ftServiceNode);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "修改系统成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/sysInfo/ftSysInfo/?repage";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "delete")
    public String delete(FtSysInfo ftSysInfo, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ftSysInfoService.delete(ftSysInfo);
        addMessage(redirectAttributes, "删除系统目录成功");
        return "redirect:" + Global.getAdminPath() + "/sysInfo/ftSysInfo/?repage";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = "checkSysName")
    @ResponseBody
    public String checkSysName(String name, String systemName) {
        if (name != null && name.equals(systemName)) {
            return "true";
        }
        if (name != null && ftSysInfoService.getByName(name) == null) {
            return "true";
        }
        return "false";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = {"confComp"})
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("system", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().system(new SysProtocol(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/sysinfo/ftSysToBackConfComp";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }

}