package com.dcfs.esb.ftp.server.invoke.filerename;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Created by huangzbb on 2016/8/9.
 */
public class FileRenameDeal {
    private static final Logger log = LoggerFactory.getLogger(FileRenameDeal.class);

    private static String fileRenameCfg = Cfg.getFileRenameCfg();
    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String PRINT = "print";
    private static final String SELBYIDORTYPE = "selbyIDorType";

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto;
        String operateType = MessDealTool.getString(jsonObject, "operateType");
        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equalsIgnoreCase(ADD)) {
            resultDto = FileRenameManager.getInstance().add(jsonObject);
        } else if (operateType.equalsIgnoreCase(DEL)) {
            resultDto = FileRenameManager.getInstance().del(jsonObject);
        } else if (operateType.equalsIgnoreCase(UPDATE)) {
            resultDto = FileRenameManager.getInstance().update(jsonObject);
        } else if (operateType.equalsIgnoreCase(SELECT)) {
            resultDto = FileRenameManager.getInstance().query();
        } else if (operateType.equalsIgnoreCase(SELBYIDORTYPE)) {
            resultDto = FileRenameManager.getInstance().selByIDorType(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(fileRenameCfg);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }

        MessDealTool.sendBackMes(resultDto, socket);
    }
}
