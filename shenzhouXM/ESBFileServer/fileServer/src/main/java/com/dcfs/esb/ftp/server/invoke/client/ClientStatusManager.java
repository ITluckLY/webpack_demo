package com.dcfs.esb.ftp.server.invoke.client;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.client.ClientManage;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientStatusManager {
    private static final Logger log = LoggerFactory.getLogger(ClientStatusManager.class);

    private static String CLIENT_STATUS_PATH = Cfg.getClientStatusCfg();//NOSONAR
    public static final String CLIENT_ID = "id";
    public static final String CLIENT_STATUS = "status";
    public static final String CLIENT_TYPE = "type";
    public static final String CLIENT_MODE = "mode";
    public static final String CLIENT_ELEMENT = "client";

    private ResultDto<String> resultDto = new ResultDto<String>();
    private Document doc;
    private Element root;

    private String id = null;
    private String status = null;
    private String type = null;
    private String mode = null;

    private String xpath;
    private String str = null;
    private Element client = null;

    private ClientStatusManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadClientStatus();
        } catch (FtpException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static ClientStatusManager getInstance() {
        return new ClientStatusManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        id = MessDealTool.getString(data, CLIENT_ID);
        type = MessDealTool.getString(data, CLIENT_TYPE);
        mode = MessDealTool.getString(data, CLIENT_MODE);
        status = MessDealTool.getString(data, CLIENT_STATUS);

        xpath = "/clients/client[@id='" + id + "']";
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);
        //判断用户名称是否为空
        if (null == id || id.length() == 0) {
            resultDto = ResultDtoTool.buildError("客户端编号不能为空");
            return resultDto;
        }

        //判断用户是否已存在
        Element client = (Element) root.selectSingleNode(xpath);
        if (client != null) {
            resultDto = ResultDtoTool.buildError("此客户端状态信息已存在");
            return resultDto;
        }
        id = (StringUtils.isEmpty(id) ? "" : id);
        type = (StringUtils.isEmpty(type) ? "" : type);
        mode = (StringUtils.isEmpty(mode) ? "" : mode);
        status = (StringUtils.isEmpty(status) ? "" : status);


        client = XMLDealTool.addChild(CLIENT_ELEMENT, root);
        XMLDealTool.addProperty(CLIENT_ID, id, client);
        XMLDealTool.addProperty(CLIENT_TYPE, type, client);
        XMLDealTool.addProperty(CLIENT_MODE, mode, client);
        XMLDealTool.addProperty(CLIENT_STATUS, status, client);

        str = XMLDealTool.xmlWriter(doc, CLIENT_STATUS_PATH);

        ClientManage.reload();

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);

        client = (Element) root.selectSingleNode(xpath);
        if (null == client) {
            if (log.isInfoEnabled()) {
                log.info("没有找到指定的客户端状态信息");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的客户端状态信息");
            return resultDto;
        }
        root.remove(client);

        str = XMLDealTool.xmlWriter(doc, CLIENT_STATUS_PATH);

        ClientManage.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    /**
     * 全量方式更新用户信息，uid为key
     *
     * @param jsonObject
     * @return
     */
    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);

        client = (Element) root.selectSingleNode(xpath);
        if (null == client) {
            if (log.isInfoEnabled()) {
                log.error("没有找到要求节点参数");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的客户端状态信息");
            return resultDto;
        }
        if (null != id) {
            Attribute attr = client.attribute(CLIENT_ID);
            attr.setValue(id);
        }

        if (null != type) {
            Attribute attr = client.attribute(CLIENT_TYPE);
            attr.setValue(type);
        }
        if (null != mode) {
            Attribute attr = client.attribute(CLIENT_MODE);
            attr.setValue(mode);
        }
        if (null != status) {
            Attribute attr = client.attribute(CLIENT_STATUS);
            attr.setValue(status);
        }

        str = XMLDealTool.xmlWriter(doc, CLIENT_STATUS_PATH);

        ClientManage.reload();

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selById(JSONObject jsonObject) {
        load(jsonObject);

        client = (Element) root.selectSingleNode(xpath);
        if (null == client) {
            log.error("没有找到指定的客户端状态信息");
            resultDto = ResultDtoTool.buildError("没有找到指定的客户端状态信息");
            return resultDto;
        }

        str = XMLtoJSON.getJSONFromXMLEle(client);
        return ResultDtoTool.buildSucceed(str);
    }
    public String addToStr(JSONObject jsonObject) {
        load(jsonObject);
        //判断用户名称是否为空
        if (null == id || id.length() == 0) {
            log.info("客户端编号不能为空");
            return null;
        }

        //判断用户是否已存在
        Element client = (Element) root.selectSingleNode(xpath);
        if (client != null) {
            log.info("此客户端状态信息已存在");
            return null;
        }
        id = (StringUtils.isEmpty(id) ? "" : id);
        type = (StringUtils.isEmpty(type) ? "" : type);
        mode = (StringUtils.isEmpty(mode) ? "" : mode);
        status = (StringUtils.isEmpty(status) ? "" : status);


        client = XMLDealTool.addChild(CLIENT_ELEMENT, root);
        XMLDealTool.addProperty(CLIENT_ID, id, client);
        XMLDealTool.addProperty(CLIENT_TYPE, type, client);
        XMLDealTool.addProperty(CLIENT_MODE, mode, client);
        XMLDealTool.addProperty(CLIENT_STATUS, status, client);

        try {
            return XMLDealTool.format(doc);
        } catch (IOException e) {
            return null;
        }
    }
    /**
     * 全量方式更新用户信息，uid为key
     *
     * @param jsonObject
     * @return
     */
    public String updateToStr(JSONObject jsonObject) {
        load(jsonObject);

        client = (Element) root.selectSingleNode(xpath);
        if (null == client) {
            if (log.isInfoEnabled()) {
                log.error("没有找到要求节点参数");
            }
            log.error("没有找到指定的客户端状态信息");
            return null;
        }
        if (null != id) {
            Attribute attr = client.attribute(CLIENT_ID);
            attr.setValue(id);
        }

        if (null != type) {
            Attribute attr = client.attribute(CLIENT_TYPE);
            attr.setValue(type);
        }
        if (null != mode) {
            Attribute attr = client.attribute(CLIENT_MODE);
            attr.setValue(mode);
        }
        if (null != status) {
            Attribute attr = client.attribute(CLIENT_STATUS);
            attr.setValue(status);
        }

        try {
            return XMLDealTool.format(doc);
        } catch (IOException e) {
            return null;
        }
    }

}
