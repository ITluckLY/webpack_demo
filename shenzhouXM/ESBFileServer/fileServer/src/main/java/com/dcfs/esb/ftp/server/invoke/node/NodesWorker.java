package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.cons.NodeState;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.zk.ZkServiceHelper;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import com.google.gson.JsonObject;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.*;

/**
 * Created by mocg on 2016/7/15.
 */
public class NodesWorker {
    private static final Object lock = new Object();
    private static NodesWorker instance = null;
    private Element root;
    private String currNodeSysName;
    private int currNodeIsolState;//1-隔离，0-非隔离

    private static final String CMD_PORT = "cmd_port";

    private NodesWorker() {
        load();
        Node node = selNodeByName(FtpConfig.getInstance().getNodeName());
        currNodeSysName = node == null || node.getSystems().isEmpty() ? null : node.getSystems().get(0);
        currNodeIsolState = node == null ? 1 : node.getIsolState();//非注册节点为隔离
    }

    public static NodesWorker getInstance() {
        if (instance != null) return instance;
        synchronized (lock) {
            if (instance == null) {
                instance = new NodesWorker();
            }
        }
        return instance;
    }

    public static void reload() {
        try {
            CachedCfgDoc.getInstance().reloadNodes();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        instance = new NodesWorker();
    }

    private void load() {
        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().loadNodes();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public List<Node> getNodesBySystem(String sysname) {
        List<Node> nodeList = new ArrayList<>();
        String xpath = String.format("/nodes/node[@type='datanode'][system='%s']", sysname);
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            Node node = element2Node(nodeEle);
            nodeList.add(node);
        }
        return nodeList;
    }

    public List<Node> getDataNodesBySystemExcludeNode(String sysname, String nodeName, int state) {
        return this.getDataNodesBySystemExcludeNode(sysname, nodeName, state, 0);
    }

    /**
     * 获取指定系统下，除nodeName外其他可用的非隔离的节点列表
     *
     * @param sysname   系统名称
     * @param nodeName  节点名称
     * @param state     节点运行状态
     * @param isolState 节点隔离状态
     * @return 节点列表
     */
    public List<Node> getDataNodesBySystemExcludeNode(String sysname, String nodeName, int state, int isolState) {
        List<Node> nodeList = new ArrayList<>();
        String nodeXpath = String.format("/nodes/node[@type='datanode'][@name='%s'][system='%s']", nodeName, sysname);
        Element origNode = (Element) root.selectSingleNode(nodeXpath);
        if (origNode == null) return nodeList;

        String ip = origNode.attributeValue("ip", "");
        String cmdPort = origNode.attributeValue(CMD_PORT, "");

        //节点状态有时会不对，不根据状态返回，获取后再与ZK上的节点状态作比较
        String sameSysXpath = String.format("/nodes/node[@type='datanode'][@name!='%s'][system='%s'][@isolState='%s']", nodeName, sysname, isolState);
        List nodes = root.selectNodes(sameSysXpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            //过滤本节点
            if (ip.equals(nodeEle.attributeValue("ip")) && cmdPort.equals(nodeEle.attributeValue(CMD_PORT))) continue;
            Node node = element2Node(nodeEle);
            nodeList.add(node);
        }
        Map<String, JsonObject> dataNodeMap = ZkServiceHelper.getZkService().getDataNodeMap();
        Iterator<Node> it = nodeList.iterator();
        while (it.hasNext()) {
            Node node = it.next();
            boolean contains = containsNode(node, dataNodeMap);
            if ((state == 1 && contains)
                    || (state == 0 && !contains)) continue;
            it.remove();
        }
        return nodeList;
    }

    private boolean containsNode(Node node, Map<String, JsonObject> dataNodeMap) {
        String ipPort = node.getIp() + ":" + node.getCmdPort();
        return dataNodeMap.containsKey(ipPort);
    }

    public List<Node> getDataNodesBySystemExcludeNode0(String sysname, String nodeName, int state) {
        List<Node> nodeList = new ArrayList<>();
        String xpath = String.format("/nodes/node[@type='datanode'][@name!='%s'][system='%s'][@state='%s']", nodeName, sysname, state);
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            Node node = element2Node(nodeEle);
            nodeList.add(node);
        }
        return nodeList;
    }

    public List<Node> listDataNodesByState(int state) {
        List<Node> nodeList = new ArrayList<>();
        String xpath = String.format("/nodes/node[@type='datanode'][@state='%s']", state);//1-正常
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            Node node = element2Node(nodeEle);
            nodeList.add(node);
        }
        return nodeList;
    }

    public List<Node> listDataNodesByStateAndIsolState(int state, int isolState) {
        List<Node> nodeList = new ArrayList<>();
        //节点状态有时会不对，不根据状态返回，获取后再与ZK上的节点状态作比较
        String xpath = String.format("/nodes/node[@type='datanode'][@isolState='%s']", isolState);//1-隔离
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            Node node = element2Node(nodeEle);
            nodeList.add(node);
        }
        Map<String, JsonObject> dataNodeMap = ZkServiceHelper.getZkService().getDataNodeMap();
        Iterator<Node> it = nodeList.iterator();
        while (it.hasNext()) {
            Node node = it.next();
            boolean contains = containsNode(node, dataNodeMap);
            if ((state == 1 && contains)
                    || (state == 0 && !contains)) continue;
            it.remove();
        }
        return nodeList;
    }

    public List<Node> listNameNodes() {
        List<Node> nodeList = new ArrayList<>();
        String xpath = "/nodes/node[@type='namenode']"; //NOSONAR
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            Node node = element2Node(nodeEle);
            nodeList.add(node);
        }
        return nodeList;
    }

    public List<Node> listDataNodes() {
        List<Node> nodeList = new ArrayList<>();
        String xpath = "/nodes/node[@type='datanode']"; //NOSONAR
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            Node node = element2Node(nodeEle);
            nodeList.add(node);
        }
        return nodeList;
    }

    public Set<String> listSystem() {
        Set<String> sysSet = new HashSet<>();
        String xpath = "/nodes/node[@type='datanode']/system"; //NOSONAR
        List<Element> systems = root.selectNodes(xpath);
        for (Element ele : systems) {
            sysSet.add(ele.getText().trim());
        }
        return sysSet;
    }

    public Node selNodeByName(String nodeName) {
        String xpath = String.format("/nodes/node[@name='%s']", nodeName);
        Element nodeEle = (Element) root.selectSingleNode(xpath);
        if (nodeEle == null) return null;
        return element2Node(nodeEle);
    }


    public int countNodes(String sysname, NodeState nodeState) {
        String xpath = String.format("/nodes/node[@state='%s'][system='%s']", nodeState.num(), sysname);
        List nodes = root.selectNodes(xpath);
        return nodes.size();
    }

    public boolean hasNode(String nodeName, String ip, int cmdPort, NodeType nodeType) {
        String xpath;
        if (NodeType.LOGNODE.equals(nodeType)) {
            xpath = String.format("/nodes/node[@name='%s'][@ip='%s']", nodeName, ip);
        } else {
            xpath = String.format("/nodes/node[@name='%s'][@ip='%s'][@cmd_port='%s']", nodeName, ip, cmdPort);
        }
        Element nodeEle = (Element) root.selectSingleNode(xpath);
        return nodeEle != null;
    }

    private Node element2Node(Element nodeEle) {
        Node node = new Node();
        node.setName(nodeEle.attributeValue("name"));
        node.setType(nodeEle.attributeValue("type"));
        node.setIp(nodeEle.attributeValue("ip"));
        node.setCmdPort(Integer.parseInt(nodeEle.attributeValue(CMD_PORT)));
        node.setFtpServPort(Integer.parseInt(nodeEle.attributeValue("ftp_serv_port")));
        node.setFtpManagePort(Integer.parseInt(nodeEle.attributeValue("ftp_manage_port")));
        node.setReceivePort(Integer.parseInt(nodeEle.attributeValue("receive_port")));
        node.setState(Integer.parseInt(nodeEle.attributeValue("state")));
        node.setModel(nodeEle.attributeValue("model"));
        List<Element> systemEles = nodeEle.selectNodes("system");
        List<String> systems = new ArrayList<>();
        for (Element systemEle : systemEles) {
            systems.add(systemEle.getText());
            node.setStoreModel(systemEle.attributeValue("storeModel"));
            node.setSwitchModel(systemEle.attributeValue("switchModel"));
        }
        node.setSystems(systems);
        node.setIsolState(Integer.parseInt(nodeEle.attributeValue("isolState")));
        return node;
    }


    public String getVersion() {
        return root.attributeValue("timestamp", null);
    }

    public String getCurrNodeSysName() {
        return currNodeSysName;
    }

    public int getCurrNodeIsolState() {
        return currNodeIsolState;
    }
}
