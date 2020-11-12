package com.dcfs.esb.ftp.server.invoke.filemsg2client;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.spring.SpringContext;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Created by huangzbb on 2017/8/14.
 */
public class FileMsg2ClientDeal {
    private static final Logger log = LoggerFactory.getLogger(FileMsg2ClientDeal.class);
    private static final String REPUSH = "repush";
    private FileMsg2ClientServiceFactoryFace fileMsg2ClientServiceFactory = SpringContext.getInstance().getFileMsg2ClientServiceFactory();

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto = null;
        String operateType = (String) jsonObject.get("operateType");

        if (null == operateType) {
            if (log.isErrorEnabled()) {
                log.error("操作类型为空");
            }
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }
        if (operateType.equals(REPUSH)) {
            resultDto = fileMsg2ClientServiceFactory.getFileMsg2ClientService().push(jsonObject);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
