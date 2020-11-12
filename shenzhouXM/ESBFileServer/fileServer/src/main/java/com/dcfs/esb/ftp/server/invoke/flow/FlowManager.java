package com.dcfs.esb.ftp.server.invoke.flow;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.impls.flow.ServiceFlowManager;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FlowManager {
    private static final Logger log = LoggerFactory.getLogger(FlowManager.class);

    private static final String FLOWPATH = Cfg.getFlowCfg();
    private ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String name;
    private String describe;
    private String components;
    private String sysName;//@Text
    private String xpath;
    private String str = null;
    private Element flowNode = null;

    private static final String F_COMPONENTS = "components";
    private static final String F_DESCRIBE = "describe";

    private FlowManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadFlow();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static FlowManager getInstance() {
        return new FlowManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        name = MessDealTool.getString(data, "name");
        components = MessDealTool.getString(data, F_COMPONENTS);
        describe = MessDealTool.getString(data, F_DESCRIBE);
        sysName = MessDealTool.getString(data, "sysName");
        if (sysName == null) sysName = "";
        xpath = String.format("/flows/flow[@name='%s'][text()='%s']", name, sysName);
    }

    public ResultDto<String> add(JSONObject jsonObject) throws FtpException {
        load(jsonObject);

        if (StringUtils.isBlank(name)) {
            resultDto = ResultDtoTool.buildError("流程名称不能为空");
            return resultDto;
        }

        if (root.selectSingleNode(xpath) != null) {
            resultDto = ResultDtoTool.buildError("添加失败，已有此流程");
            return resultDto;
        }
        if (root.selectSingleNode(String.format("/flows/flow[@name='%s'][text()='*']", name)) != null) {
            resultDto = ResultDtoTool.buildError("添加失败，已有此流程");
            return resultDto;
        }

        components = StringUtils.defaultString(components, "");
        describe = StringUtils.defaultString(describe, "");

        flowNode = XMLDealTool.addChild("flow", root);
        XMLDealTool.addProperty("name", name, flowNode);
        XMLDealTool.addProperty(F_COMPONENTS, components, flowNode);
        XMLDealTool.addProperty(F_DESCRIBE, describe, flowNode);
        flowNode.setText(sysName);

        str = XMLDealTool.xmlWriter(doc, FLOWPATH);
        ServiceFlowManager.getInstance().reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) throws FtpException {
        load(jsonObject);

        flowNode = (Element) root.selectSingleNode(xpath);
        if (null == flowNode) {
            log.error("没有找到要求的流程信息");//NOSONAR
            resultDto = ResultDtoTool.buildError("没有找到要求的流程信息");
            return resultDto;
        }
        root.remove(flowNode);

        str = XMLDealTool.xmlWriter(doc, FLOWPATH);
        ServiceFlowManager.getInstance().reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) throws FtpException {
        load(jsonObject);

        flowNode = (Element) root.selectSingleNode(xpath);
        if (null == flowNode) {
            log.error("没有找到要求的流程信息");
            resultDto = ResultDtoTool.buildError("没有找到要求的流程信息");
            return resultDto;
        }
        if (null != components) XMLDealTool.addProperty(F_COMPONENTS, components, flowNode);
        if (null != describe) XMLDealTool.addProperty(F_DESCRIBE, describe, flowNode);
        if (sysName != null) XMLDealTool.updateNode(sysName, flowNode);

        str = XMLDealTool.xmlWriter(doc, FLOWPATH);
        ServiceFlowManager.getInstance().reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query(JSONObject jsonObject) {
        load(jsonObject);
        if (StringUtils.isEmpty(sysName)) {
            str = XMLtoJSON.getJSONFromXMLEle(root);
            return ResultDtoTool.buildSucceed(str);
        } else {
            xpath = "/flows/flow[text()!='*' and text()!='" + sysName + "']";
            List<Element> list = root.selectNodes(xpath);
            XMLDealTool.remove(list);
            str = XMLtoJSON.getJSONFromXMLEle(root);
            return ResultDtoTool.buildSucceed(str);
        }
    }

    public ResultDto<String> selByName(JSONObject jsonObject) {
        load(jsonObject);

        flowNode = (Element) root.selectSingleNode(xpath);

        if (null == flowNode) {
            log.error("没有找到要求的流程信息");
            resultDto = ResultDtoTool.buildError("没有找到要求的流程信息");
            return resultDto;
        }

        str = XMLtoJSON.getJSONFromXMLEle(flowNode);
        return ResultDtoTool.buildSucceed(str);
    }
}
