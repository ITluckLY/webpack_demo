package com.dcfs.esb.ftp.server.invoke.version;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.utils.ObjectsTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Created by xujianj on 2016/9/19.
 */
public class VersionDeal {
    private static final Logger log = LoggerFactory.getLogger(VersionDeal.class);
    private static String version = Cfg.getApiVersion();
    private static final String SELECT = "select";

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto;
        String operateType = MessDealTool.getString(jsonObject, "operateType");
        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }
        if (operateType.equalsIgnoreCase(SELECT)) {
            String clientVersion = jsonObject.getString("clientVersion");
            boolean update = false;
            if (!ObjectsTool.equals(version, clientVersion)) {
                update = true;
            }
            String json = "{\"update\":" + update + ",\"version\":" + version + "}";
            resultDto = ResultDtoTool.buildSucceed(json);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
