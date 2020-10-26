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
import com.dc.smarteam.util.PublicRepResultTool;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/servicenode/ftServiceNode")
public class FtNodeStartStopController /*extends BaseController */{

//    @RequiresPermissions("servicenode:ftServiceNode:startStop")
    @RequestMapping(value = "startDatanode")
    public Object startDatanode(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        String message = "";
        if (StringUtils.equals(ftServiceNode.getState(), "1")) {
            message  = "不能启动正在运行的节点";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        String datanodeName = ftServiceNode.getName();
        Map<String, JsonObject> dataNodeMap = ZkService.getInstance().getDataNodeMap();
        for (String dnIpPort : dataNodeMap.keySet()) {
            JsonObject jsonObject = dataNodeMap.get(dnIpPort);
            String dataNodeNameTemp = jsonObject.get("nodeName").getAsString();
            if (StringUtils.equalsIgnoreCase(dataNodeNameTemp, datanodeName)) {
                message  = "不能启动正在运行的节点";
                log.debug("message:{}",message);
                return PublicRepResultTool.sendResult("9999",message,null);
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
            message  =  "该节点监控端monitor未启动，请检查";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        FtServiceNode ftServiceNode2 = new FtServiceNode();
        ftServiceNode2.setIpAddress(monitorIp);
        ftServiceNode2.setCmdPort(monitorPort);
        String msg = MessageFactory.getInstance().dataNode("startDataServer");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode2, String.class, true);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            message = "节点启动成功，请稍后检查节点运行状态";
        } else {
            message  = resultDto.getMessage();
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        log.debug("message:{}",message);
        return PublicRepResultTool.sendResult("0000",message,null);
    }

//    @RequiresPermissions("servicenode:ftServiceNode:startStop")
    @RequestMapping(value = "stopDatanode")
    public Object stopDatanode(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        String message = "";
        if (StringUtils.equals(ftServiceNode.getState(), "0")) {
            message = "不能停止未启动的节点";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
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
            message = "不能停止未启动的节点";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
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
            message = "该节点监控端monitor未启动，请检查";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        FtServiceNode ftServiceNode2 = new FtServiceNode();
        ftServiceNode2.setIpAddress(monitorIp);
        ftServiceNode2.setCmdPort(monitorPort);
        String msg = MessageFactory.getInstance().dataNode("stopDataServer");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode2, String.class, true);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            message = "停止节点成功";
        } else {
            message = resultDto.getMessage();
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        log.debug("message:{}",message);
        return PublicRepResultTool.sendResult("0000",message,null);
    }
}
