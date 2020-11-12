package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.cons.NodeState;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mocg on 2016/7/15.
 */
public class NodesManager {
    private static String nodesCfg = Cfg.getNodesCfg();
    private ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String name;
    private String type;
    private String ip;//NOSONAR
    private String cmdPort;//NOSONAR
    private String ftpServPort;//NOSONAR
    private String ftpManagePort;//NOSONAR
    private String receivePort;//NOSONAR
    private String state;//NOSONAR
    private String sysName;
    private String sysNodeModel;//系统的节点模式 单节点模式（single），多节点并行模式(more)，主备模式(ms) //NOSONAR
    private String nodeModel;//节点模式 单节点模式（single），多节点并行模式(more)，主备模式-主(ms-m),主备模式-备(ms-s) //NOSONAR
    private String storeModel;//存储模式 单点（single）、同步(sync)、异步(async) //NOSONAR
    private String switchModel;//主备切换模式 自动模式(auto)、手动模式(manual) //NOSONAR
    private String isolState;//隔离状态 0- 正常 1-隔离 //NOSONAR

    private String defxpath;
    private String str;

    private static final String CMD_PORT = "cmd_port";
    private static final String STORE_MODEL = "storeModel";
    private static final String SWITCH_MODEL = "switchModel";
    private static final String F_STATE = "state";
    private static final String SYSTEM = "system";

    private NodesManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadNodes();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static NodesManager getInstance() {
        return new NodesManager();
    }

    public List<Node> getNodesBySystem(String sysname) {
        List<Node> nodeList = new ArrayList<>();
        String xpath = String.format("/nodes/node[@type='datanode'][system='%s']", sysname);
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            nodeList.add(element2Node(nodeEle));
        }
        return nodeList;
    }

    /**
     * 同时排除有相同ip与命令端口的数据节点
     *
     * @param sysname
     * @param nodeName
     * @param state
     * @return
     */
    public List<Node> getDataNodesBySystemExcludeNode(String sysname, String nodeName, int state) {
        List<Node> nodeList = new ArrayList<>();
        String nodeXpath = String.format("/nodes/node[@type='datanode'][@name='%s'][system='%s']", nodeName, sysname);
        Element origNode = (Element) root.selectSingleNode(nodeXpath);
        if (origNode == null) return nodeList;

        String ip = origNode.attributeValue("ip", "");
        String cmdPort = origNode.attributeValue(CMD_PORT, "");

        String sameSysXpath = String.format("/nodes/node[@type='datanode'][@name!='%s'][system='%s'][@state='%s']", nodeName, sysname, state);
        List nodes = root.selectNodes(sameSysXpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            //过滤本节点
            if (ip.equals(nodeEle.attributeValue("ip")) && cmdPort.equals(nodeEle.attributeValue(CMD_PORT))) continue;
            nodeList.add(element2Node(nodeEle));
        }
        return nodeList;
    }

    public List<Node> listDataNodesByState(int state) {
        List<Node> nodeList = new ArrayList<>();
        String xpath = String.format("/nodes/node[@type='datanode'][@state='%s']", state);//1-正常
        List nodes = root.selectNodes(xpath);
        for (Object obj : nodes) {
            Element nodeEle = (Element) obj;
            nodeList.add(element2Node(nodeEle));
        }
        return nodeList;
    }

    public List<Node> listNameNodes() {
        List<Node> nodeList = new ArrayList<>();
        String xpath = "/nodes/node[@type='namenode']";
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
        String xpath = "/nodes/node[@type='datanode']";
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
        String xpath = "/nodes/node[@type='datanode']/system";
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

    public boolean changeNodeState(String nodeName, String ipPort, NodeState nodeState) {
        String ip = ipPort.split(":")[0];
        String xpath = String.format("/nodes/node[@name='%s'][@ip='%s']", nodeName, ip);
        Element nodeEle = (Element) root.selectSingleNode(xpath);
        if (nodeEle == null) return false;
        XMLDealTool.addProperty(F_STATE, String.valueOf(nodeState.num()), nodeEle);
        XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        return true;
    }

    public boolean changeAllNodeState(NodeState nodeState) {
        List<Element> list = root.selectNodes("/nodes/node");
        if (list == null) return false;
        for (Element nodeEle : list) {
            XMLDealTool.addProperty(F_STATE, String.valueOf(nodeState.num()), nodeEle);
        }
        XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        return true;
    }

    public boolean changeNodeSysname(String nodeName, String sysName) {
        if (sysName == null) return false;
        String xpath = String.format("/nodes/node[@name='%s']/system", nodeName);
        Element systemEle = (Element) root.selectSingleNode(xpath);
        if (systemEle == null) return false;
        XMLDealTool.updateNode(sysName, systemEle);
        XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        return true;
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
        node.setState(Integer.parseInt(nodeEle.attributeValue(F_STATE)));
        node.setModel(nodeEle.attributeValue("model"));
        List<Element> systemEles = nodeEle.selectNodes(SYSTEM);
        List<String> systems = new ArrayList<>();
        for (Element systemEle : systemEles) {
            systems.add(systemEle.getText());
            node.setStoreModel(systemEle.attributeValue(STORE_MODEL));
            node.setSwitchModel(systemEle.attributeValue(SWITCH_MODEL));
        }
        node.setSystems(systems);
        node.setIsolState(Integer.parseInt(nodeEle.attributeValue("isolState")));
        return node;
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        root = XMLDealTool.getRoot(doc);

        name = MessDealTool.getString(data, "name");
        type = MessDealTool.getString(data, "type");
        ip = MessDealTool.getString(data, "ip");
        cmdPort = MessDealTool.getString(data, CMD_PORT);
        ftpServPort = MessDealTool.getString(data, "ftp_serv_port");
        ftpManagePort = MessDealTool.getString(data, "ftp_manage_port");
        receivePort = MessDealTool.getString(data, "receive_port");
        state = MessDealTool.getString(data, F_STATE);
        sysName = MessDealTool.getString(data, SYSTEM);
        sysNodeModel = MessDealTool.getString(data, "sysNodeModel");
        nodeModel = MessDealTool.getString(data, "nodeModel");
        storeModel = MessDealTool.getString(data, STORE_MODEL);
        switchModel = MessDealTool.getString(data, SWITCH_MODEL);

        defxpath = String.format("/nodes/node[@name='%s']", name);
    }


    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByName(JSONObject jsonObject) {
        load(jsonObject);
        String xpath = String.format("/nodes/node[@name='%s' or @type='%s']", name, type);

        List<Element> list = root.selectNodes(xpath);
        if (null == list) {
            return ResultDtoTool.buildError("不能找到指定的节点");//NOSONAR
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) builder.append(",");
            String json = XMLtoJSON.getJSONFromXMLEle(list.get(i));
            builder.append(json);
        }
        builder.append("]");
        return ResultDtoTool.buildSucceed(builder.toString());
    }

    public ResultDto<String> addSys(JSONObject jsonObject) {//NOSONAR
        load(jsonObject);
        Element node = (Element) root.selectSingleNode(defxpath);
        //判断用户名称是否为空
        if (null == name || name.length() == 0) {
            resultDto = ResultDtoTool.buildError("节点名称不能为空");
            return resultDto;
        }
        if (null == node) {
            resultDto = ResultDtoTool.buildError("不能找到指定的节点");
            return resultDto;
        }
        //删除已存在的system
        List<Element> sysList = node.elements();
        for (Element e : sysList) {
            if (SYSTEM.equals(e.getName())) {
                node.remove(e);
            }
        }
        //添加报文里的system
        if ("".equals(sysName) || "[]".equals(sysName) || null == sysName) {
            str = XMLDealTool.xmlWriter(doc, nodesCfg);
        } else {
            String[] sys = sysName.split(",");//["esb","cms"]
            ArrayList<String> strList = new ArrayList<>();
            for (String sy : sys) {
                strList.add(sy.substring(sy.indexOf('"') + 1, sy.lastIndexOf('"')));
            }
            for (String aStrList : strList) {
                org.dom4j.Node sysNode = node.selectSingleNode("system[text()='" + aStrList + "']");
                if (sysNode != null) {
                    resultDto = ResultDtoTool.buildError("用户下已存在该system");
                    return resultDto;
                }
                Element system = XMLDealTool.addChild(SYSTEM, node);
                XMLDealTool.updateNode(aStrList, system);
                XMLDealTool.addProperty(STORE_MODEL, storeModel, system);
                XMLDealTool.addProperty(SWITCH_MODEL, switchModel, system);
                str = XMLDealTool.xmlWriter(doc, nodesCfg);
            }
        }
        reloadNodes();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> delSys(JSONObject jsonObject) {
        load(jsonObject);
        Element node = (Element) root.selectSingleNode(defxpath);
        if (null == node) {
            resultDto = ResultDtoTool.buildError("不能找到指定的节点");
            return resultDto;
        }
        List<Element> sysList = node.elements();
        for (Element e : sysList) {
            if (SYSTEM.equals(e.getName())) {
                String sys = e.getText().trim();
                if (sys.equals(sysName.substring(2, 5))) {
                    node.remove(e);
                }
            }
        }
        str = XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selBySys(JSONObject jsonObject) {
        load(jsonObject);
        String xpath = String.format("/nodes/node[system='%s']", sysName);

        Document docClone = XMLDealTool.cloneDoc(doc);
        root = XMLDealTool.getRoot(docClone);
        List<Element> list = root.selectNodes(xpath);

        if (null == list) {
            resultDto = ResultDtoTool.buildError("不能找到指定的节点");
            return resultDto;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) builder.append(",");
            Element element = list.get(i);
            //删除system不做显示
            List<Element> sysList = element.elements();
            for (Element e : sysList) {
                if (SYSTEM.equals(e.getName())) {
                    element.remove(e);
                }
            }
            String str = XMLtoJSON.getJSONFromXMLEle(element);
            builder.append(str);
        }
        builder.append("]");
        return ResultDtoTool.buildSucceed(builder.toString());
    }

    /**
     * 主备模式切换
     *
     * @param jsonObject data{sysName、switchMode}
     * @return
     */
    public ResultDto<Boolean> switchSystemModel(JSONObject jsonObject) {
        load(jsonObject);
        String xpath = String.format("/nodes/node/system[text()='%s']", sysName);
        List<Element> list = root.selectNodes(xpath);
        for (Element element : list) {
            XMLDealTool.addProperty(SWITCH_MODEL, switchModel, element);
        }
        XMLDealTool.xmlWriter(doc, nodesCfg);
        reloadNodes();
        return ResultDtoTool.buildSucceed(true);
    }

    private void reloadNodes() {
        try {
            CachedCfgDoc.getInstance().reloadNodes();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        NodesWorker.reload();
    }

    public String getVersion() {
        return root.attributeValue("timestamp", null);
    }
}
