package com.dcfs.esb.ftp.server.invoke.service;


import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class ServiceDeal {

    private static final Logger log = LoggerFactory.getLogger(ServiceDeal.class);
    private static final String SERVICEPATH = Cfg.getServicesInfoCfg();

    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String SELBYTRANCODE = "selByTrancode";
    private static final String SELECT_ONE = "selectOne";
    private static final String ADD_PUT_AUTH = "addPutAuth";
    private static final String DEL_PUT_AUTH = "delPutAuth";
    private static final String ADD_GET_AUTH = "addGetAuth";
    private static final String DEL_GET_AUTH = "delGetAuth";
    private static final String PRINT = "print";

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto resultDto;
        String operateType = MessDealTool.getString(jsonObject, "operateType");

        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equalsIgnoreCase(ADD)) {
            resultDto = ServiceManager.getInstance().add(jsonObject);
        } else if (operateType.equalsIgnoreCase(DEL)) {
            resultDto = ServiceManager.getInstance().del(jsonObject);
        } else if (operateType.equalsIgnoreCase(UPDATE)) {
            resultDto = ServiceManager.getInstance().update(jsonObject);
        } else if (operateType.equalsIgnoreCase(SELECT)) {
            resultDto = ServiceManager.getInstance().select(jsonObject);
        } else if (operateType.equalsIgnoreCase(SELBYTRANCODE)) {
            resultDto = ServiceManager.getInstance().selByTrancode(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(SERVICEPATH);
        } else if (operateType.equalsIgnoreCase(SELECT_ONE)) {
            resultDto = ServiceManager.getInstance().selectOne(jsonObject);
        } else if (operateType.equalsIgnoreCase(ADD_PUT_AUTH)) {
            resultDto = ServiceManager.getInstance().addPutAuth(jsonObject);
        } else if (operateType.equalsIgnoreCase(DEL_PUT_AUTH)) {
            resultDto = ServiceManager.getInstance().delPutAuth(jsonObject);
        } else if (operateType.equalsIgnoreCase(ADD_GET_AUTH)) {
            resultDto = ServiceManager.getInstance().addGetAuth(jsonObject);
        } else if (operateType.equalsIgnoreCase(DEL_GET_AUTH)) {
            resultDto = ServiceManager.getInstance().delGetAuth(jsonObject);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }

        MessDealTool.sendBackMes(resultDto, socket);

    }
}

