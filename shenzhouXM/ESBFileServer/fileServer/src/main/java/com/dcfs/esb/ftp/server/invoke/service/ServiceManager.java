package com.dcfs.esb.ftp.server.invoke.service;

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
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServiceManager {
    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
    private static final String SERVICEPATH = Cfg.getServicesInfoCfg();
    private String sysName;
    private String trancode;
    private String flow;
    private String describe;
    private String rename;
    private String uname = "";
    private String directoy;
    private String filePeriod;//文件保留期限 单位分钟
    private String priority;//服务优先级
    private String size;//服务数量
    private Document doc;
    private Element root;
    private String xpath;
    private String str = null;
    private Element service = null;

    private ServiceManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadServicesInfo();
        } catch (FtpException e) {
            throw new NestedRuntimeException("加载配置文件出错", e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static ServiceManager getInstance() {
        return new ServiceManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");

        sysName = MessDealTool.getString(data, "sysName");
        trancode = MessDealTool.getString(data, "trancode");
        flow = MessDealTool.getString(data, "flow");
        describe = MessDealTool.getString(data, "describe");//NOSONAR
        rename = MessDealTool.getString(data, "rename");//NOSONAR
        uname = MessDealTool.getString(data, "uname");
        directoy = MessDealTool.getString(data, "directoy");
        filePeriod = MessDealTool.getString(data, "filePeriod");//NOSONAR
        priority = MessDealTool.getString(data, "priority");//NOSONAR
        size = MessDealTool.getString(data, "size");
        if (uname == null) uname = "";
        xpath = String.format("/services/service[@sysname='%s'][@trancode='%s']", sysName, trancode);
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        if (null == trancode || trancode.isEmpty()) {
            resultDto = ResultDtoTool.buildError("交易码不能为空");
            return resultDto;
        }
        if (null == sysName || sysName.isEmpty()) {
            resultDto = ResultDtoTool.buildError("系统名称不能为空");
            return resultDto;
        }

        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，交易码重复");
            return resultDto;
        }

        describe = (StringUtils.isEmpty(describe) ? "" : describe);
        flow = (StringUtils.isEmpty(flow) ? "" : flow);

        try {
            service = XMLDealTool.addChild("service", root);
            XMLDealTool.addProperty("sysname", sysName, service);
            XMLDealTool.addProperty("trancode", trancode, service);
            XMLDealTool.addProperty("flow", flow, service);
            XMLDealTool.addProperty("describe", describe, service);
            XMLDealTool.addProperty("rename", rename, service);
            XMLDealTool.addProperty("filePeriod", filePeriod, service);
            XMLDealTool.addProperty("priority", priority, service);
            XMLDealTool.addProperty("size", size, service);
            str = XMLDealTool.xmlWriter(doc, SERVICEPATH);
        } finally {
            ServiceContainer.reload();
        }

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        service = (Element) root.selectSingleNode(xpath);
        if (null == service) {
            log.error("没有找到指定的服务信息#{}", xpath);//NOSONAR
            resultDto = ResultDtoTool.buildError("没有找到指定的服务信息");//NOSONAR
            return resultDto;
        }
        try {
            root.remove(service);
            str = XMLDealTool.xmlWriter(doc, SERVICEPATH);
        } finally {
            ServiceContainer.reload();
        }

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        service = (Element) root.selectSingleNode(xpath);
        if (null == service) {
            log.error("没有找到指定的服务信息#{}", xpath);
            resultDto = ResultDtoTool.buildError("没有找到指定的服务信息");
            return resultDto;
        }
        try {
            if (null != flow) XMLDealTool.addProperty("flow", flow, service);
            if (null != describe) XMLDealTool.addProperty("describe", describe, service);
            if (null != rename) XMLDealTool.addProperty("rename", rename, service);
            if (null != filePeriod) XMLDealTool.addProperty("filePeriod", filePeriod, service);
            if (null != priority) XMLDealTool.addProperty("priority", priority, service);
            if (null != size) XMLDealTool.addProperty("size", size, service);
            str = XMLDealTool.xmlWriter(doc, SERVICEPATH);
        } finally {
            ServiceContainer.reload();
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> select(JSONObject jsonObject) {
        load(jsonObject);
        Document cloneDoc = XMLDealTool.cloneDoc(doc);
        Element root2 = XMLDealTool.getRoot(cloneDoc);
        String xpath2 = String.format("/services/service[@sysname!='%s']", sysName);
        List<Element> list = root2.selectNodes(xpath2);
        XMLDealTool.remove(list);
        str = XMLtoJSON.getJSONFromXMLEle(root2);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByTrancode(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        service = (Element) root.selectSingleNode(xpath);
        if (null == service) {
            log.error("没有找到指定的服务信息#{}", xpath);
            resultDto = ResultDtoTool.buildError("没有找到指定的服务信息");
            return resultDto;
        }

        str = XMLtoJSON.getJSONFromXMLEle(service);
        return ResultDtoTool.buildSucceed(str);
    }

    /**
     * 获取一个交易码相关信息
     *
     * @param jsonObject
     * @return {}
     */
    public ResultDto<String> selectOne(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        service = (Element) root.selectSingleNode(xpath);
        if (null == service) {
            log.error("没有找到指定的服务信息#{}", xpath);
            resultDto = ResultDtoTool.buildError("没有找到指定的服务信息");
            return resultDto;
        }
        str = XMLtoJSON.getJSONFromXMLEle(service);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<Void> addPutAuth(JSONObject jsonObject) {
        load(jsonObject);
        if (StringUtils.isAnyBlank(directoy, uname)) return ResultDtoTool.buildError("对数错误");
        Element serviceEle = (Element) root.selectSingleNode(xpath);
        if (serviceEle == null) return ResultDtoTool.buildError("没有找到相关的服务");
        xpath = String.format("putAuth/user[@directoy='%s'][text()='%s']", directoy, uname);
        Element userEle = (Element) serviceEle.selectSingleNode(xpath);
        if (userEle != null) return ResultDtoTool.buildError("已存在该权限");

        Element putAuthEle = (Element) serviceEle.selectSingleNode("putAuth");
        if (putAuthEle == null) putAuthEle = XMLDealTool.addChild("putAuth", serviceEle);
        boolean succ;
        try {
            userEle = XMLDealTool.addChild("user", putAuthEle);
            XMLDealTool.addProperty("directoy", directoy, userEle);
            XMLDealTool.updateNode(uname, userEle);
            succ = writerXml(doc, SERVICEPATH);
        } finally {
            ServiceContainer.reload();
        }
        if (succ) return ResultDtoTool.buildSucceed(null);
        else return ResultDtoTool.buildError("更新xml文件失败");//NOSONAR
    }


    public ResultDto<Void> delPutAuth(JSONObject jsonObject) {
        load(jsonObject);
        String xpath2 = String.format("/services/service[@sysname='%s'][@trancode='%s']/putAuth/user[@directoy='%s'][text()='%s']"
                , sysName, trancode, directoy, uname);
        Element userEle = (Element) root.selectSingleNode(xpath2);
        if (userEle == null) return ResultDtoTool.buildError("没有找到该权限");
        boolean succ;
        try {
            XMLDealTool.remove(userEle);
            succ = writerXml(doc, SERVICEPATH);
        } finally {
            ServiceContainer.reload();
        }
        if (succ) return ResultDtoTool.buildSucceed(null);
        else return ResultDtoTool.buildError("更新xml文件失败");
    }

    public ResultDto<Void> addGetAuth(JSONObject jsonObject) {
        load(jsonObject);
        if (StringUtils.isAnyBlank(uname)) return ResultDtoTool.buildError("对数错误");
        Element serviceEle = (Element) root.selectSingleNode(xpath);
        if (serviceEle == null) return ResultDtoTool.buildError("没有找到相关的服务");
        xpath = String.format("getAuth/user[text()='%s']", uname);
        Element userEle = (Element) serviceEle.selectSingleNode(xpath);
        if (userEle != null) return ResultDtoTool.buildError("已存在该权限");

        Element getAuthEle = (Element) serviceEle.selectSingleNode("getAuth");
        if (getAuthEle == null) getAuthEle = XMLDealTool.addChild("getAuth", serviceEle);
        boolean succ;
        try {
            userEle = XMLDealTool.addChild("user", getAuthEle);
            XMLDealTool.updateNode(uname, userEle);
            succ = writerXml(doc, SERVICEPATH);
        } finally {
            ServiceContainer.reload();
        }
        if (succ) return ResultDtoTool.buildSucceed(null);
        else return ResultDtoTool.buildError("更新xml文件失败");
    }


    public ResultDto<Void> delGetAuth(JSONObject jsonObject) {
        load(jsonObject);
        String xpath2 = String.format("/services/service[@sysname='%s'][@trancode='%s']/getAuth/user[text()='%s']"
                , sysName, trancode, uname);
        Element userEle = (Element) root.selectSingleNode(xpath2);
        if (userEle == null) return ResultDtoTool.buildError("没有找到该权限");
        boolean succ;
        try {
            XMLDealTool.remove(userEle);
            succ = writerXml(doc, SERVICEPATH);
        } finally {
            ServiceContainer.reload();
        }
        if (succ) return ResultDtoTool.buildSucceed(null);
        else return ResultDtoTool.buildError("更新xml文件失败");
    }

    private boolean writerXml(Document doc, String servicepath) {
        return !XMLDealTool.xmlWriter(doc, servicepath).contains("{\"code\":\"9999\"");
    }
}
