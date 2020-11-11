package com.dc.smarteam.modules.servicenode.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.google.gson.JsonObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 系统设置-系统管理-节点列表
 * <p>
 * Created by huangzbb on 2017/11/3.
 */
@Controller
@RequestMapping(value = "${adminPath}/servicenode/ftServiceNode")

public class FtNodeStartStopController extends BaseController {

    @RequiresPermissions("servicenode:ftServiceNode:startStop")
    @RequestMapping(value = "startDatanode")
    public String startDatanode(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.equals(ftServiceNode.getState(), "1")) {
            addMessage(redirectAttributes, "不能启动正在运行的节点");
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
        }
        String datanodeName = ftServiceNode.getName();
        Map<String, JsonObject> dataNodeMap = ZkService.getInstance().getDataNodeMap();
        for (String dnIpPort : dataNodeMap.keySet()) {
            JsonObject jsonObject = dataNodeMap.get(dnIpPort);
            String dataNodeNameTemp = jsonObject.get("nodeName").getAsString();
            if (StringUtils.equalsIgnoreCase(dataNodeNameTemp, datanodeName)) {
                addMessage(redirectAttributes, "不能启动正在运行的节点");
                return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
            }
        }
        String monitorIp = null;
        String monitorPort = null;
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeNameTemp = jsonObject.get("dnName").getAsString();
            monitorPort = jsonObject.get("mnCmdPort").getAsString();
            if (dataNodeNameTemp.equals(datanodeName)) {
                String[] split = mnIpPort.split(":");
                monitorIp = split[0];
                break;
            }
        }
        if (monitorIp == null) {
            addMessage(redirectAttributes, "该节点监控端monitor未启动，请检查");
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
        }
        FtServiceNode ftServiceNode2 = new FtServiceNode();
        ftServiceNode2.setIpAddress(monitorIp);
        ftServiceNode2.setCmdPort(monitorPort);
        String msg = MessageFactory.getInstance().dataNode("startDataServer");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode2, String.class, true);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "节点启动成功，请稍后检查节点运行状态");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
    }

    @RequiresPermissions("servicenode:ftServiceNode:startStop")
    @RequestMapping(value = "stopDatanode")
    public String stopDatanode(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.equals(ftServiceNode.getState(), "0")) {
            addMessage(redirectAttributes, "不能停止未启动的节点");
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
        }
        String datanodeName = ftServiceNode.getName();
        Map<String, JsonObject> dataNodeMap = ZkService.getInstance().getDataNodeMap();
        List<String> liveDNList = new ArrayList<>();
        for (String dnIpPort : dataNodeMap.keySet()) {
            JsonObject jsonObject = dataNodeMap.get(dnIpPort);
            String dataNodeNameTemp = jsonObject.get("nodeName").getAsString();
            liveDNList.add(dataNodeNameTemp);
        }
        if (!liveDNList.contains(datanodeName)) {
            addMessage(redirectAttributes, "不能停止未启动的节点");
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
        }
        String monitorIp = null;
        String monitorPort = null;
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeNameTemp = jsonObject.get("dnName").getAsString();
            monitorPort = jsonObject.get("mnCmdPort").getAsString();
            if (dataNodeNameTemp.equals(datanodeName)) {
                String[] split = mnIpPort.split(":");
                monitorIp = split[0];
                break;
            }
        }
        if (monitorIp == null) {
            addMessage(redirectAttributes, "该节点监控端monitor未启动，请检查");
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
        }
        FtServiceNode ftServiceNode2 = new FtServiceNode();
        ftServiceNode2.setIpAddress(monitorIp);
        ftServiceNode2.setCmdPort(monitorPort);
        String msg = MessageFactory.getInstance().dataNode("stopDataServer");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode2, String.class, true);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "停止节点成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode";
    }
}
