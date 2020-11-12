package com.dcfs.esb.ftp.server.invoke.vsys;


import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.invoke.node.VsysmapWorker;
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

public class VsysmapManager {
    private static final Logger log = LoggerFactory.getLogger(VsysmapManager.class);
    private static String cfgPath = Cfg.getVsysmapCfg();

    private String key;
    private String val;

    private Document doc;
    private Element root;
    private String xpath;

    public VsysmapManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadVsysmap();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        key = MessDealTool.getString(data, "key");
        val = MessDealTool.getString(data, "val");

        xpath = String.format("/root/map[@key='%s']", key);
    }

    public ResultDto<String> add(JSONObject jsonObject) throws FtpException {
        load(jsonObject);

        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(val)) {
            return ResultDtoTool.buildError("系统名称不能为空");
        }

        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            return ResultDtoTool.buildError("添加失败，该系统映射已存在");
        }

        Element mapEle = XMLDealTool.addChild("map", root);
        XMLDealTool.addProperty("key", key, mapEle);
        XMLDealTool.addProperty("val", val, mapEle);

        String str = XMLDealTool.xmlWriter(doc, cfgPath);

        reloadCfg();
        return ResultDtoTool.buildSucceed(str);
    }


    public ResultDto<String> del(JSONObject jsonObject) throws FtpException {
        load(jsonObject);

        Element mapEle = (Element) root.selectSingleNode(xpath);
        if (null == mapEle) {
            log.error("没有找到指定的信息");
            return ResultDtoTool.buildError("没有找到指定的信息");
        }
        root.remove(mapEle);

        String str = XMLDealTool.xmlWriter(doc, cfgPath);
        reloadCfg();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        String str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    private void reloadCfg() throws FtpException {
        try {
            doc = CachedCfgDoc.getInstance().reloadVsysmap();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        VsysmapWorker.reload();
    }
}
