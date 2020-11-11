/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.component.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ComponentModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.component.entity.FtComponent;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.ComponentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 组件管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/component/ftComponent")
public class FtComponentController extends BaseController {

    @Resource
    private ComponentService componentService;

    @RequiresPermissions("component:ftComponent:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtComponent ftComponent, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        List<FtComponent> list = new ArrayList<FtComponent>();
        ResultDto<List<ComponentModel.Service>> dto = componentService.listAll();
        if (ResultDtoTool.isSuccess(dto)) {
            List<ComponentModel.Service> services = dto.getData();
            String ftComponentName = ftComponent.getName();
            for (ComponentModel.Service service : services) {
                if (StringUtils.isNoneEmpty(ftComponentName) && !StringUtils.containsIgnoreCase(service.getName(), ftComponentName)) continue;
                FtComponent ftComponent2 = new FtComponent();
                CfgModelConverter.convertTo(service, ftComponent2);
                ftComponent2.setId(String.valueOf(list.size()));
                list.add(ftComponent2);
            }
        } else {
            addMessage(redirectAttributes, dto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";

        }
//        model.addAttribute("ftComponentList", list2);
        PageHelper.getInstance().getPage(ftComponent.getClass(), request, response, model, list);
        return "modules/component/ftComponentList";
    }

    @RequiresPermissions("component:ftComponent:view")
    @RequestMapping(value = "form")
    public String form(FtComponent ftComponent, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (StringUtils.isEmpty(ftComponent.getName())) {
            return "modules/component/ftComponentForm";
        } else {
            ftComponent.setId("1111");
        }
        ResultDto<ComponentModel.Service> dto = componentService.selByName(ftComponent);
        FtComponent ftComponent2 = new FtComponent();
        if (ResultDtoTool.isSuccess(dto)) {
            ComponentModel.Service compsvc = dto.getData();
            CfgModelConverter.convertTo(compsvc, ftComponent2);
        } else {
            addMessage(redirectAttributes, dto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/component/ftComponent/?repage";
        }
        ftComponent2.setId(ftComponent.getId());
        model.addAttribute("ftComponent", ftComponent2);
        return "modules/component/ftComponentForm";
    }

    @RequiresPermissions("component:ftComponent:edit")
    @RequestMapping(value = "save")
    public String save(FtComponent ftComponent, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> resultDto;
        if (StringUtils.isEmpty(ftComponent.getId())) {
            resultDto = componentService.add(ftComponent);
        } else {
            resultDto = componentService.update(ftComponent);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存组件管理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/component/ftComponent/?repage";
    }

    @RequiresPermissions("component:ftComponent:edit")
    @RequestMapping(value = "delete")
    public String delete(FtComponent ftComponent, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        ResultDto<String> resultDto = componentService.del(ftComponent);
        if (ResultDtoTool.isSuccess(resultDto)) {

            addMessage(redirectAttributes, "删除组件管理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/component/ftComponent/?repage";
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().component(new FtComponent(), "print");//生成查询报文

        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/component/ftComponentOtherConf";
    }

    @RequiresPermissions("component:ftComponent:view")
    @RequestMapping(value = "confComp")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("components", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().component(new FtComponent(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/component/ftComponentConfComp";
    }

    @RequiresPermissions("serviceinfo:ftServiceInfo:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }
}