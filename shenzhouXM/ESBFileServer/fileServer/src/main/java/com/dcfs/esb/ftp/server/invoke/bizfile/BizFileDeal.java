package com.dcfs.esb.ftp.server.invoke.bizfile;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.spring.SpringContext;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Created by huangzbb on 2016/10/19.
 */
public class BizFileDeal {
    public static final String SEL_BY_REQUEST_FILE_PATH = "selByRequestFilePath";
    private static final Logger log = LoggerFactory.getLogger(BizFileDeal.class);
    BizFileServiceFactoryFace bizFileServiceFactory = SpringContext.getInstance().getBizFileServiceFactory();

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
        if (operateType.equals(SEL_BY_REQUEST_FILE_PATH)) {
            resultDto = bizFileServiceFactory.getBizFileService().selByRequestFilePath(jsonObject);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
