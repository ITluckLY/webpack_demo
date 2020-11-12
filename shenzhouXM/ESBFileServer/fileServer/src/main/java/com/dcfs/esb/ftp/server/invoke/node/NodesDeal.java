package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.spring.SpringContext;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Created by huangzbb on 2016/7/21.
 */
public class NodesDeal {
    private static final Logger log = LoggerFactory.getLogger(NodeDeal.class);

    private static String nodesCfg = Cfg.getNodesCfg();
    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String SELBYNAME = "selByName";
    private static final String ADDSYS = "addSys";
    private static final String DELSYS = "delSys";
    private static final String SELBYSYS = "selBySys";
    private static final String PRINT = "print";
    private static final String SWITCH_MODE = "switchMode";

    private NodesServiceFactoryFace nodesServiceFactory = SpringContext.getInstance().getNodesServiceFactory();


    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto resultDto = null;
        String operateType = MessDealTool.getString(jsonObject, "operateType");
        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equalsIgnoreCase(ADD)) {
//            resultDto = NodesManager.getInstance().add(jsonObject);//NOSONAR
            resultDto = nodesServiceFactory.getNodesService().add(jsonObject);
        } else if (operateType.equalsIgnoreCase(ADDSYS)) {
            resultDto = NodesManager.getInstance().addSys(jsonObject);
        } else if (operateType.equalsIgnoreCase(DELSYS)) {
            resultDto = NodesManager.getInstance().delSys(jsonObject);
        } else if (operateType.equalsIgnoreCase(DEL)) {
//            resultDto = NodesManager.getInstance().del(jsonObject);//NOSONAR
            resultDto = nodesServiceFactory.getNodesService().del(jsonObject);
        } else if (operateType.equalsIgnoreCase(UPDATE)) {
//            resultDto = NodesManager.getInstance().update(jsonObject);//NOSONAR
            resultDto = nodesServiceFactory.getNodesService().update(jsonObject);
        } else if (operateType.equalsIgnoreCase(SELECT)) {
            resultDto = NodesManager.getInstance().query();
        } else if (operateType.equalsIgnoreCase(SELBYNAME)) {
            resultDto = NodesManager.getInstance().selByName(jsonObject);
        } else if (operateType.equalsIgnoreCase(SELBYSYS)) {
            resultDto = NodesManager.getInstance().selBySys(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(nodesCfg);
        } else if (SWITCH_MODE.equalsIgnoreCase(operateType)) {
            resultDto = NodesManager.getInstance().switchSystemModel(jsonObject);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }

        MessDealTool.sendBackMes(resultDto, socket);

    }
}
