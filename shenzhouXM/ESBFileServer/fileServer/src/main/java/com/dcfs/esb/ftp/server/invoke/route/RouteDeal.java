package com.dcfs.esb.ftp.server.invoke.route;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class RouteDeal {

    private static final Logger log = LoggerFactory.getLogger(RouteDeal.class);
    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String SELBYTRANSCODE = "selByTranscode";
    private static final String PRINT = "print";
    private static final String ROUTEPATH = Cfg.getRouteCfg();

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto;
        String operateType = (String) jsonObject.get("operateType");

        if (null == operateType) {
            if (log.isErrorEnabled()) {
                log.error("操作类型为空");
            }
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equals(ADD)) {

            resultDto = RouteManager.getInstance().add(jsonObject);

        } else if (operateType.equals(DEL)) {

            resultDto = RouteManager.getInstance().del(jsonObject);

        } else if (operateType.equals(UPDATE)) {
            resultDto = RouteManager.getInstance().update(jsonObject);

        } else if (operateType.equals(SELECT)) {

            resultDto = RouteManager.getInstance().query();

        } else if (operateType.equals(SELBYTRANSCODE)) {

            resultDto = RouteManager.getInstance().selByName(jsonObject);

        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(ROUTEPATH);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
