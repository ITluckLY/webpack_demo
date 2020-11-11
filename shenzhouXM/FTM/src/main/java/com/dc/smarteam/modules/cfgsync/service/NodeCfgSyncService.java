package com.dc.smarteam.modules.cfgsync.service;

import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.cons.NodeState;
import com.dc.smarteam.util.XMLDealTool;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class NodeCfgSyncService {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 按系统生成临时的同步配置xml内容
     *
     * @param sysName
     * @param cfgName
     * @param cfgContent
     * @return
     * @throws IOException
     */

    public String generateSyncCfgXml(String sysName, String cfgName, String cfgContent, boolean hasTimestamp) throws IOException {
        //cfg.xml不同步
        if ("cfg.xml".equalsIgnoreCase(cfgName)) return null;

        Document doc = XMLDealTool.readXml(cfgContent); // 此处获取数据正常
        Element root = XMLDealTool.getRoot(doc);
        //添加时间戳
        if (hasTimestamp) XMLDealTool.addProperty("timestamp", String.valueOf(System.currentTimeMillis()), root);

        //部分配置需要根据系统进行处理
        if ("file.xml".equalsIgnoreCase(cfgName)) {
            makeSyncCfgForFile(sysName, doc);
        } else if ("file_clean.xml".equalsIgnoreCase(cfgName)) {
            makeSyncCfgForFileClean(sysName, doc);
        } else if ("file_rename.xml".equalsIgnoreCase(cfgName)) {
            makeSyncCfgForFileRename(sysName, doc);
        } else if ("flow.xml".equalsIgnoreCase(cfgName)) {
            makeSyncCfgForFlow(sysName, doc);
        } else if ("services_info.xml".equalsIgnoreCase(cfgName)) {
            makeSyncCfgForServicesInfo(sysName, doc);
        } else if ("nodes.xml".equalsIgnoreCase(cfgName)) {
            makeSyncCfgForNodes(sysName, doc);
        }

        return XMLDealTool.format(doc);
    }

    private void makeSyncCfgForFile(String sysName, Document doc) {
        Element root = XMLDealTool.getRoot(doc);
        List<Element> list = root.selectNodes("/FileRoot/BaseFile");
        for (Element element : list) {
            String name = element.attributeValue("name");
            if (("/" + sysName).equals(name)) continue;
            if (StringUtils.startsWith(name, "/" + sysName + "/")) continue;
            XMLDealTool.remove(element);
        }
    }

    private void makeSyncCfgForFileClean(String sysName, Document doc) {
        Element root = XMLDealTool.getRoot(doc);
        String xpath = String.format("/root/fileClean[@system!='%s']", sysName);
        List<Element> list = root.selectNodes(xpath);
        XMLDealTool.remove(list);
    }

    private void makeSyncCfgForFileRename(String sysName, Document doc) {
        Element root = XMLDealTool.getRoot(doc);
        List<Element> list = root.selectNodes("/root/rule");
        for (Element element : list) {
            String sysname = element.attributeValue("sysname");
            if (StringUtils.equals(sysName, sysname)) continue;
            XMLDealTool.remove(element);
        }
    }

    private void makeSyncCfgForFlow(String sysName, Document doc) {
        Element root = XMLDealTool.getRoot(doc);
        List<Element> list = root.selectNodes("/flows/flow");
        for (Element element : list) {
            String text = element.getText();//sysname
            if (sysName.equals(text) || "*".equals(text)) continue;
            XMLDealTool.remove(element);
        }
    }

    private void makeSyncCfgForServicesInfo(String sysName, Document doc) {
        Element root = XMLDealTool.getRoot(doc);
        List<Element> list = root.selectNodes("/services/service");
        for (Element element : list) {
            String text = element.attributeValue("sysname");
            if (sysName.equals(text)) continue;
            XMLDealTool.remove(element);
        }
    }

    //更新节点的状态
    private void makeSyncCfgForNodes(String sysName, Document doc) {
        changeAllNodeState(doc, NodeState.STOP);
        for (Map.Entry<String, JsonObject> entry : ZkService.getInstance().getDataNodeMap().entrySet()) {
            changeNodeState(doc, entry.getKey(), NodeState.RUNNING);
        }
        for (Map.Entry<String, JsonObject> entry : ZkService.getInstance().getNameNodeMap().entrySet()) {
            changeNodeState(doc, entry.getKey(), NodeState.RUNNING);
        }
        for (Map.Entry<String, JsonObject> entry : ZkService.getInstance().getLogNodeMap().entrySet()) {
            changeNodeState(doc, entry.getKey(), NodeState.RUNNING);
        }
    }

    private void changeNodeState(Document doc, String ipPort, NodeState nodeState) {
        String[] arr = ipPort.split(":");
        String xpath = String.format("/nodes/node[@ip='%s'][@cmd_port='%s']", arr[0], arr[1]);
        List<Element> list = doc.getRootElement().selectNodes(xpath);
        if (list == null) return;
        for (Element nodeEle : list) {
            XMLDealTool.addProperty("state", String.valueOf(nodeState.ordinal()), nodeEle);
        }
    }

    private void changeAllNodeState(Document doc, NodeState nodeState) {
        List<Element> list = doc.getRootElement().selectNodes("/nodes/node[@type!='lognode']");
        if (list == null) return;
        for (Element nodeEle : list) {
            XMLDealTool.addProperty("state", String.valueOf(nodeState.ordinal()), nodeEle);
        }
    }
}
