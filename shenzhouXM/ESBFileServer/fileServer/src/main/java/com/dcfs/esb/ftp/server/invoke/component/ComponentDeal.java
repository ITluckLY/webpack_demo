package com.dcfs.esb.ftp.server.invoke.component;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class ComponentDeal {

    private static final Logger log = LoggerFactory.getLogger(ComponentDeal.class);
    private static final String COMPONENTPATH = Cfg.getConfigPath() + "components.xml";

    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String SELBYNAME = "selByName";
    private static final String PRINT = "print";

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto = null;
        String operateType = (String) jsonObject.get("operateType");

        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }


        if (operateType.equalsIgnoreCase(ADD)) {

            resultDto = ComponentManager.getInstance().add(jsonObject);

        } else if (operateType.equalsIgnoreCase(DEL)) {

            resultDto = ComponentManager.getInstance().del(jsonObject);

        } else if (operateType.equalsIgnoreCase(UPDATE)) {

            resultDto = ComponentManager.getInstance().update(jsonObject);

        } else if (operateType.equalsIgnoreCase(SELECT)) {

            resultDto = ComponentManager.getInstance().query();

        } else if (operateType.equalsIgnoreCase(SELBYNAME)) {

            resultDto = ComponentManager.getInstance().selByName(jsonObject);

        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(COMPONENTPATH);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }

        MessDealTool.sendBackMes(resultDto, socket);
    }
}
