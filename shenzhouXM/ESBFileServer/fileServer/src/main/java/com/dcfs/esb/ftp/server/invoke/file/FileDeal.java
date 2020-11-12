package com.dcfs.esb.ftp.server.invoke.file;

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

public class FileDeal {

    private static final Logger log = LoggerFactory.getLogger(NodeDeal.class);
    public static final String DEL_FILE = "delFile";
    private static final String FILEPATH = Cfg.getFileCfg();
    private static final String SELECT = "select";
    private static final String SELBYKEY = "selByKey";
    private static final String SUBDIR = "subDir";
    private static final String QUERY = "query";
    private static final String DOWNLOAD = "download";
    private static final String TRANSCODING = "transcoding";
    private static final String ENCRYPT = "encrypt";
    private static final String DECRYPT = "decrypt";
    private static final String MKDIR = "mkdir";
    private static final String PRINT = "print";
    private static final String SEARCH = "search";

    public void dealMess(JSONObject jsonObject, Socket socket) {//NOSONAR
        ResultDto<String> resultDto = null;
        String operateType = MessDealTool.getString(jsonObject, "operateType");

        if (null == operateType) {
            if (log.isErrorEnabled()) {
                log.error("操作类型为空");
            }
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equalsIgnoreCase(SELECT)) {

            resultDto = FileManager.getInstance().getFileTree(jsonObject);

        } else if (operateType.equalsIgnoreCase(SELBYKEY)) {

            resultDto = FileManager.getInstance().selByKey(jsonObject);

        } else if (operateType.equalsIgnoreCase(SUBDIR)) {

            resultDto = FileManager.getInstance().subDir(jsonObject);

        } else if (operateType.equalsIgnoreCase(QUERY)) {

            resultDto = FileManager.getInstance().query(jsonObject);

        } else if (operateType.equalsIgnoreCase(DOWNLOAD)) {

            resultDto = FileManager.getInstance().downLoad(jsonObject, socket);

        } else if (operateType.equalsIgnoreCase(TRANSCODING)) {

            FileManager.getInstance().convert(jsonObject);

        } else if (operateType.equalsIgnoreCase(ENCRYPT)) {
            resultDto = ResultDtoTool.buildError("TODO");
        } else if (operateType.equalsIgnoreCase(DECRYPT)) {
            resultDto = ResultDtoTool.buildError("TODO");
        } else if (operateType.equalsIgnoreCase(MKDIR)) {
            resultDto = FileManager.getInstance().mkdir(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(FILEPATH);
        } else if (DEL_FILE.equalsIgnoreCase(operateType)) {
            resultDto = FileManager.getInstance().delFile(jsonObject);
        } else if(SEARCH.equalsIgnoreCase(operateType)){
            resultDto = FileManager.getInstance().search(jsonObject);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);

    }
}