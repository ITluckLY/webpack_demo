/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.flow.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ComponentModel;
import com.dc.smarteam.cfgmodel.FlowModel;
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
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.ComponentService;
import com.dc.smarteam.service.FlowService;
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
 * 流程管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/flow/ftFlow")
public class FtFlowController extends BaseController {

    @Resource
    private FlowService flowService;
    @Resource
    private ComponentService componentService;

    @RequiresPermissions("flow:ftFlow:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtFlow ftFlow, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {//NOSONAR

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ftFlow.setSystemName(ftServiceNode.getSystemName());

        ResultDto<List<FlowModel.Flow>> resultDto = flowService.listAll();
        List<FtFlow> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<FlowModel.Flow> flows = resultDto.getData();
            for (FlowModel.Flow flow : flows) {
                //按查询条件筛选
                String flowSysname = flow.getSysname().replaceAll("\n", "").trim();
                flow.setSysname(flowSysname);
                if (!"*".equals(flowSysname) && !StringUtils.equalsIgnoreCase(flowSysname, ftFlow.getSystemName())) continue;
                if (StringUtils.isNoneEmpty(ftFlow.getName()) && !StringUtils.containsIgnoreCase(flow.getName(), ftFlow.getName())) continue;
                if (StringUtils.isNoneEmpty(ftFlow.getDes()) && !StringUtils.containsIgnoreCase(flow.getDescribe(), ftFlow.getDes())) continue;
                FtFlow ftFlow2 = new FtFlow();
                CfgModelConverter.convertTo(flow, ftFlow2);
                ftFlow2.setId(String.valueOf(list.size()));
                list.add(ftFlow2);
            }
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";

        }
        PageHelper.getInstance().getPage(ftFlow.getClass(), request, response, model, list);
        return "modules/flow/ftFlowList";
    }

    @RequiresPermissions("flow:ftFlow:view")
    @RequestMapping(value = "form")
    public String form(FtFlow ftFlow, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        if (StringUtils.isEmpty(ftFlow.getName())) {
            return "modules/flow/ftFlowForm";
        }

        FtFlow entity = new FtFlow();
        ResultDto<FlowModel.Flow> resultDto = flowService.selByName(ftFlow);
        if (ResultDtoTool.isSuccess(resultDto)) {
            FlowModel.Flow flow = resultDto.getData();
            CfgModelConverter.convertTo(flow, entity);
            entity.setId("1");
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/flow/ftFlow/?repage";
        }
        model.addAttribute("ftFlow", entity);
        return "modules/flow/ftFlowEditForm";
    }

    @RequiresPermissions("flow:ftFlow:edit")
    @RequestMapping(value = "save")
    public String save(FtFlow ftFlow, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ftFlow.setSystemName("*".equals(ftFlow.getSystemName()) ? "*" : ftServiceNode.getSystemName());
        ResultDto<String> resultDto;
        if (StringUtils.isEmpty(ftFlow.getId())) {
            resultDto = flowService.add(ftFlow);
        } else {
            resultDto = flowService.update(ftFlow);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存流程管理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/flow/ftFlow/?repage";
    }

    @RequiresPermissions("flow:ftFlow:edit")
    @RequestMapping(value = "delete")
    public String delete(FtFlow ftFlow, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        ResultDto<String> resultDto = flowService.del(ftFlow);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除流程管理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/flow/ftFlow/?repage";
    }

    @RequiresPermissions("flow:ftFlow:view")
    @RequestMapping(value = "component")
    public String component(FtFlow ftFlow, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        ResultDto<List<ComponentModel.Service>> dto = componentService.listAll();
        List<FtComponent> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(dto)) {
            List<ComponentModel.Service> compservices = dto.getData();
            for (ComponentModel.Service compservice : compservices) {
                FtComponent ftComponent = new FtComponent();
                CfgModelConverter.convertTo(compservice, ftComponent);
                list.add(ftComponent);
            }
        } else {
            addMessage(redirectAttributes, dto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/flow/ftFlow/?repage";
        }
        model.addAttribute("ftFlow", ftFlow);
        model.addAttribute("ftComponentList", list);
        return "modules/flow/ftFlowComponent";
    }

    @RequiresPermissions("flow:ftFlow:view")
    @RequestMapping(value = "confComp")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("flow", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().flow(new FtFlow(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/flow/ftFlowConfComp";
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }


    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().flow(new FtFlow(), "print");//生成查询报文
        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/flow/ftFlowOtherConf";
    }

}