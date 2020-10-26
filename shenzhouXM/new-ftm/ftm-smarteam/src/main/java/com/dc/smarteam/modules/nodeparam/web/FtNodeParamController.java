/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.nodeparam.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.FtNodeParamMsgGen;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;
import com.dc.smarteam.modules.nodeparam.service.FtNodeParamService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.servicenode.service.FtServiceNodeService;
import com.dc.smarteam.util.PublicRepResultTool;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 节点参数Controller
 *
 * @author liwang
 * @version 2016-01-11
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/nodeparam/ftNodeParam")
public class FtNodeParamController extends BaseController {

    @Resource(name = "FtNodeParamServiceImpl")
    private FtNodeParamService ftNodeParamService;
    @Resource(name = "FtServiceNodeServiceImpl")
    private FtServiceNodeService ftServiceNodeService;

//    @RequiresPermissions("nodeparam:ftNodeParam:view")
    @RequestMapping(value = {"list"})
    public Object list(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        String message = "";
        FtNodeParam ftNodeParam = new FtNodeParam();
        request.getSession().setAttribute("ftServiceNodeParam", ftServiceNode);
        List<FtNodeParam> list = new ArrayList<FtNodeParam>();
        if (null != ftServiceNode.getState() && ftServiceNode.getState().trim().equalsIgnoreCase("1")) {
            String allStr = MessageFactory.getInstance().nodeParam(ftNodeParam, "select");
            TCPAdapter tcpAdapter = new TCPAdapter();
            ResultDto<String> resultDto = tcpAdapter.invoke(allStr, ftServiceNode, String.class);//发送报文
            if (ResultDtoTool.isSuccess(resultDto)) {
                String data = resultDto.getData();
                List<FtNodeParam> subList = JsonToEntityFactory.getInstance().getNodeParamList(data, ftServiceNode);
                list.addAll(subList);
            } else {
                message = resultDto.getMessage();
                return PublicRepResultTool.sendResult("9999",message,null);
            }
        } else {
            message = "节点未连接，请检查连接状态后，重新再试！";
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("ftNodeParamList", list);
        return PublicRepResultTool.sendResult("0000",message,resultMap);
    }

    @RequiresPermissions("nodeparam:ftNodeParam:view")
    @RequestMapping(value = {"baseList"})
    public String baseList(FtNodeParam ftNodeParam, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null || ftServiceNode.getSystemName() == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String allStr = MessageFactory.getInstance().nodeParam(ftNodeParam, "select");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(allStr, ftServiceNode, String.class);//发送报文
        List<FtNodeParam> list = new ArrayList<FtNodeParam>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            List<FtNodeParam> subList = JsonToEntityFactory.getInstance().getNodeParamList(data, ftServiceNode);
            list.addAll(subList);
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        PageHelper.getInstance().getPage(ftNodeParam.getClass(), request, response, model, list);
        return "modules/nodeparam/ftNodeParamBaseList";
    }


    @RequiresPermissions("nodeparam:ftNodeParam:view")
    @RequestMapping(value = "form")
    public String addForm(FtNodeParam ftNodeParam, Model model, HttpServletRequest request) {
        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeParam");
        ftNodeParam.setNodeId(node.getName());
        model.addAttribute("ftServiceNode", node);
        return "modules/nodeparam/ftNodeParamAddForm";
    }

    @RequiresPermissions("nodeparam:ftNodeParam:view")
    @RequestMapping(value = "editForm")
    public String editForm(FtNodeParam ftNodeParam, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        FtServiceNode node= (FtServiceNode) request.getSession().getAttribute("ftServiceNodeParam");
        String selectStr = FtNodeParamMsgGen.select(ftNodeParam);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(selectStr, node, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            ftNodeParam = JsonToEntityFactory.getInstance().getFtNodeParam(JSONObject.fromObject(data), node);
            ftNodeParam.setNodeId(node.getName());
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/nodeparam/ftNodeParam/?repage";
        }
        model.addAttribute("ftNodeParam", ftNodeParam);
        return "modules/nodeparam/ftNodeParamEditForm";
    }

    @RequiresPermissions("nodeparam:ftNodeParam:edit")
    @RequestMapping(value = "saveAdd")
    public String saveAdd(FtServiceNode ftServiceNode, FtNodeParam ftNodeParam, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeParam");
        String addStr = FtNodeParamMsgGen.add(ftNodeParam);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(addStr, node, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "添加节点参数成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/nodeparam/ftNodeParam/list?name=" + node.getName() + "&ipAddress=" + node.getIpAddress() + "&cmdPort=" + node.getCmdPort() + "&state=" + node.getState();
    }

    @RequiresPermissions("nodeparam:ftNodeParam:edit")
    @RequestMapping(value = "saveEdit")
    public String saveEdit(FtNodeParam ftNodeParam, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String editStr = FtNodeParamMsgGen.update(ftNodeParam);
        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeParam");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(editStr, node, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "修改节点参数成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/nodeparam/ftNodeParam/list?name=" + node.getName() + "&ipAddress=" + node.getIpAddress() + "&cmdPort=" + node.getCmdPort() + "&state=" + node.getState();
    }

    @RequiresPermissions("nodeparam:ftNodeParam:edit")
    @RequestMapping(value = "delete")
    public String delete(FtNodeParam ftNodeParam, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeParam");
        String delStr = FtNodeParamMsgGen.del(ftNodeParam);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(delStr, node, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除节点参数成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/nodeparam/ftNodeParam/list?name=" + node.getName() + "&ipAddress=" + node.getIpAddress() + "&cmdPort=" + node.getCmdPort() + "&state=" + node.getState();
    }

}