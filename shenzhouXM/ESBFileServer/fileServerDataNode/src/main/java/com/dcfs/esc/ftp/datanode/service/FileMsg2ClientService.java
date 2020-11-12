package com.dcfs.esc.ftp.datanode.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.invoke.filemsg2client.FileMsg2ClientServiceFace;
import com.dcfs.esb.ftp.server.system.*;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by huangzbb on 2017/8/14.
 */
public class FileMsg2ClientService implements FileMsg2ClientServiceFace {
    private static final Logger log = LoggerFactory.getLogger(FileMsg2ClientService.class);
    private static FileMsg2ClientService ourInstance = new FileMsg2ClientService();
    private String toUid;//NOSONAR
    private String fromUid;
    private String routeName;
    private String tranCode;
    private String sysname;//NOSONAR
    private String serverFileName;
    private String clientFileName;
    private String prenano;
    private String sync;//NOSONAR
    private String msgId;
    private String flowNo;

    public static FileMsg2ClientService getInstance() {
        return ourInstance;
    }

    /**
     * 处理前台报文
     *
     * @param jsonObject
     */
    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        toUid = MessDealTool.getString(data, "toUid");
        fromUid = MessDealTool.getString(data, "fromUid");
        routeName = MessDealTool.getString(data, "routeName");
        tranCode = MessDealTool.getString(data, "tranCode");
        sysname = MessDealTool.getString(data, "sysname");
        serverFileName = MessDealTool.getString(data, "serverFileName");
        clientFileName = MessDealTool.getString(data, "clientFileName");
        prenano = MessDealTool.getString(data, "prenano");
        sync = MessDealTool.getString(data, "sync");
        msgId = MessDealTool.getString(data, "msgId");
        flowNo = MessDealTool.getString(data, "flowNo");
    }

    @Override
    public ResultDto<String> push(JSONObject jsonObject) {
        load(jsonObject);
        // ref com.dcfs.esb.ftp.impls.service.FileRouteHandler#invokeInner
        SystemInfo systemInfo = SystemManage.getInstance().getSystemInfo(routeName);
        if (systemInfo == null) {
            return ResultDtoTool.buildError("该文件路由不存在");
        }
        if (StringUtils.equals(systemInfo.getProtocol(), "ftsfilemsg")) {
            return ResultDtoTool.buildError("路由协议不一致");
        }
        FileRouteArgs routeArgs = new FileRouteArgs();
        routeArgs.setTranCode(tranCode);
        routeArgs.setSvrFilePath(serverFileName);
        routeArgs.setUploadUid(fromUid);
        routeArgs.setNano(Long.parseLong(prenano));
        routeArgs.setMsgId(Long.parseLong(msgId));
        routeArgs.setFlowNo(flowNo);
        String targetFileName = clientFileName.substring(clientFileName.indexOf('/'));// 去掉uid前缀
        final IProtocol protocol = ProtocolFactory.getProtocol(systemInfo, null, targetFileName, null, routeArgs);
        try {
            boolean upload = protocol.uploadBySync();
            return ResultDtoTool.buildSucceed(String.valueOf(upload));
        } catch (FtpException e) {
            log.error("nano:{}#flowNo:{}#重新发起文件路由失败#msgId:{},dest:{}", prenano, flowNo, msgId, routeName, e);
        }
        return ResultDtoTool.buildError("文件路由失败");
    }
}
