package com.dcfs.esb.ftp.server.invoke.crontab;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.invoke.node.NodeDeal;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class CrontabDeal {

    private static final Logger log = LoggerFactory.getLogger(NodeDeal.class);
    private static final String CRONTABPATH = Cfg.getCrontabCfg();

    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String SELBYID = "selByID";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String PRINT = "print";

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto;
        String operateType = (String) jsonObject.get("operateType");

        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equalsIgnoreCase(ADD)) {
            resultDto = CrontabManager.getInstance().add(jsonObject);
        } else if (operateType.equalsIgnoreCase(DEL)) {
            resultDto = CrontabManager.getInstance().del(jsonObject);
        } else if (operateType.equalsIgnoreCase(UPDATE)) {
            resultDto = CrontabManager.getInstance().update(jsonObject);
        } else if (operateType.equalsIgnoreCase(SELECT)) {
            resultDto = CrontabManager.getInstance().query();
        } else if (operateType.equalsIgnoreCase(SELBYID)) {
            resultDto = CrontabManager.getInstance().selByID(jsonObject);
        } else if (operateType.equalsIgnoreCase(START)) {
            resultDto = CrontabManager.getInstance().start(jsonObject);
        } else if (operateType.equalsIgnoreCase(STOP)) {
            resultDto = CrontabManager.getInstance().stop(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(CRONTABPATH);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }

        MessDealTool.sendBackMes(resultDto, socket);
    }
}


