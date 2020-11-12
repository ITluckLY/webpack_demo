package com.dcfs.esb.ftp.server.invoke.auth;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class AuthDeal {
    private static final Logger log = LoggerFactory.getLogger(AuthDeal.class);
    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";
    private static final String SELBYNAME = "selByName";
    private static final String QUERY_AUTH_DIR_OF_USER = "queryAuthDirOfUser";
    private static final String QUERY_AUTH_USER_OF_DIR = "queryAuthUserOfDir";
    private static final String QUERY_AUTH_USER_OF_SYS = "queryAuthUserOfSys";
    private static final String ADD_USER_AUTH = "addUserAuth";
    private static final String DEL_USER_AUTH = "delUserAuth";
    private static final String UPDATE_USER_AUTH = "updateUserAuth";
    private static final String PRINT = "print";
    private static final String QUERY_ALL_ORDERBY_SYS = "queryAllOrderbySys";
    private static String FILEPATH = Cfg.getFileCfg();//NOSONAR

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto;
        String operateType = (String) jsonObject.get("operateType");
        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }
        if (operateType.equals(ADD)) {
            resultDto = AuthManager.getInstance().add(jsonObject);
        } else if (operateType.equals(DEL)) {
            resultDto = AuthManager.getInstance().del(jsonObject);
        } else if (operateType.equals(UPDATE)) {
            resultDto = AuthManager.getInstance().update(jsonObject);
        } else if (operateType.equals(SELECT)) {
            resultDto = AuthManager.getInstance().query();
        } else if (operateType.equals(SELBYNAME)) {
            resultDto = AuthManager.getInstance().selByName(jsonObject);
        } else if (operateType.equals(QUERY_AUTH_DIR_OF_USER)) {
            resultDto = AuthManager.getInstance().queryAuthDirOfUser(jsonObject);
        } else if (operateType.equals(QUERY_AUTH_USER_OF_DIR)) {
            resultDto = AuthManager.getInstance().queryAuthUserOfDir(jsonObject);
        } else if (operateType.equals(QUERY_AUTH_USER_OF_SYS)) {
            resultDto = AuthManager.getInstance().queryAuthUserOfSys(jsonObject);
        } else if (operateType.equals(ADD_USER_AUTH)) {
            resultDto = AuthManager.getInstance().addAuth(jsonObject);
        } else if (operateType.equals(DEL_USER_AUTH)) {
            resultDto = AuthManager.getInstance().delAuth(jsonObject);
        } else if (operateType.equals(UPDATE_USER_AUTH)) {
            resultDto = AuthManager.getInstance().updateAuth(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(FILEPATH);
        } else if (operateType.equalsIgnoreCase(QUERY_ALL_ORDERBY_SYS)) {
            resultDto = AuthManager.getInstance().queryAllOrderbySys();
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
