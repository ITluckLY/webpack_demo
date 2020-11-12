package com.dcfs.esb.ftp.server.invoke.component;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ComponentManager {
    private static final Logger log = LoggerFactory.getLogger(ComponentManager.class);
    private static String componentPath = Cfg.getComponentsCfg();
    private ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String name;
    private String describe;
    private String implement;
    private String param;
    private String comment;
    private String xpath;
    private String str = null;
    private Element service = null;

    private ComponentManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadComponents();
        } catch (FtpException e) {
            throw new NestedRuntimeException("加载配置文件err", e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static ComponentManager getInstance() {
        return new ComponentManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        name = MessDealTool.getString(data, "name");
        describe = MessDealTool.getString(data, "describe");//NOSONAR
        implement = MessDealTool.getString(data, "implement");//NOSONAR
        param = MessDealTool.getString(data, "param");//NOSONAR
        comment = MessDealTool.getString(data, "comment");//NOSONAR

        xpath = "/services/service[@name='" + name + "']";

    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);

        if (null == name || name.equals("")) {
            resultDto = ResultDtoTool.buildError("名称不能为空");
            return resultDto;
        }

        if (null == implement || implement.equals("")) {
            resultDto = ResultDtoTool.buildError("生成类不能为空");
            return resultDto;
        }

        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，已有此组件");
            return resultDto;
        }

        describe = (StringUtils.isEmpty(describe) ? "" : describe);
        implement = (StringUtils.isEmpty(implement) ? "" : implement);
        param = (StringUtils.isEmpty(param) ? "" : param);
        comment = (StringUtils.isEmpty(comment) ? "" : comment);

        service = XMLDealTool.addChild("service", root);
        XMLDealTool.addProperty("name", name, service);
        XMLDealTool.addProperty("describe", describe, service);
        Element implementNode = service.addElement("implement");
        XMLDealTool.updateNode(implement, implementNode);
        Element params = service.addElement("params");//NOSONAR

        if (StringUtils.isNotBlank(param)) {
            String[] ps = param.split(",");
            for (String param2 : ps) {
                int idx = param2.indexOf('=');
                String n = param2.substring(0, idx);
                String p = param2.substring(idx + 1);
                Element paramNode = params.addElement("param");
                XMLDealTool.addProperty("name", n, paramNode);
                XMLDealTool.updateNode(p, paramNode);
            }
        }

        Element commentNode = service.addElement("comment");
        XMLDealTool.updateNode(comment, commentNode);

        str = XMLDealTool.xmlWriter(doc, componentPath);
        ServiceContainer.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);

        service = (Element) root.selectSingleNode(xpath);
        if (null == service) {
            log.error("没有找到指定的组件信息");//NOSONAR
            resultDto = ResultDtoTool.buildError("没有找到指定的组件信息");
            return resultDto;
        }
        root.remove(service);

        str = XMLDealTool.xmlWriter(doc, componentPath);
        ServiceContainer.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);

        service = (Element) root.selectSingleNode(xpath);
        if (null == service) {
            log.error("没有找到指定的组件信息");
            resultDto = ResultDtoTool.buildError("没有找到指定的组件信息");
            return resultDto;
        }

        if (null == implement || implement.equals("")) {
            resultDto = ResultDtoTool.buildError("生成类不能为空");
            return resultDto;
        }

        if (null != describe) {
            Attribute attr = service.attribute("describe");
            attr.setValue(describe);
        }

        XMLDealTool.updateNode(implement, service.element("implement"));

        //删除所有参数
        Element params = service.element("params");
        if (params != null) {
            @SuppressWarnings("unchecked")
            List<Element> elements = params.elements();
            for (Element element : elements) {
                params.remove(element);
            }
        } else {
            params = XMLDealTool.addChild("params", service);
        }

        //重新添加属性
        if (param != null && param.contains("=")) {
            String[] ps = param.split(",");
            for (String param2 : ps) {
                int idx = param2.indexOf('=');
                String n = param2.substring(0, idx);
                String p = param2.substring(idx + 1);
                Element paramNode = params.addElement("param");
                XMLDealTool.addProperty("name", n, paramNode);
                XMLDealTool.updateNode(p, paramNode);
            }
        }

        if (null != comment) {
            Element commentNode = service.element("comment");
            XMLDealTool.updateNode(comment, commentNode);
        }

        str = XMLDealTool.xmlWriter(doc, componentPath);
        ServiceContainer.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByName(JSONObject jsonObject) {
        load(jsonObject);

        service = (Element) root.selectSingleNode(xpath);
        if (null == service) {
            log.error("没有找到指定的组件信息");
            resultDto = ResultDtoTool.buildError("没有找到指定的组件信息");
            return resultDto;
        }

        str = XMLtoJSON.getJSONFromXMLEle(service);
        return ResultDtoTool.buildSucceed(str);
    }

    public String getVersion() {
        return root.attributeValue("timestamp", null);
    }
}
