package com.dcfs.esb.ftp.server.invoke.user;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.UserInfoFactory;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserManager {
    private static final Logger log = LoggerFactory.getLogger(UserManager.class);

    private static String USERPATH = Cfg.getUserCfg();//NOSONAR
    private ResultDto<String> resultDto = new ResultDto<String>();
    private Document doc;
    private Element root;
    private String uid;//用户名称,系统内唯一,全部英文
    private String home;//用户目录=/系统名称/用户名称
    private String sysname;//系统名称,全部英文
    private String describe;//用户描述，如中文名称
    private String passwd;
    private String IPText;
    private String status;
    private String IPDescribe;
    private String xpath;
    private String grant;
    private String str = null;
    private Element userInfo = null;

    private UserManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadUser();
        } catch (FtpException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static UserManager getInstance() {
        /*if (instance != null) {
            return instance;
        } else {
            synchronized (lock) {
                instance = new UserManager();
            }
            return instance;
        }*/
        return new UserManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        uid = MessDealTool.getString(data, "uid");
        home = MessDealTool.getString(data, "home");
//        sysname = MessDealTool.getString(data, "sysname");
        describe = MessDealTool.getString(data, "describe");
        sysname = "";
        passwd = MessDealTool.getString(data, "passwd");
        IPText = MessDealTool.getString(data, "IP");
        status = MessDealTool.getString(data, "status");
        IPDescribe = MessDealTool.getString(data, "IPDescribe");
        grant = MessDealTool.getString(data, "grant");

//        home = "/" + sysname + "/" + uid;
//        xpath = "/UserRoot/UserInfo[Uid='" + uid + "'][Uid/@sysname='" + sysname + "']";
        home = "/" + uid;
        xpath = "/UserRoot/UserInfo[Uid='" + uid + "']";
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);
        //判断用户名称是否为空
        if (null == uid || uid.length() == 0) {
            resultDto = ResultDtoTool.buildError("用户名称不能为空");
            return resultDto;
        }
        //判断用户目录是否为空
//        if (null == sysname || sysname.length() == 0) {
//            resultDto = ResultDtoTool.buildError("系统目录不能为空");
//            return resultDto;
//        }
        //判断用户是否已存在
        Element user = (Element) root.selectSingleNode(xpath);
        if (user != null) {
            resultDto = ResultDtoTool.buildError("此用户已存在");
            return resultDto;
        }
        //判断密码是否为空
        if (null == passwd || passwd.length() == 0) {
            resultDto = ResultDtoTool.buildError("密码不能为空");
            return resultDto;
        }
        //判断用户目录是否为空
        if (null == home || home.length() == 0) {
            resultDto = ResultDtoTool.buildError("用户目录不能为空");
            return resultDto;
        }

        if (!home.startsWith("/")) {
            resultDto = ResultDtoTool.buildError("用户目录格式不对,可参照格式：/esb");
            return resultDto;
        }

        describe = (StringUtils.isEmpty(describe) ? "" : describe);

        userInfo = XMLDealTool.addChild("UserInfo", root);
        Element uid = XMLDealTool.addChild("Uid", userInfo);
        Element passwd = XMLDealTool.addChild("Passwd", userInfo);
        XMLDealTool.addProperty("home", home, uid);
        XMLDealTool.addProperty("sysname", sysname, uid);
        XMLDealTool.addProperty("describe", describe, uid);
        XMLDealTool.updateNode(this.uid, uid);
        this.passwd = "${3DES}" + ScrtUtil.encryptEsb(this.passwd);
        XMLDealTool.updateNode(this.passwd, passwd);
        //Grant
        Element grantEle = XMLDealTool.addChild("Grant", userInfo);//NOSONAR
        grantEle.addAttribute("type", grant);

        str = XMLDealTool.xmlWriter(doc, USERPATH);

        try {
            UserInfoFactory.reload();
        } catch (FtpException e) {
            log.error("", e);
        }

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);

        userInfo = (Element) root.selectSingleNode(xpath);
        if (null == userInfo) {
            if (log.isInfoEnabled()) {
                log.info("没有找到指定用户");
            }
            resultDto = ResultDtoTool.buildError("没有找到指用户");
            return resultDto;
        }
        root.remove(userInfo);

        str = XMLDealTool.xmlWriter(doc, USERPATH);

        try {
            UserInfoFactory.reload();
        } catch (FtpException e) {
            log.error("", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    /**
     * 全量方式更新用户信息，uid为key
     *
     * @param jsonObject
     * @return
     */
    public ResultDto<String> updateUser(JSONObject jsonObject) {
        load(jsonObject);

        userInfo = (Element) root.selectSingleNode(xpath);
        if (null == userInfo) {
            if (log.isInfoEnabled()) {
                log.error("没有找到要求节点参数");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的用户");
            return resultDto;
        }
        //判断用户目录是否为空
//        if (null == sysname || sysname.length() == 0) {
//            resultDto = ResultDtoTool.buildError("系统目录不能为空");
//            return resultDto;
//        }
        //判断密码是否为空
        if (null == passwd || passwd.length() == 0) {
            resultDto = ResultDtoTool.buildError("密码不能为空");
            return resultDto;
        }
        //判断用户目录是否为空
        if (null == home || home.length() == 0) {
            resultDto = ResultDtoTool.buildError("用户目录不能为空");
            return resultDto;
        }

        if (!home.startsWith("/")) {
            resultDto = ResultDtoTool.buildError("用户目录格式不对,可参照格式：/esb");
            return resultDto;
        }

        Element uid = userInfo.element("Uid");

        Attribute homeAttr = uid.attribute("home");
        homeAttr.setValue(home);

        Element passwd = userInfo.element("Passwd");
        this.passwd = this.passwd.startsWith("${3DES}") ? this.passwd : ("${3DES}" + ScrtUtil.encryptEsb(this.passwd));
        XMLDealTool.updateNode(this.passwd, passwd);

        if (null != sysname && !("").equals(sysname)) {
            Attribute nameAttr = uid.attribute("sysname");
            nameAttr.setValue(sysname);
        }
        if (null != describe) {
            Attribute desAttr = uid.attribute("describe");
            desAttr.setValue(describe);
        }
        //Grant
        Element grantEle = (Element) userInfo.selectSingleNode("Grant");
        if (grantEle != null) grantEle.addAttribute("type", grant);
        str = XMLDealTool.xmlWriter(doc, USERPATH);

        try {
            UserInfoFactory.reload();
        } catch (FtpException e) {
            log.error("", e);
        }

        //更新用户权限
//        AuthManager.getInstance().updateAuth(home, sysname, uid, grant);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selOneUser(JSONObject jsonObject) {
        load(jsonObject);

        userInfo = (Element) root.selectSingleNode(xpath);
        if (null == userInfo) {
            if (log.isInfoEnabled()) {
                log.error("没有找到指定的用户信息");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的用户信息");
            return resultDto;
        }

        str = XMLtoJSON.getJSONFromXMLEle(userInfo);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> addIP(JSONObject jsonObject) {
        load(jsonObject);

//		判断用户名称是否为空
        if (null == uid || uid.length() == 0) {
            resultDto = ResultDtoTool.buildError("用户名称不能为空");
            return resultDto;
        }
        userInfo = (Element) root.selectSingleNode(xpath);
        if (null == userInfo) {
            resultDto = ResultDtoTool.buildError("不能找到指定的用户");
            return resultDto;
        }
        if (null == IPText || IPText.length() == 0) {
            resultDto = ResultDtoTool.buildError("IP不能为空");
            return resultDto;
        }

        Node ipNode = userInfo.selectSingleNode("IP[text()='" + IPText + "']");
        if (ipNode != null) {
            resultDto = ResultDtoTool.buildError("用户下已存在该IP");
            return resultDto;
        }

        status = (StringUtils.isEmpty(status) ? "" : status);
        IPDescribe = (StringUtils.isEmpty(IPDescribe) ? "" : IPDescribe);

        Element ip = XMLDealTool.addChild("IP", userInfo);

        XMLDealTool.addProperty("status", status, ip);
        XMLDealTool.addProperty("IPDescribe", IPDescribe, ip);
        XMLDealTool.updateNode(IPText, ip);

        str = XMLDealTool.xmlWriter(doc, USERPATH);

        try {
            UserInfoFactory.reload();
        } catch (FtpException e) {
            log.error("", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> delIP(JSONObject jsonObject) {
        load(jsonObject);

        userInfo = (Element) root.selectSingleNode(xpath);
        if (null == userInfo) {
            resultDto = ResultDtoTool.buildError("不能找到指定的用户");
            return resultDto;
        }
        @SuppressWarnings("unchecked")
        List<Element> ips = userInfo.elements();

        for (Element e : ips) {
            if ("IP".equals(e.getName())) {
                String ip = e.getText().trim();
                if (ip.equals(IPText)) {
                    userInfo.remove(e);
                }
            }
        }

        str = XMLDealTool.xmlWriter(doc, USERPATH);

        try {
            UserInfoFactory.reload();
        } catch (FtpException e) {
            log.error("", e);
        }

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> updateIP(JSONObject jsonObject) {
        load(jsonObject);

        userInfo = (Element) root.selectSingleNode(xpath);
        if (null == userInfo) {
            resultDto = ResultDtoTool.buildError("不能找到指定的用户");
            return resultDto;
        }
        if (null == IPText || IPText.length() == 0) {
            resultDto = ResultDtoTool.buildError("IP为空");
            return resultDto;
        }
        Node ipNode = userInfo.selectSingleNode("IP[text()='" + IPText + "']");
        if (ipNode == null) {
            resultDto = ResultDtoTool.buildError("没有找到指定的IP");
            return resultDto;
        }
        @SuppressWarnings("unchecked")
        List<Element> ips = userInfo.elements();

        for (Element e : ips) {
            if ("IP".equals(e.getName())) {
                String ip = e.getText().trim();
                if (ip.equals(IPText)) {
                    if (null != status) {
                        Attribute attr = e.attribute("status");
                        attr.setValue(status);
                    }
                    if (null != IPDescribe) {
                        Attribute attr = e.attribute("IPDescribe");
                        attr.setValue(IPDescribe);
                    }
                }
            }
        }
        str = XMLDealTool.xmlWriter(doc, USERPATH);

        try {
            UserInfoFactory.reload();
        } catch (FtpException e) {
            log.error("", e);
        }

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByUser(JSONObject jsonObject) {
        load(jsonObject);

        userInfo = (Element) root.selectSingleNode(xpath);
        if (null == userInfo) {
            resultDto = ResultDtoTool.buildError("不能找到指定的用户");
            return resultDto;
        }
        Element u = userInfo.element("Uid");
        Element p = userInfo.element("Passwd");
        Element grant = userInfo.element("Grant");
        userInfo.remove(u);
        userInfo.remove(p);
        if (grant != null) userInfo.remove(grant);
        str = XMLtoJSON.getJSONFromXMLEle(userInfo);
        str = XMLtoJSON.wrapToArray(str);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selectUserBySys(JSONObject jsonObject) {
        load(jsonObject);
        xpath = "/UserRoot/UserInfo[Uid/@sysname='" + sysname + "']";
        List<Element> list = root.selectNodes(xpath);
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) builder.append(",");
            String str = XMLtoJSON.getJSONFromXMLEle(list.get(i));
            builder.append(str);
        }
        builder.append("]");
        return ResultDtoTool.buildSucceed(builder.toString());
    }
}
