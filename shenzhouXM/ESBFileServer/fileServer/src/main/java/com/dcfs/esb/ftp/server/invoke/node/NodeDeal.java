package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NodeDeal {
    private static final Logger log = LoggerFactory.getLogger(NodeDeal.class);
    private static final String CFGPATH = Cfg.getSysCfg();

    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String SELBYKEY = "selByKey";
    private static final String PRINT = "print";
    private static final String INFO = "info";
    private static final String CURR_RESOURCE = "currResource";

    public void dealMess(JSONObject jsonObject, Socket socket) {//NOSONAR
        ResultDto resultDto = null;
        String operateType = MessDealTool.getString(jsonObject, "operateType");
        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equalsIgnoreCase(ADD)) {
            resultDto = NodeManager.getInstance().add(jsonObject);
        } else if (operateType.equalsIgnoreCase(DEL)) {
            resultDto = NodeManager.getInstance().del(jsonObject);
        } else if (operateType.equalsIgnoreCase(UPDATE)) {
            resultDto = NodeManager.getInstance().update(jsonObject);
        } else if (operateType.equalsIgnoreCase(SELECT)) {
            resultDto = NodeManager.getInstance().query();
        } else if (operateType.equalsIgnoreCase(SELBYKEY)) {
            resultDto = NodeManager.getInstance().selByKey(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(CFGPATH);
        } else if (INFO.equalsIgnoreCase(operateType)) {
            resultDto = NodeManager.getInstance().info(jsonObject);
        } else if (CURR_RESOURCE.equalsIgnoreCase(operateType)) {
            JSONObject data = jsonObject.getJSONObject("data");
            String key = MessDealTool.getString(data, "key");
            SysContent sysContent = SysContent.getInstance();
            Map<String, Object> map = new HashMap<>();
            if (key == null || key.contains("network")) map.put("networkSpeed", sysContent.currNetworkSpeed());
            if (key == null || key.contains("task")) map.put("taskCountMap", sysContent.getCurrTaskCountMap());
            if (key == null || key.contains("priority")) map.put("taskPriorityCountMap", sysContent.getCurrTaskPriorityTokenCountMap());
            if (key == null || key.contains("token")) map.put("tokenCountMap", sysContent.getCurrTaskPriorityTokenCountMap());
            String json = GsonUtil.toJson(map);
            resultDto = ResultDtoTool.buildSucceed(json);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }

        MessDealTool.sendBackMes(resultDto, socket);
    }
}
