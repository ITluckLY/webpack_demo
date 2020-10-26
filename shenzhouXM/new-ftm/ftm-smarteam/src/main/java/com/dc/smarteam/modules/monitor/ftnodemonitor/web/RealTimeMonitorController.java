package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

/**
 * Created by huangzbb on 2017/5/16.
 */
@Controller
@RequestMapping(value = "${adminPath}/monitor/FtNodeMonitor/RealTime")
public class RealTimeMonitorController {

    @RequestMapping(value = "cpu")
    @ResponseBody
    public FtNodeMonitor getNodeCpu(String nodename) throws Exception {
        FtNodeMonitor ftNodeMonitor = new FtNodeMonitor();
        ftNodeMonitor.setNode(nodename);
        ftNodeMonitor.setTime(new Date());
        String monitorIp = null;
        String monitorPort = null;
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeNameTemp = jsonObject.get("dnName").getAsString();
            monitorPort = jsonObject.get("mnCmdPort").getAsString();
            if (dataNodeNameTemp.equals(nodename)) {
                String[] split = mnIpPort.split(":");
                monitorIp = split[0];
                break;
            }
        }
        if (monitorIp == null) {
            ftNodeMonitor.setCpu("-1");
            return ftNodeMonitor;
        }
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setIpAddress(monitorIp);
        ftServiceNode.setCmdPort(monitorPort);
        String msg = MessageFactory.getInstance().dataNode("cpu");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode, String.class, true);//发送报文
        String data = resultDto.getData();
        ftNodeMonitor.setCpu(data == null ? "-1" : data);
        return ftNodeMonitor;
    }

    @RequestMapping(value = "memory")
    @ResponseBody
    public FtNodeMonitor getNodeMemory(String nodename) throws Exception {
        FtNodeMonitor ftNodeMonitor = new FtNodeMonitor();
        ftNodeMonitor.setNode(nodename);
        ftNodeMonitor.setTime(new Date());
        String monitorIp = null;
        String monitorPort = null;
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeNameTemp = jsonObject.get("dnName").getAsString();
            monitorPort = jsonObject.get("mnCmdPort").getAsString();
            if (dataNodeNameTemp.equals(nodename)) {
                String[] split = mnIpPort.split(":");
                monitorIp = split[0];
                break;
            }
        }
        if (monitorIp == null) {
            ftNodeMonitor.setMemory("-1");
            return ftNodeMonitor;
        }
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setIpAddress(monitorIp);
        ftServiceNode.setCmdPort(monitorPort);
        String msg = MessageFactory.getInstance().dataNode("memory");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode, String.class, true);//发送报文
        String data = resultDto.getData();
        ftNodeMonitor.setMemory(data == null ? "-1" : data);
        return ftNodeMonitor;
    }

    @RequestMapping(value = "flowrate")
    @ResponseBody
    public FtNodeMonitor getNodeFlowrate(String nodename) throws Exception {
        FtNodeMonitor ftNodeMonitor = new FtNodeMonitor();
        ftNodeMonitor.setNode(nodename);
        ftNodeMonitor.setTime(new Date());
        String monitorIp = null;
        String monitorPort = null;
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeNameTemp = jsonObject.get("dnName").getAsString();
            monitorPort = jsonObject.get("mnCmdPort").getAsString();
            if (dataNodeNameTemp.equals(nodename)) {
                String[] split = mnIpPort.split(":");
                monitorIp = split[0];
                break;
            }
        }
        if (monitorIp == null) {
            ftNodeMonitor.setFlowrate("-1");
            return ftNodeMonitor;
        }
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setIpAddress(monitorIp);
        ftServiceNode.setCmdPort(monitorPort);
        String msg = MessageFactory.getInstance().dataNode("flowrate");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode, String.class, true);//发送报文
        String data = resultDto.getData();
        ftNodeMonitor.setFlowrate(data == null ? "-1" : data);
        return ftNodeMonitor;
    }
}
