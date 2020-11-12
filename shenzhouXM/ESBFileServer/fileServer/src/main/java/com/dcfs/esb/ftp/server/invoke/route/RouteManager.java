package com.dcfs.esb.ftp.server.invoke.route;

import com.dcfs.esb.ftp.common.error.FtpException;
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
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteManager {
    private static final Logger log = LoggerFactory.getLogger(RouteManager.class);

    private static final String ROUTEPATH = Cfg.getRouteCfg();
    ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String user;
    private String tranCode;
    private String type;
    private String mode;
    private String destination;
    private String xpath;
    private String str = null;
    private Element rule = null;

    private RouteManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadRoute();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static RouteManager getInstance() {
        return new RouteManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        user = MessDealTool.getString(data, "user");
        tranCode = MessDealTool.getString(data, "tran_code");
        type = MessDealTool.getString(data, "type");
        mode = MessDealTool.getString(data, "mode");
        destination = MessDealTool.getString(data, "destination");//NOSONAR

        xpath = "/rules/rule[@tran_code='" + tranCode + "']";
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);

        if (null == tranCode || tranCode.equals("")) {
            resultDto = ResultDtoTool.buildError("交易码不能为空");
            return resultDto;
        }

        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，此交易码已存在");
            return resultDto;
        }

        user = (StringUtils.isEmpty(user) ? "" : user);
        type = (StringUtils.isEmpty(type) ? "" : type);
        mode = (StringUtils.isEmpty(mode) ? "" : mode);
        destination = (StringUtils.isEmpty(destination) ? "" : destination);


        rule = XMLDealTool.addChild("rule", root);
        XMLDealTool.addProperty("user", user, rule);
        XMLDealTool.addProperty("tran_code", tranCode, rule);
        XMLDealTool.addProperty("type", type, rule);
        XMLDealTool.addProperty("mode", mode, rule);
        XMLDealTool.addProperty("destination", destination, rule);


        str = XMLDealTool.xmlWriter(doc, ROUTEPATH);

        com.dcfs.esb.ftp.server.route.RouteManager.reload();
        return ResultDtoTool.buildSucceed(str);

    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);

        rule = (Element) root.selectSingleNode(xpath);
        if (null == rule) {
            log.error("没有找到指定的路由信息");//NOSONAR
            resultDto = ResultDtoTool.buildError("没有找到指定的路由信息");
            return resultDto;
        }
        root.remove(rule);

        str = XMLDealTool.xmlWriter(doc, ROUTEPATH);
        com.dcfs.esb.ftp.server.route.RouteManager.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);

        rule = (Element) root.selectSingleNode(xpath);
        if (null == rule) {
            log.error("没有找到指定的路由信息");
            resultDto = ResultDtoTool.buildError("没有找到指定的路由信息");
            return resultDto;
        }

        if (null != user) {
            Attribute attr = rule.attribute("user");
            attr.setValue(user);
        }

        if (null != type) {
            Attribute attr = rule.attribute("type");
            attr.setValue(type);
        }
        if (null != mode) {
            Attribute attr = rule.attribute("mode");
            attr.setValue(mode);
        }
        if (null != destination) {
            Attribute attr = rule.attribute("destination");
            attr.setValue(destination);
        }

        str = XMLDealTool.xmlWriter(doc, ROUTEPATH);
        com.dcfs.esb.ftp.server.route.RouteManager.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByName(JSONObject jsonObject) {
        load(jsonObject);

        rule = (Element) root.selectSingleNode(xpath);
        if (null == rule) {
            log.error("没有找到指定的路由信息");
            resultDto = ResultDtoTool.buildError("没有找到指定的路由信息");
            return resultDto;
        }

        str = XMLtoJSON.getJSONFromXMLEle(rule);
        return ResultDtoTool.buildSucceed(str);
    }
}
