package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NodeManager {
    private static final Logger log = LoggerFactory.getLogger(NodeManager.class);

    private static String CFGPATH = Cfg.getSysCfg();//NOSONAR
    private ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String key;
    private String describe;
    private String text;
    private String xpath;
    private String str = null;
    private Element entry = null;

    private NodeManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadCfg();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static NodeManager getInstance() {
        return new NodeManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        key = MessDealTool.getString(data, "key");
        describe = MessDealTool.getString(data, "describe");//NOSONAR
        text = MessDealTool.getString(data, "text");
        xpath = "/properties/entry[@key='" + key + "']";
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);

        if (null == key || key.equals("")) {
            resultDto = ResultDtoTool.buildError("参数名称不能为空");
            return resultDto;
        }

        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，已有此参数");
            return resultDto;
        }

        describe = (StringUtils.isEmpty(describe) ? "" : describe);
        text = (StringUtils.isEmpty(text) ? "" : text);

        entry = XMLDealTool.addChild("entry", root);
        XMLDealTool.addProperty("key", key, entry);
        XMLDealTool.addProperty("describe", describe, entry);
        XMLDealTool.updateNode(text, entry);

        str = XMLDealTool.xmlWriter(doc, CFGPATH);

        try {
            FtpConfig.reload();
        } catch (FtpException e1) {
            log.error("", e1);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);

        entry = (Element) root.selectSingleNode(xpath);
        if (null == entry) {
            log.error("没有找到要求节点参数");//NOSONAR
            resultDto = ResultDtoTool.buildError("没有找到指定的参数");//NOSONAR
            return resultDto;
        }
        root.remove(entry);

        str = XMLDealTool.xmlWriter(doc, CFGPATH);
        try {
            FtpConfig.reload();
        } catch (FtpException e1) {
            log.error("", e1);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);

        entry = (Element) root.selectSingleNode(xpath);
        if (null == entry) {
            if (log.isInfoEnabled()) {
                log.error("没有找到要求节点参数");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的参数");
            return resultDto;
        }

        if (null != describe) {
            Attribute attr = entry.attribute("describe");
            attr.setValue(describe);
        }

        if (null != text) {
            entry.setText(text);
        }
        str = XMLDealTool.xmlWriter(doc, CFGPATH);
        try {
            FtpConfig.reload();
        } catch (FtpException e1) {
            log.error("", e1);
        }
        //修改系统名称同时修改nodes中对应节点的，如果存在的话
        if ("SYSTEM_NAME".equalsIgnoreCase(key)) {
            NodesManager.getInstance().changeNodeSysname(getNodeName(), text);
            //todo 重新注册zk
            if (NodeType.DATANODE.equals(Cfg.getNodeType())) {
                //
            }
        }
        if ("NODE_NAME".equalsIgnoreCase(key) || "CMD_PORT".equalsIgnoreCase(key)
                || "FTP_SERV_PORT".equalsIgnoreCase(key) || "FTP_MANAGE_PORT".equalsIgnoreCase(key)
                || "DISTRIBUTE_FILE_RECEIVE_PORT".equalsIgnoreCase(key)
                || "HOST_IP".equalsIgnoreCase(key) || "MANAGER_IP".equalsIgnoreCase(key)) {
            resultDto = ResultDtoTool.buildError("不能修改此参数");
            return resultDto;
        }

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByKey(JSONObject jsonObject) {
        load(jsonObject);

        entry = (Element) root.selectSingleNode(xpath);
        if (null == entry) {
            if (log.isInfoEnabled()) {
                log.error("没有找到要求节点参数");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的参数");
            return resultDto;
        }
        str = XMLtoJSON.getJSONFromXMLEle(entry);
        return ResultDtoTool.buildSucceed(str);
    }

    public String getEntryValue(String key) {
        String xpath2 = "/properties/entry[@key='" + key + "']";
        Node node = root.selectSingleNode(xpath2);
        if (node == null) return null;
        return node.getText();
    }

    public String getNodeName() {
        //return getEntryValue("NODE_NAME");//NOSONAR
        return FtpConfig.getInstance().getNodeName();
    }

    public String getSysName() {
        //以cfg.xml中的为准
        return FtpConfig.getInstance().getSystemName();
        /*com.dcfs.esb.ftp.common.model.Node node = NodesManager.getInstance().selNodeByName(getNodeName());//NOSONAR
        String sysName = node == null || node.getSystems().isEmpty() ? null : node.getSystems().get(0);//NOSONAR
        //nodes.xml中的为空，则取cfg.xml中的
        if (sysName == null || sysName.isEmpty()) sysName = FtpConfig.getInstance().getSystemName();//NOSONAR
        return sysName;*/
    }

    public ResultDto<Map<String, String>> info(JSONObject jsonObject) {//NOSONAR
        Map<String, String> map = new HashMap<>();
        map.put("nodeName", getNodeName());
        map.put("sysName", getSysName());
        return ResultDtoTool.buildSucceed(map);
    }
}
