package com.dcfs.esb.ftp.server.invoke.nodesync;

import com.dcfs.esb.ftp.interfases.NodeCfgSyncFace;
import com.dcfs.esb.ftp.report.CfgSyncReport;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.MClassLoaderUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by huangzbb on 2016/7/29.
 */
public class NodeSyncManager {
    private static final Logger log = LoggerFactory.getLogger(NodeSyncManager.class);
    private String str;
    private String sysName;
    private String cfgNames;


    private NodeSyncManager() {
    }

    public static NodeSyncManager getInstance() {
        return new NodeSyncManager();
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

            NodeCfgSyncFace nodeCfgSyncFace = SpringContext.getInstance().getNodeCfgSyncService();
            try {
                CfgSyncReport report = new CfgSyncReport();
                boolean syncSucc = nodeCfgSyncFace.sync(sysName, sync, report);
                if (syncSucc) {
                    String json = GsonUtil.toJson(report.getNodeMsgMap());
                    str = cfgNameArr[i] + ":" + json;
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
     * 按系统生成同步配置文件
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
                boolean syncSucc = makeSyncCfg(sysName, sync);
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

    public boolean makeSyncCfg(String sysName, String cfgName) throws IOException {
        //cfg.xml不同步
        if ("cfg.xml".equalsIgnoreCase(cfgName)) return false;

        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().load(cfgName);
        } catch (DocumentException e) {
            throw new IOException(e);
        }
        String clsPath = MClassLoaderUtil.getResourceFile(".");
        File sysSyncDir = new File(clsPath, "cfgsync" + File.separator + sysName);
        FileUtils.forceMkdir(sysSyncDir);
        File tmpCfgFile = new File(sysSyncDir, cfgName + System.currentTimeMillis());

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
        }
        XMLDealTool.writerXml(doc, tmpCfgFile);

        File tagerCfgFile = new File(sysSyncDir, cfgName);
        return FileUtil.renameTo(tmpCfgFile, tagerCfgFile);
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

        //删除没子节点的BaseFile
        //list.clear();//NOSONAR
        //list = root.selectNodes("/FileRoot/BaseFile[not(*)]");//NOSONAR
        //XMLDealTool.remove(list);//NOSONAR
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

}
