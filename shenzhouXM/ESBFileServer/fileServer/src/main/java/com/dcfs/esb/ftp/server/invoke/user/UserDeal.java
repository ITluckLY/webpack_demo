package com.dcfs.esb.ftp.server.invoke.user;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class UserDeal {

    private static final Logger log = LoggerFactory.getLogger(UserDeal.class);
    private static final String ADDUSER = "addUser";
    private static final String DELUSER = "delUser";
    private static final String UPDATEUSER = "updateUser";
    private static final String SELECT = "select";
    private static final String SELONEUSER = "selOneUser";
    private static final String SELECT_USER_BY_SYS = "selectUserBySys";
    private static final String ADDIP = "addIP";
    private static final String DELIP = "delIP";
    private static final String UPDATEIP = "updateIP";
    private static final String SELBYUSER = "selByUser";
    private static final String PRINT = "print";
    private static String USERPATH = Cfg.getUserCfg();//NOSONAR

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto;
        String operateType = MessDealTool.getString(jsonObject, "operateType");
        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (operateType.equals(ADDUSER)) {

            resultDto = UserManager.getInstance().add(jsonObject);
        } else if (operateType.equals(DELUSER)) {

            resultDto = UserManager.getInstance().del(jsonObject);
        } else if (operateType.equals(UPDATEUSER)) {

            resultDto = UserManager.getInstance().updateUser(jsonObject);
        } else if (operateType.equals(SELECT)) {

            resultDto = UserManager.getInstance().query();
        } else if (operateType.equals(SELONEUSER)) {

            resultDto = UserManager.getInstance().selOneUser(jsonObject);
        } else if (operateType.equals(ADDIP)) {

            resultDto = UserManager.getInstance().addIP(jsonObject);
        } else if (operateType.equals(DELIP)) {

            resultDto = UserManager.getInstance().delIP(jsonObject);
        } else if (operateType.equals(UPDATEIP)) {

            resultDto = UserManager.getInstance().updateIP(jsonObject);
        } else if (operateType.equals(SELBYUSER)) {
            resultDto = UserManager.getInstance().selByUser(jsonObject);
        } else if (operateType.equals(SELECT_USER_BY_SYS)) {
            resultDto = UserManager.getInstance().selectUserBySys(jsonObject);
        } else if (operateType.equalsIgnoreCase(PRINT)) {
            resultDto = XMLDealTool.printXML(USERPATH);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
