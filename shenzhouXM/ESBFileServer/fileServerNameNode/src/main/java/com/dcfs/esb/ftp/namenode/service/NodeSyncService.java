package com.dcfs.esb.ftp.namenode.service;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.cons.NodeState;
import com.dcfs.esb.ftp.namenode.spring.NameSpringContext;
import com.dcfs.esb.ftp.namenode.spring.core.entity.biz.CfgFile;
import com.dcfs.esb.ftp.namenode.spring.core.repository.CfgFileRepository;
import com.dcfs.esb.ftp.report.CfgSyncReport;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.invoke.nodesync.NodeSyncServiceFace;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.google.gson.JsonObject;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/10/25.
 */
public class NodeSyncService implements NodeSyncServiceFace {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String str = null;
    private String sysName;
    private String cfgNames;

    private NodeSyncService() {
    }

    public static NodeSyncService getInstance() {
        return new NodeSyncService();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        sysName = MessDealTool.getString(data, "sysName");
        cfgNames = MessDealTool.getString(data, "cfgNames");
    }

    public ResultDto<String> nodeSync(JSONObject jsonObject) {
        load(jsonObject);
        String[] cfgNameArr = cfgNames.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (int i = 0; i < cfgNameArr.length; i++) {
            if (i > 0) stringBuilder.append(",");
            StringBuilder sb = new StringBuilder(cfgNameArr[i]);
            sb.append(".xml");
            String sync = sb.toString();
            log.info("需传递的参数：{}", sync);

            try {
                CfgSyncReport report = new CfgSyncReport();
                boolean syncSucc = NodeCfgSyncService.getInstance().sync(sysName, sync, report);
                if (syncSucc) {
                    String json = GsonUtil.toJson(report.getNodeMsgMap());
                    str = cfgNameArr[i] + ":" + json;
                    str = cfgNameArr[i] + ":true";
                    stringBuilder.append(str);
                } else {
                    str = cfgNameArr[i] + ":false";
                    stringBuilder.append(str);
                }
            } catch (IOException e) {
                str = cfgNameArr[i] + ":error";
                stringBuilder.append(str);
            }
        }
        stringBuilder.append("}");
        return ResultDtoTool.buildSucceed(stringBuilder.toString());
    }

    /**
     * 按系统生成同步配置文件，保存到数据库
     *
     * @param jsonObject
     * @return
     */
    public ResultDto<String> makeSyncCfg(JSONObject jsonObject) {
        load(jsonObject);
        String[] cfgNameArr = cfgNames.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (int i = 0; i < cfgNameArr.length; i++) {
            if (i > 0) stringBuilder.append(",");
            StringBuilder sb = new StringBuilder(cfgNameArr[i]);
            sb.append(".xml");
            String sync = sb.toString();
            log.info("需传递的参数：{}", sync);

            try {
                boolean syncSucc = makeSyncCfgtoDB(sysName, sync);
                if (syncSucc) {
                    str = cfgNameArr[i] + ":true";
                    stringBuilder.append(str);
                } else {
                    str = cfgNameArr[i] + ":false";
                    stringBuilder.append(str);
                }
            } catch (IOException e) {
                log.error("", e);
                str = cfgNameArr[i] + ":error";
                stringBuilder.append(str);
            }
        }
        stringBuilder.append("}");
        return ResultDtoTool.buildSucceed(stringBuilder.toString());
    }

    public boolean makeSyncCfgtoDB(String sysName, String cfgName) throws IOException {
        //cfg.xml不同步
        if ("cfg.xml".equalsIgnoreCase(cfgName)) return false;

        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().load(cfgName);
        } catch (DocumentException e) {
            throw new IOException(e);
        }

        Element root = XMLDealTool.getRoot(doc);
        //添加时间戳
        XMLDealTool.addProperty("timestamp", String.valueOf(System.currentTimeMillis()), root);

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

        // 写入数据库biz_file_cfg
        String formatDoc = XMLDealTool.format(doc);
        byte[] bytes = formatDoc.getBytes(Charset.forName("UTF-8"));

        CfgFileRepository cfgFileRepository = NameSpringContext.getInstance().getCfgFileRepository();
        String nodetype = NodeType.DATANODE.name();
        List<CfgFile> cfgFileList = cfgFileRepository.findByFilenameAndSystemAndNodetype(cfgName, sysName, nodetype);
        CfgFile cfgFile;
        if (!cfgFileList.isEmpty()) {
            cfgFile = cfgFileList.get(0);
        } else {
            cfgFile = new CfgFile();
        }
        int bytesLength = bytes.length;
        String filetype = FilenameUtils.getExtension(cfgName);
        cfgFile.setContent(formatDoc);
        cfgFile.setFiletype(filetype);
        cfgFile.setFilesize(bytesLength);
        cfgFile.setNodetype(nodetype);
        cfgFile.setFilename(cfgName);
        cfgFile.setSystem(sysName);
        cfgFileRepository.save(cfgFile);
        return true;
    }

    /**
     * 按系统生成临时的同步配置xml内容
     *
     * @param jsonObject
     * @return
     */
    public ResultDto<String> generateSyncCfgXml(JSONObject jsonObject) {
        load(jsonObject);
        String cfgName = cfgNames + ".xml";
        try {
            String cfgXml = generateSyncCfgXml(sysName, cfgName);
            return ResultDtoTool.buildSucceed(cfgXml);
        } catch (IOException e) {
            log.error("", e);
            return ResultDtoTool.buildError(e.getMessage());
        }
    }

    public String generateSyncCfgXml(String sysName, String cfgName) throws IOException {
        //cfg.xml不同步
        if ("cfg.xml".equalsIgnoreCase(cfgName)) return null;

        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().load(cfgName);
        } catch (DocumentException e) {
            throw new IOException(e);
        }
        Element root = XMLDealTool.getRoot(doc);
        //添加时间戳
        XMLDealTool.addProperty("timestamp", String.valueOf(System.currentTimeMillis()), root);

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
            if (("/" + sysName).equals(name)
                    || StringUtils.startsWith(name, "/" + sysName + "/")) continue;
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
    private void makeSyncCfgForNodes(String sysName, Document doc) {//NOSONAR
        changeAllNodeState(doc, NodeState.STOP);
        for (Map.Entry<String, JsonObject> entry : ZkService.getInstance().getDataNodeMap().entrySet()) {
            changeNodeState(doc, entry.getKey(), NodeState.RUNNING);
        }
        for (Map.Entry<String, JsonObject> entry : ZkService.getInstance().getNameNodeMap().entrySet()) {
            changeNodeState(doc, entry.getKey(), NodeState.RUNNING);
        }
    }

    private void changeNodeState(Document doc, String ipPort, NodeState nodeState) {
        String[] arr = ipPort.split(":");
        String xpath = String.format("/nodes/node[@ip='%s'][@cmd_port='%s']", arr[0], arr[1]);
        List<Element> list = doc.getRootElement().selectNodes(xpath);
        if (list == null) return;
        for (Element nodeEle : list) {
            XMLDealTool.addProperty("state", String.valueOf(nodeState.num()), nodeEle);
        }
    }

    private void changeAllNodeState(Document doc, NodeState nodeState) {
        List<Element> list = doc.getRootElement().selectNodes("/nodes/node[@type!='lognode']");
        if (list == null) return;
        for (Element nodeEle : list) {
            XMLDealTool.addProperty("state", String.valueOf(nodeState.num()), nodeEle);
        }
    }
}
