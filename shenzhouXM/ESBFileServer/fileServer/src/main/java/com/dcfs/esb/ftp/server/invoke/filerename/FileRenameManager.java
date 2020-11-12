package com.dcfs.esb.ftp.server.invoke.filerename;

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

import java.util.List;

/**
 * Created by huangzbb on 2016/8/9.
 */
public class FileRenameManager {
    private static final Logger log = LoggerFactory.getLogger(FileRenameManager.class);
    private static String fileRenameCfg = Cfg.getFileRenameCfg();
    private ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String xpath;
    private String id;
    private String type;
    private String path;
    private String sysname;
    private String str = null;
    private Element rule = null;
    private static final String SYS_NAME = "sysname";

    private FileRenameManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadFileRename();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static FileRenameManager getInstance() {
        return new FileRenameManager();
    }

    //	处理前台报文
    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");

        id = MessDealTool.getString(data, "id");
        type = MessDealTool.getString(data, "type");
        path = MessDealTool.getString(data, "path");
        sysname = MessDealTool.getString(data, SYS_NAME);
        xpath = String.format("/root/rule[@id='%s']", id);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);
        if (null == id || id.equals("")) {
            resultDto = ResultDtoTool.buildError("节点名称不能为空");
            return resultDto;
        }
        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，已有此节点");
            return resultDto;
        }
        id = (StringUtils.isEmpty(id) ? "" : id);
        type = (StringUtils.isEmpty(type) ? "" : type);
        path = (StringUtils.isEmpty(path) ? "" : path);
        sysname = (StringUtils.isEmpty(sysname) ? "" : sysname);
        rule = XMLDealTool.addChild("rule", root);

        if (path.contains(".") && type.equals("dir")) {
            resultDto = ResultDtoTool.buildError("添加失败，dir类型不能包含文件名");
        } else if (!path.contains(".") && type.equals("file")) {
            resultDto = ResultDtoTool.buildError("添加失败，file类型需包含文件名");
        } else {
            XMLDealTool.addProperty("id", id, rule);
            XMLDealTool.addProperty("type", type, rule);
            XMLDealTool.addProperty("path", path, rule);
            XMLDealTool.addProperty(SYS_NAME, sysname, rule);
            str = XMLDealTool.xmlWriter(doc, fileRenameCfg);
            reloadFileRename();
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);
        rule = (Element) root.selectSingleNode(xpath);
        if (null == rule) {
            if (log.isInfoEnabled()) {
                log.error("没有找到要求的参数");
            }
            resultDto = ResultDtoTool.buildError("没有找到指定的参数");
            return resultDto;
        }
        root.remove(rule);
        str = XMLDealTool.xmlWriter(doc, fileRenameCfg);
        reloadFileRename();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);
        rule = (Element) root.selectSingleNode(xpath);
        if (null == rule) {
            log.warn("没有找到要求的参数");
            return ResultDtoTool.buildError("没有找到指定的参数");
        }
        if (type.equals("dir") && StringUtils.contains(path, ".")) {
            return ResultDtoTool.buildError("更新失败，dir类型不能包含文件名");
        } else if (type.equals("file") && !StringUtils.contains(path, ".")) {
            return ResultDtoTool.buildError("更新失败，file类型需包含文件名");
        }
        rule.attribute("type").setValue(type);
        if (null != id) {
            Attribute attr = rule.attribute("id");
            attr.setValue(id);
        }
        if (null != path) {
            Attribute attr = rule.attribute("path");
            attr.setValue(path);
        }
        if (null != sysname) {
            Attribute attr = rule.attribute("sysname");
            attr.setValue(sysname);
        }

        str = XMLDealTool.xmlWriter(doc, fileRenameCfg);
        reloadFileRename();

        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByIDorType(JSONObject jsonObject) {
        load(jsonObject);
        StringBuilder builder = new StringBuilder();
        if ("".equals(type) || null == type && null == id) {
            str = XMLtoJSON.getJSONFromXMLEle(root);
            resultDto.setCode("0000");
            resultDto.setData(str);
        } else {
            xpath = String.format("/root/rule[@id='%s' or @type='%s']", id, type);
            List<Element> list = root.selectNodes(xpath);

            if (null == list) {
                resultDto = ResultDtoTool.buildError("不能找到指定的节点");
                return resultDto;
            }

            builder.append("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) builder.append(",");
                builder.append(XMLtoJSON.getJSONFromXMLEle(list.get(i)));
            }
            builder.append("]");
        }
        return ResultDtoTool.buildSucceed(builder.toString());
    }

    public boolean compare(String fileName) {
        if (fileName == null) return false;
        String xpathTmp = "/root/rule[@path]";
        List paths = root.selectNodes(xpathTmp);
        for (Object obj : paths) {
            Element pathEle = (Element) obj;
            String typeTmp = pathEle.attributeValue("type");
            String pathTmp = pathEle.attributeValue("path");
            if ("file".equals(typeTmp) && pathTmp.equalsIgnoreCase(fileName)) return true;
            else if ("dir".equals(typeTmp)) {
                if (!pathTmp.endsWith("/")) pathTmp = pathTmp + "/";
                if (fileName.startsWith(pathTmp)) return true;
            }
        }
        return false;
    }

    private void reloadFileRename() {
        try {
            doc = CachedCfgDoc.getInstance().reloadFileRename();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
    }
}
