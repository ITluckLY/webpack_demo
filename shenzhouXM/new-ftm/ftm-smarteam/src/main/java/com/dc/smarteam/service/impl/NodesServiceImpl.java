package com.dc.smarteam.service.impl;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.NodesModel;
import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.NodesService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

@Service("NodesServiceImpl")
public class NodesServiceImpl  implements NodesService {

    @Resource(name ="CfgFileServiceImpl")
    private CfgFileService cfgFileService;
    @Resource(name = "FtNodeMonitorServiceImpl")
    private FtNodeMonitorService ftNodeMonitorService;

    public ResultDto<String> select(FtServiceNode node, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String saveStr = MessageFactory.getInstance().nodes(node, "select");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> dto = tcpAdapter.invoke(saveStr, ftServiceNode, String.class);//发送报文
        return dto;
    }

    public ResultDto<List<NodesModel.Node>> listAll(FtServiceNode ftServiceNode, HttpServletRequest request) {
        NodesModel nodesModel = loadModel();
        return ResultDtoTool.buildSucceed(nodesModel.getNodes());
    }

    public ResultDto<String> selByName0(FtServiceNode node, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String getAllStr = MessageFactory.getInstance().nodes(node, "selByName");//生成查询报文
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, ftServiceNode, String.class);//发送报文
        return resultDto;
    }

    public ResultDto<NodesModel.Node> selByName(FtServiceNode ftServiceNode, HttpServletRequest request) {
        NodesModel nodesModel = loadModel();
        NodesModel.Node retNode = null;
        for (NodesModel.Node node1 : nodesModel.getNodes()) {
            if (StringUtils.equals(node1.getName(), ftServiceNode.getName())) {
                retNode = node1;
                break;
            }
        }
        if (retNode == null) return ResultDtoTool.buildError("没有找到指定的节点");
        return ResultDtoTool.buildSucceed(retNode);
    }

    public ResultDto<String> add0(FtServiceNode node, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String getAllStr = MessageFactory.getInstance().nodesAddUpdate(node, "add");//生成查询报文
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, ftServiceNode, String.class);//发送报文
        return resultDto;
    }

    public ResultDto<String> add(FtServiceNode ftServiceNode, HttpServletRequest request) {
        String name = ftServiceNode.getName();
        if (StringUtils.isEmpty(name)) {
            return ResultDtoTool.buildError("节点名称不能为空");
        }
        String lastThree = null;
        Matcher matcher = nodeIdPatt.matcher(name);
        if (matcher.find()) {
            lastThree = matcher.group(1);
        }
        if (lastThree == null) {
            return ResultDtoTool.buildError("节点名称后面三位不是数字");
        }
        NodeType nodeType;
        try {
            nodeType = NodeType.valueOf(ftServiceNode.getType().toUpperCase());
        } catch (Exception e1) {
            return ResultDtoTool.buildError("添加失败，无效的节点类型");
        }

        NodesModel.Node newNode = new NodesModel.Node();
        CfgModelConverter.convertTo(ftServiceNode, newNode);
        NodesModel nodesModel = loadModel();
        for (NodesModel.Node node : nodesModel.getNodes()) {
            if (StringUtils.equalsIgnoreCase(name, node.getName())) {
                return ResultDtoTool.buildError("添加失败，已有此节点");
            }
            if (StringUtils.equalsIgnoreCase(nodeType.name(), node.getType()) && StringUtils.endsWith(node.getName(), lastThree)) {
                return ResultDtoTool.buildError("添加失败，已有此节点ID");
            }
        }

        nodesModel.getNodes().add(newNode);
        save(nodesModel);
        // 更新ft_node_monitor
        List<FtNodeMonitor> nodeMonitorList = ftNodeMonitorService.findList(new FtNodeMonitor());
        for (FtNodeMonitor ftNodeMonitor : nodeMonitorList) {
            if (StringUtils.equalsIgnoreCase(ftNodeMonitor.getNode(), newNode.getName())) {
                ftNodeMonitorService.delete(ftNodeMonitor);
                break;
            }
        }
        FtNodeMonitor newNodeMonitor = new FtNodeMonitor();
        newNodeMonitor.setStateTime(new Date());
        newNodeMonitor.setState(newNode.getState());
        newNodeMonitor.setNode(newNode.getName());
        newNodeMonitor.setSystem(newNode.getSystem() == null ? null : newNode.getSystem().getName());
        newNodeMonitor.setIp(newNode.getIp());
        newNodeMonitor.setPort(newNode.getCmdPort());
        ftNodeMonitorService.save(newNodeMonitor);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update0(FtServiceNode node, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String getAllStr = MessageFactory.getInstance().nodesAddUpdate(node, "update");//生成查询报文
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, ftServiceNode, String.class);//发送报文
        return resultDto;
    }

    public ResultDto<String> update(FtServiceNode ftServiceNode, HttpServletRequest request) {
        NodesModel nodesModel = loadModel();
        for (NodesModel.Node node : nodesModel.getNodes()) {
            if (StringUtils.equals(node.getName(), ftServiceNode.getName())) {
                node.setIp(ftServiceNode.getIpAddress());
                node.setCmdPort(ftServiceNode.getCmdPort());
                node.setServPort(ftServiceNode.getFtpServPort());
                node.setReceivePort(ftServiceNode.getReceivePort());
                node.setManagePort(ftServiceNode.getFtpManagePort());
                node.setState(ftServiceNode.getState());
                node.setIsolState(ftServiceNode.getIsolState());
                break;
            }
        }
        save(nodesModel);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del0(FtServiceNode node, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String getAllStr = MessageFactory.getInstance().nodes(node, "del");//生成查询报文
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, ftServiceNode, String.class);//发送报文
        return resultDto;
    }

    public ResultDto<String> del(FtServiceNode ftServiceNode, HttpServletRequest request) {
        NodesModel nodesModel = loadModel();
        List<NodesModel.Node> nodes = nodesModel.getNodes();
        for (NodesModel.Node node : nodes) {
            if (StringUtils.equals(node.getName(), ftServiceNode.getName())) {
                nodes.remove(node);
                break;
            }
        }
        save(nodesModel);
        // 更新ft_node_monitor
        List<FtNodeMonitor> nodeMonitorList = ftNodeMonitorService.findList(new FtNodeMonitor());
        for (FtNodeMonitor ftNodeMonitor : nodeMonitorList) {
            if (StringUtils.equalsIgnoreCase(ftNodeMonitor.getNode(), ftServiceNode.getName())) {
                ftNodeMonitorService.delete(ftNodeMonitor);
                break;
            }
        }
        return ResultDtoTool.buildSucceed("删除成功");
    }

    /**
     * 主备模式、存储模式切换
     */
    public ResultDto<Boolean> switchSystemModel(FtServiceNode ftServiceNode) {
        NodesModel nodesModel = loadModel();
        for (NodesModel.Node node : nodesModel.getNodes()) {
            if (StringUtils.equalsIgnoreCase(NodeType.DATANODE.name(), node.getType()) &&
                    StringUtils.equals(node.getSystem().getName(), ftServiceNode.getSystemName())) {
                node.getSystem().setSwitchModel(ftServiceNode.getSwitchModel());
                node.getSystem().setStoreModel(ftServiceNode.getStoreModel());
                break;
            }
        }
        save(nodesModel);
        return ResultDtoTool.buildSucceed(true);
    }

    public static final String CFG_FILE_NAME = "nodes.xml";

    public NodesModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, NodesModel.class);
    }

    public void save(NodesModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }
}
