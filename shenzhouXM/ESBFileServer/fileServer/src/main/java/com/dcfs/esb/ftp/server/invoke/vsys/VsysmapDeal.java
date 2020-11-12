package com.dcfs.esb.ftp.server.invoke.vsys;

import com.dcfs.esb.ftp.common.error.FtpException;
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

/**
 * Created by mocg on 2017/3/2.
 */
public class VsysmapDeal {
    private static final Logger log = LoggerFactory.getLogger(FlowDeal.class);
    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String UPDATE = "update";//NOSONAR
    private static final String SELECT = "select";//NOSONAR
    private static final String QUERY = "query";
    private static final String CLEAN = "clean";//NOSONAR
    private static final String PRINT = "print";
    private String cfgPath = Cfg.getVsysmapCfg();

    public void dealMess(JSONObject jsonObject, Socket socket) throws FtpException {
        ResultDto<String> resultDto = null;
        String operateType = (String) jsonObject.get("operateType");

        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }
        if (operateType.equals(ADD)) {
            resultDto = new VsysmapManager().add(jsonObject);
        } else if (operateType.equals(DEL)) {
            resultDto = new VsysmapManager().del(jsonObject);
        } else if (operateType.equals(QUERY)) {
            resultDto = new VsysmapManager().query();
        } else if (operateType.equals(PRINT)) {
            resultDto = XMLDealTool.printXML(cfgPath);
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
