package com.dcfs.esb.ftp.server.invoke.fileclean;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.invoke.flow.FlowDeal;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class FileCleanDeal {
    private static final Logger log = LoggerFactory.getLogger(FlowDeal.class);
    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String QUERY = "query";
    private static final String CLEAN = "clean";
    private static final String PRINT = "print";
    private String cfgPath = Cfg.getFileCleanCfg();

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

            resultDto = FileCleanManager.getInstance().add(jsonObject);

        } else if (operateType.equalsIgnoreCase(DEL)) {

            resultDto = FileCleanManager.getInstance().delete(jsonObject);

        } else if (operateType.equalsIgnoreCase(UPDATE)) {

            resultDto = FileCleanManager.getInstance().update(jsonObject);

        } else if (operateType.equalsIgnoreCase(SELECT)) {

            resultDto = FileCleanManager.getInstance().select();

        } else if (operateType.equalsIgnoreCase(QUERY)) {

            resultDto = FileCleanManager.getInstance().query(jsonObject);

        } else if (operateType.equalsIgnoreCase(CLEAN)) {

            resultDto = FileCleanManager.getInstance().clean(jsonObject);

        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(cfgPath);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);

    }
}
