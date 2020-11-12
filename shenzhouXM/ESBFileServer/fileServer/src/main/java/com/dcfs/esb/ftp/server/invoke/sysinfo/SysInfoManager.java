package com.dcfs.esb.ftp.server.invoke.sysinfo;


import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.system.SystemManage;
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

public class SysInfoManager {
    private static final Logger log = LoggerFactory.getLogger(SysInfoManager.class);
    private static final String SYSINFOFILE = Cfg.getSystemCfg();

    private String name;
    private String protocol;
    private String ip;
    private String port;
    private String username;
    private String password;

    private Document doc;
    private Element root;
    private String xpath;

    private String str = null;
    private Element system = null;

    private static final String F_PROTOCOL = "protocol";
    private static final String F_USERNAME = "username";
    private static final String F_PASSWORD = "password";//NOSONAR

    private SysInfoManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadSystem();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static SysInfoManager getInstance() {
        return new SysInfoManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        name = MessDealTool.getString(data, "name");
        protocol = MessDealTool.getString(data, F_PROTOCOL);
        ip = MessDealTool.getString(data, "ip");
        port = MessDealTool.getString(data, "port");
        username = MessDealTool.getString(data, F_USERNAME);
        password = MessDealTool.getString(data, F_PASSWORD);

        xpath = "/systems/system[@name='" + name + "']";

    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        if (null == name || name.equals("")) {
            resultDto = ResultDtoTool.buildError("系统名称不能为空");
            return resultDto;
        }

        if (null == ip || ip.equals("")) {
            resultDto = ResultDtoTool.buildError("IP不能为空");
            return resultDto;
        }
        if (null == port || port.equals("")) {
            resultDto = ResultDtoTool.buildError("端口不能为空");
            return resultDto;
        }
        if (null == username || username.equals("")) {
            resultDto = ResultDtoTool.buildError("用户名不能为空");
            return resultDto;
        }
        if (null == password || password.equals("")) {
            resultDto = ResultDtoTool.buildError("密码不能为空");
            return resultDto;
        }

        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，该系统名称已存在");
            return resultDto;
        }

        protocol = (StringUtils.isEmpty(protocol) ? "" : protocol);

        system = XMLDealTool.addChild("system", root);
        XMLDealTool.addProperty("name", name, system);
        XMLDealTool.addProperty(F_PROTOCOL, protocol, system);

        Element ipEle = XMLDealTool.addChild("ip", system);
        XMLDealTool.updateNode(ip, ipEle);

        Element portEle = XMLDealTool.addChild("port", system);
        XMLDealTool.updateNode(port, portEle);

        Element usernameEle = XMLDealTool.addChild(F_USERNAME, system);
        XMLDealTool.updateNode(username, usernameEle);

        Element passwordEle = XMLDealTool.addChild(F_PASSWORD, system);
        XMLDealTool.updateNode(password, passwordEle);

        str = XMLDealTool.xmlWriter(doc, SYSINFOFILE);

        SystemManage.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        system = (Element) root.selectSingleNode(xpath);
        if (null == system) {
            log.error("没有找到指定的系统信息");//NOSONAR
            resultDto = ResultDtoTool.buildError("没有找到指定的系统信息");
            return resultDto;
        }
        root.remove(system);

        str = XMLDealTool.xmlWriter(doc, SYSINFOFILE);
        SystemManage.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        system = (Element) root.selectSingleNode(xpath);
        if (null == system) {
            log.error("没有找到指定的系统信息");
            resultDto = ResultDtoTool.buildError("没有找到指定的系统信息");
            return resultDto;
        }

        if (null != protocol) {
            Attribute attr = system.attribute(F_PROTOCOL);
            attr.setValue(protocol);
        }

        if (null != ip) {
            Element e = system.element("ip");
            XMLDealTool.updateNode(ip, e);
        }
        if (null != port) {
            Element e = system.element("port");
            XMLDealTool.updateNode(port, e);
        }
        if (null != username) {
            Element e = system.element(F_USERNAME);
            XMLDealTool.updateNode(username, e);
        }
        if (null != password) {
            Element e = system.element(F_PASSWORD);
            XMLDealTool.updateNode(password, e);
        }

        str = XMLDealTool.xmlWriter(doc, SYSINFOFILE);
        SystemManage.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByName(JSONObject jsonObject) {
        load(jsonObject);
        ResultDto<String> resultDto;
        system = (Element) root.selectSingleNode(xpath);
        if (null == system) {
            log.error("没有找到指定的系统信息");
            resultDto = ResultDtoTool.buildError("没有找到指定的系统信息");
            return resultDto;
        }

        str = XMLtoJSON.getJSONFromXMLEle(system);
        return ResultDtoTool.buildSucceed(str);
    }
}
