package com.dcfs.esb.ftp.namenode.service;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.namenode.spring.NameSpringContext;
import com.dcfs.esb.ftp.namenode.spring.core.entity.monitor.NodeMonitor;
import com.dcfs.esb.ftp.namenode.spring.core.repository.NodeMonitorRepository;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.invoke.node.NodesServiceFace;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.utils.ObjectsTool;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import com.google.gson.JsonObject;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huangzbb on 2016/10/31.
 */
public class NodesService implements NodesServiceFace {
    private static final Logger log = LoggerFactory.getLogger(NodesService.class);
    //获取nodeName的最后三位数字
    private static final Pattern nodeIdPatt = Pattern.compile("^.*([\\d]{3})$");
    private static String nodesCfg = Cfg.getNodesCfg();
    ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String xpath;
    private String name;
    private String type;
    private String ip;
    private String cmdPort;
    private String ftpServPort;
    private String ftpManagePort;
    private String receivePort;
    private String state;
    private String isolState;//隔离状态 0- 正常 1-隔离
    private String sysName;
    private String nodeModel;//节点模式 单节点模式（single），多节点并行模式(more)，主备模式-主(ms-m),主备模式-备(ms-s)
    private String storeModel;//存储模式 单点（single）、同步(sync)、异步(async)
    private String switchModel;//主备切换模式 自动模式(auto)、手动模式(manual)
    private String str;
    private Element node;

    private NodesService() {
        try {
            doc = CachedCfgDoc.getInstance().loadNodes();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static NodesService getInstance() {
        return new NodesService();
    }

    //处理前台报文
    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");

        name = MessDealTool.getString(data, "name");
        type = MessDealTool.getString(data, "type");
        ip = MessDealTool.getString(data, "ip");
        cmdPort = MessDealTool.getString(data, "cmd_port");
        ftpServPort = MessDealTool.getString(data, "ftp_serv_port");
        ftpManagePort = MessDealTool.getString(data, "ftp_manage_port");
        receivePort = MessDealTool.getString(data, "receive_port");
        state = MessDealTool.getString(data, "state");
        isolState = MessDealTool.getString(data, "isolState");
        sysName = MessDealTool.getString(data, "system");
        nodeModel = MessDealTool.getString(data, "nodeModel");
        storeModel = MessDealTool.getString(data, "storeModel");
        switchModel = MessDealTool.getString(data, "switchModel");

        xpath = String.format("/nodes/node[@name='%s']", name);
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);
        if (null == name || name.length() == 0) {
            resultDto = ResultDtoTool.buildError("节点名称不能为空");
            return resultDto;
        }
        String lastThree = null;
        Matcher matcher = nodeIdPatt.matcher(name);
        if (matcher.find()) {
            lastThree = matcher.group(1);
        }
        if (lastThree == null) {
            resultDto = ResultDtoTool.buildError("节点名称后面三位不是数字");
            return resultDto;
        }
        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，已有此节点");
            return resultDto;
        }
        NodeType nodeType;
        try {
            nodeType = NodeType.valueOf(type.toUpperCase());
        } catch (Exception e1) {
            resultDto = ResultDtoTool.buildError("添加失败，无效的节点类型");
            return resultDto;
        }
        if (hasNodeId(nodeType, lastThree)) {
            resultDto = ResultDtoTool.buildError("添加失败，已有此节点ID");
            return resultDto;
        }

        ip = (StringUtils.isEmpty(ip) ? "" : ip);
        cmdPort = StringUtils.defaultIfEmpty(cmdPort, "");
        ftpServPort = StringUtils.defaultIfEmpty(ftpServPort, "");
        ftpManagePort = StringUtils.defaultIfEmpty(ftpManagePort, "");
        receivePort = StringUtils.defaultIfEmpty(receivePort, "");
        state = StringUtils.defaultIfEmpty(state, "0");
        isolState = StringUtils.defaultIfEmpty(isolState, "0");
        node = XMLDealTool.addChild("node", root);
        XMLDealTool.addProperty("name", name, node);
        XMLDealTool.addProperty("type", type.toLowerCase(), node);
        XMLDealTool.addProperty("ip", ip, node);
        XMLDealTool.addProperty("cmd_port", cmdPort, node);
        XMLDealTool.addProperty("ftp_serv_port", ftpServPort, node);
        XMLDealTool.addProperty("ftp_manage_port", ftpManagePort, node);
        XMLDealTool.addProperty("receive_port", receivePort, node);
        XMLDealTool.addProperty("model", nodeModel, node);

        // 根据zk判断节点状态
        if (NodeType.DATANODE.equals(nodeType)) {
            if ("".equals(sysName) || "[]".equals(sysName) || null == sysName) {
                resultDto = ResultDtoTool.buildError("dataNode系统名称不能为空");
                return resultDto;
            } else {
                String systemStr = sysName.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("]", "");
                Element system = XMLDealTool.addChild("system", node);
                XMLDealTool.updateNode(systemStr, system);
                XMLDealTool.addProperty("storeModel", storeModel, system);
                XMLDealTool.addProperty("switchModel", switchModel, system);
                Map<String, JsonObject> dataNodeMap = ZkService.getInstance().getDataNodeMap();
                state = dataNodeMap.containsKey(ip + ":" + cmdPort) ? "1" : "0";
            }
        } else if (NodeType.NAMENODE.equals(nodeType)) {
            Map<String, JsonObject> nameNodeMap = ZkService.getInstance().getNameNodeMap();
            state = nameNodeMap.containsKey(ip + ":" + cmdPort) ? "1" : "0";
        } else if (NodeType.LOGNODE.equals(nodeType)) {
            Map<String, JsonObject> logNodeMap = ZkService.getInstance().getlogNodeMap();
            state = containsLogNode(logNodeMap, name, ip) ? "1" : "0";
        } else {
            resultDto = ResultDtoTool.buildError("节点类型异常");
            return resultDto;
        }
        XMLDealTool.addProperty("state", state, node);
        XMLDealTool.addProperty("isolState", isolState, node);
        log.debug("name:{}, state:{}", name, state);
        str = XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        // 更新ft_node_monitor
        NodeMonitorRepository nodeMonitorRepository = NameSpringContext.getInstance().getNodeMonitorRepository();
        List<NodeMonitor> monitorList = nodeMonitorRepository.findByNode(name);
        if (!monitorList.isEmpty()) {
            nodeMonitorRepository.delete(monitorList);
        }
        if (NodeType.DATANODE.equals(nodeType)) {
            sysName = sysName.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("]", "");
        } else {
            sysName = null;
        }
        NodeMonitor nodeMonitor = new NodeMonitor();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        nodeMonitor.setId(uuid);
        //nodeMonitor.setTime(new Date())
        nodeMonitor.setStateTime(new Date());
        nodeMonitor.setState(state);
        nodeMonitor.setNode(name);
        nodeMonitor.setSystem(sysName);
        nodeMonitor.setIp(ip);
        nodeMonitor.setPort(cmdPort);
        nodeMonitorRepository.save(nodeMonitor);
        return ResultDtoTool.buildSucceed(str);
    }

    private boolean containsLogNode(Map<String, JsonObject> logNodeMap, String nodeName, String ip) {
        for (Map.Entry<String, JsonObject> entry : logNodeMap.entrySet()) {
            String nodeName2 = entry.getValue().get("nodeName").getAsString();
            if (ObjectsTool.equals(nodeName, nodeName2) && entry.getKey().startsWith(ip + ":")) {
                return true;
            }
        }
        return false;
    }

    public ResultDto<String> update(JSONObject jsonObject) {//NOSONAR
        load(jsonObject);
        node = (Element) root.selectSingleNode(xpath);
        if (null == node) {
            log.error("没有找到要求节点参数");
            resultDto = ResultDtoTool.buildError("没有找到指定的参数");
            return resultDto;
        }
        if (null != ip) {
            Attribute attr = node.attribute("ip");
            attr.setValue(ip);
        }
        if (null != cmdPort) {
            Attribute attr = node.attribute("cmd_port");
            attr.setValue(cmdPort);
        }
        if (null != ftpServPort) {
            Attribute attr = node.attribute("ftp_serv_port");
            attr.setValue(ftpServPort);
        }
        if (null != ftpManagePort) {
            Attribute attr = node.attribute("ftp_manage_port");
            attr.setValue(ftpManagePort);
        }
        if (null != receivePort) {
            Attribute attr = node.attribute("receive_port");
            attr.setValue(receivePort);
        }

        if (null != state && null != type) {
            if (NodeType.DATANODE.name().equalsIgnoreCase(type)) {
                Map<String, JsonObject> dataNodeMap = ZkService.getInstance().getDataNodeMap();
                state = dataNodeMap.containsKey(ip + ":" + cmdPort) ? "1" : "0";
            } else if (NodeType.NAMENODE.name().equalsIgnoreCase(type)) {
                Map<String, JsonObject> nameNodeMap = ZkService.getInstance().getNameNodeMap();
                state = nameNodeMap.containsKey(ip + ":" + cmdPort) ? "1" : "0";
            } else if (NodeType.LOGNODE.name().equalsIgnoreCase(type)) {
                Map<String, JsonObject> logNodeMap = ZkService.getInstance().getlogNodeMap();
                state = logNodeMap.containsKey(ip + ":" + cmdPort) ? "1" : "0";
            }
            Attribute attr = node.attribute("state");
            attr.setValue(state);
        }
        if (StringUtils.isNotEmpty(isolState)) XMLDealTool.addProperty("isolState", isolState, node);
        XMLDealTool.addProperty("model", nodeModel, node);

        //删除已存在的system
        List<Element> sysList = node.elements();
        for (Element e : sysList) {
            if ("system".equals(e.getName())) {
                node.remove(e);
            }
        }
        //添加报文里的system
        if (null != sysName && !"".equals(sysName) && !"[]".equals(sysName)) {
            String systemStr = sysName.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
            node.selectSingleNode("system[text()='" + systemStr + "']");
            Element system = XMLDealTool.addChild("system", node);
            XMLDealTool.updateNode(systemStr, system);
            XMLDealTool.addProperty("storeModel", storeModel, system);
            if (!"".equals(switchModel)) XMLDealTool.addProperty("switchModel", switchModel, system);
        }
        str = XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);
        node = (Element) root.selectSingleNode(xpath);
        if (null == node) {
            if (log.isInfoEnabled()) {
                log.error("没有找到要求节点参数");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的参数");
            return resultDto;
        }
        NodeMonitorRepository nodeMonitorRepository = NameSpringContext.getInstance().getNodeMonitorRepository();
        List<NodeMonitor> monitorList = nodeMonitorRepository.findByNode(name);
        if (!monitorList.isEmpty()) {
            nodeMonitorRepository.delete(monitorList);
        }
        root.remove(node);
        str = XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        return ResultDtoTool.buildSucceed(str);
    }

    private boolean hasNodeId(NodeType nodeType, String lastThree) {
        List<Element> nodes = root.selectNodes(String.format("/nodes/node[@type='%s']", nodeType.name().toLowerCase()));
        for (Element element : nodes) {
            String nodeName = element.attributeValue("name", "");
            if (nodeName.endsWith(lastThree)) return true;
        }
        return false;
    }

    private void reloadNodes() {
        try {
            CachedCfgDoc.getInstance().reloadNodes();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
    }
}
