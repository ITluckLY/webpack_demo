package com.dcfs.esb.ftp.namenode.service;

import com.dcfs.esb.ftp.adapter.TCPAdapter;
import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.interfases.NodeCfgSyncFace;
import com.dcfs.esb.ftp.namenode.spring.NameSpringContext;
import com.dcfs.esb.ftp.namenode.spring.core.entity.biz.CfgFile;
import com.dcfs.esb.ftp.namenode.spring.core.repository.CfgFileRepository;
import com.dcfs.esb.ftp.report.CfgSyncReport;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.servcomm.helper.RequestMsgHelper;
import com.dcfs.esb.ftp.server.invoke.BusDeal;
import com.dcfs.esb.ftp.server.invoke.nodesync.NodeSyncDeal;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 需要单粒模式
 * Created by mocg on 2016/7/28.
 */
public class NodeCfgSyncService implements NodeCfgSyncFace {
    private static final Logger log = LoggerFactory.getLogger(NodeCfgSyncService.class);
    private static NodeCfgSyncService ourInstance = new NodeCfgSyncService();
    private final String nodetype = NodeType.DATANODE.name();

    private static final String SYS_NAME = "sysName";

    private NodeCfgSyncService() {
    }

    public static NodeCfgSyncService getInstance() {
        return ourInstance;
    }

    @Override
    public boolean sync(String sysName, String cfgName, CfgSyncReport report) throws IOException {//NOSONAR
        if ("cfg.xml".equals(cfgName)) return false;
        Map<String, JsonObject> dataNodeMap = ZkService.getInstance().getDataNodeMap();
        if (dataNodeMap == null || dataNodeMap.isEmpty()) {
            log.warn("没有datanode");
            return false;
        }

        CfgFileRepository cfgFileRepository = NameSpringContext.getInstance().getCfgFileRepository();
        List<CfgFile> cfgFileList = cfgFileRepository.findByFilenameAndSystemAndNodetype(cfgName, sysName, nodetype);
        if (cfgFileList.isEmpty()) return false;
        String cfgContent = cfgFileList.get(0).getContent();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(SYS_NAME, sysName);
        dataMap.put("cfgName", cfgName);
        dataMap.put("cfgContent", cfgContent);
        String msg = RequestMsgHelper.generate(BusDeal.CFGSYNC, NodeSyncDeal.RECEIVE_CFG_FILE, dataMap);
        log.debug("配置同步报文:{}", msg);
        //
        for (Map.Entry<String, JsonObject> entry : dataNodeMap.entrySet()) {
            String ipPort = entry.getKey();
            JsonObject value = entry.getValue();
            String nodeName = value.get("nodeName").getAsString();
            String sysName1 = value.get(SYS_NAME).getAsString();
            if (StringUtils.equals(sysName, sysName1)) {
                try {
                    String[] ipPortArr = ipPort.split(":");
                    String ip = ipPortArr[0];
                    int cmdPort = Integer.parseInt(ipPortArr[1]);
                    TCPAdapter tcpAdapter = new TCPAdapter(ip, cmdPort);
                    ResultDto<String> dto = tcpAdapter.invoke(msg, String.class);
                    if (log.isDebugEnabled()) log.debug("dto:{}", GsonUtil.toJson(dto));
                    report.put(nodeName, ResultDtoTool.isSuccess(dto) ? "true" : StringUtils.defaultIfEmpty(dto.getMessage(), "同步失败"));
                    log.debug("配置同步成功#nodeName:{},ipPort:{},cfgName:{}", nodeName, ipPort, cfgName);
                } catch (Exception e) {
                    report.put(nodeName, "网络异常");
                    log.error("同步配置文件err,ipPort:" + ipPort, e);
                }
            } else {
                report.put(nodeName, "系统名称不相同");
                log.warn("配置同步异常#系统名称不相同#节点名称:{},当前系统:{},节点系统:{}", nodeName, sysName, sysName1);
            }
        }
        return true;
    }

    public boolean syncByStartWithCmdport(String sysName, String ip, int cmdPort) {
        String[] cfgNameArr = new String[]{"components.xml", "crontab.xml", "file.xml"
                , "file_clean.xml", "file_rename.xml", "flow.xml", "nodes.xml", "rule.xml"
                , "services_info.xml", "system.xml", "user.xml"};
        boolean result = true;
        try {
            for (String cfgName : cfgNameArr) {//NOSONAR
                CfgFileRepository cfgFileRepository = NameSpringContext.getInstance().getCfgFileRepository();
                List<CfgFile> cfgFileList = cfgFileRepository.findByFilenameAndSystemAndNodetype(cfgName, sysName, nodetype);
                if (cfgFileList.isEmpty()) {
                    boolean makeSyncCfg = NodeSyncService.getInstance().makeSyncCfgtoDB(sysName, cfgName);
                    if (!makeSyncCfg) continue;
                }
                cfgFileList = cfgFileRepository.findByFilenameAndSystemAndNodetype(cfgName, sysName, nodetype);
                if (cfgFileList.isEmpty()) continue;
                String cfgContent = cfgFileList.get(0).getContent();

                Map<String, String> dataMap = new HashMap<>();
                dataMap.put(SYS_NAME, sysName);
                dataMap.put("cfgName", cfgName);
                dataMap.put("cfgContent", cfgContent);
                String msg = RequestMsgHelper.generate(BusDeal.CFGSYNC, NodeSyncDeal.RECEIVE_CFG_FILE, dataMap);
                log.debug("配置同步报文:{}", msg);
                TCPAdapter tcpAdapter = new TCPAdapter(ip, cmdPort);
                ResultDto<String> dto = tcpAdapter.invoke(msg, String.class);
                if (log.isDebugEnabled()) log.debug("dto:{}", GsonUtil.toJson(dto));
                boolean syncSuss = ResultDtoTool.isSuccess(dto);
                if (!syncSuss) result = false;
                log.info("节点启动时配置同步?cfgName:{},syncSuss:{},ip:{},port:{},result:{}", cfgName, syncSuss, ip, cmdPort, result);
            }
        } catch (IOException e) {
            result = false;
            log.error("节点启动时配置同步 err", e);
        }
        return result;
    }
}
