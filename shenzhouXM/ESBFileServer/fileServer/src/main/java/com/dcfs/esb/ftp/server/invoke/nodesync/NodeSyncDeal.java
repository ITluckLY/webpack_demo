package com.dcfs.esb.ftp.server.invoke.nodesync;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.spring.SpringContext;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Created by huangzbb on 2016/7/29.
 */
public class NodeSyncDeal {
    public static final String NODESYNC = "nodeSync";
    public static final String MAKE_SYNC_CFG = "makeSyncCfg";
    public static final String GENERATE_SYNC_CFG_XML = "generateSyncCfgXml";
    public static final String RECEIVE_CFG_FILE = "receiveCfgFile";
    private static final Logger log = LoggerFactory.getLogger(NodeSyncDeal.class);
    private NodeSyncServiceFactoryFace nodeSyncServiceFactoryFace = SpringContext.getInstance().getNodeSyncServiceFactory();

    public void dealMess(JSONObject jsonObject, Socket socket) {
        ResultDto<String> resultDto;
        String operateType = MessDealTool.getString(jsonObject, "operateType");
        if (null == operateType) {
            log.error("操作类型为空");
            resultDto = ResultDtoTool.buildError("操作类型不能为空");
            MessDealTool.sendBackMes(resultDto, socket);
            return;
        }

        if (NODESYNC.equalsIgnoreCase(operateType)) {
//            NodeSyncManager.getInstance().makeSyncCfg(jsonObject);//NOSONAR
//            resultDto = NodeSyncManager.getInstance().nodeSync(jsonObject);//NOSONAR
            nodeSyncServiceFactoryFace.getNodeSyncService().makeSyncCfg(jsonObject);
            resultDto = nodeSyncServiceFactoryFace.getNodeSyncService().nodeSync(jsonObject);

        } else if (MAKE_SYNC_CFG.equalsIgnoreCase(operateType)) {
//            resultDto = NodeSyncManager.getInstance().makeSyncCfg(jsonObject);//NOSONAR
            resultDto = nodeSyncServiceFactoryFace.getNodeSyncService().makeSyncCfg(jsonObject);

        } else if (GENERATE_SYNC_CFG_XML.equalsIgnoreCase(operateType)) {
            //resultDto = NodeSyncManager.getInstance().generateSyncCfgXml(jsonObject);//NOSONAR
            resultDto = nodeSyncServiceFactoryFace.getNodeSyncService().generateSyncCfgXml(jsonObject);

        } else if (RECEIVE_CFG_FILE.equalsIgnoreCase(operateType)) {
            AbstractCfgFileReceiver cfgFileReceiver = CfgFileReceiverFace.getInstance().getCfgFileReceiver();
            if (cfgFileReceiver != null) {
                resultDto = cfgFileReceiver.receive(jsonObject);
            } else resultDto = ResultDtoTool.buildError("cfgFileReceiver为空");
        } else {
            resultDto = ResultDtoTool.buildError("指定的操作类型不正确");
        }
        MessDealTool.sendBackMes(resultDto, socket);
    }
}
