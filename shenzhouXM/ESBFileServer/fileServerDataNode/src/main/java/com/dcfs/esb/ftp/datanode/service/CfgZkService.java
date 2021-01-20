package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodesManager;
import com.dcfs.esb.ftp.server.invoke.nodesync.AbstractCfgFileReceiver;
import com.dcfs.esb.ftp.server.invoke.nodesync.CfgFileReceiverFace;
import com.dcfs.esb.ftp.spring.EfsProperties;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.zk.ZkClientTool;
import com.dcfs.esb.ftp.zk.ZkPathCons;
import com.google.gson.JsonObject;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mocg on 2017/5/9.
 */
public class CfgZkService {
    private static final Logger log = LoggerFactory.getLogger(CfgZkService.class);
    private static CfgZkService ourInstance = new CfgZkService();
    private EfsProperties efsProperties = SpringContext.getInstance().getEfsProperties();
    private AbstractCfgFileReceiver cfgFileReceiver = CfgFileReceiverFace.getInstance().getCfgFileReceiver();

    public static CfgZkService getInstance() {
        return ourInstance;
    }

    protected ZkClient zkClient;

    private CfgZkService() {
        init();
    }

    private void init() {
        zkClient = ZkClientTool.getInstance().getZkClient();
    }

    /**
     *  配置同步 xml文件 ********************
     */
    public void subscribe4Cfgsync() {
        final String sysname = FtpConfig.getInstance().getSystemName();
        List<String> cfgFileNameList = new ArrayList<>();
        cfgFileNameList.add("components.xml");
        cfgFileNameList.add("crontab.xml");
        cfgFileNameList.add("file.xml");
        cfgFileNameList.add("file_process.xml");/* 文件校验流程*/
        cfgFileNameList.add("file_clean.xml");
        cfgFileNameList.add("file_rename.xml");
        cfgFileNameList.add("flow.xml");
        cfgFileNameList.add("nodes.xml");
        cfgFileNameList.add("route.xml");
        cfgFileNameList.add("services_info.xml");
        cfgFileNameList.add("system.xml");
        cfgFileNameList.add("user.xml");
        cfgFileNameList.add("vsysmap.xml");
        cfgFileNameList.add("client_status.xml");
        cfgFileNameList.add("netty.xml");

        boolean ifCfgSyncByStart = true;
        if (efsProperties != null && !efsProperties.getIfCfgSyncByStart()) {
            log.info("节点启动时不同步配置同步");
            ifCfgSyncByStart = false;
        }
        if (ifCfgSyncByStart) {
            for (String fileName : cfgFileNameList) {
                String cfgContent = readCfgContent(sysname, fileName);
                boolean receive = false;
                try {
                    receive = cfgFileReceiver.receive(sysname, fileName, cfgContent);
                } catch (IOException e) {
                    log.error("", e);
                }
                log.debug("配置#{}-{}-{}#{}", sysname, fileName, receive, cfgContent);
            }
        }

        for (final String fileName : cfgFileNameList) {
            zkClient.subscribeDataChanges(ZkPathCons.PRE_DCFS_CFGSYNC_DN_SYS + sysname + "/" + fileName, new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {
                    if (data == null) return;//fix 节点创建时
                    String content = readCfgContent(sysname, fileName);
                    boolean receive = cfgFileReceiver.receive(sysname, fileName, content);
                    log.debug("配置#{}-{}-{}#{}", sysname, fileName, receive, content);
                    if (StringUtils.equals("nodes.xml", fileName)) updateLegalNodeMap();
                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    //nothing
                }
            });
        }
    }

    private String readCfgContent(String sysname, String fileName) {
        String path = ZkPathCons.PRE_DCFS_CFGSYNC_DN_SYS + sysname + '/' + fileName;
        int count = zkClient.countChildren(path);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String data = zkClient.readData(path + "/" + i);
            builder.append(data);
        }
        return builder.toString();
    }

    private void updateLegalNodeMap() {
        Map<String, JsonObject> dataNodeMap = ZkService.getInstance().getDataNodeMap();
        Map<String, JsonObject> nameNodeMap = ZkService.getInstance().getNameNodeMap();
        Map<String, JsonObject> logNodeMap = ZkService.getInstance().getLogNodeMap();
        Map<String, JsonObject> legalDataNodeMap = ZkService.getInstance().getLegalDataNodeMap();
        Map<String, JsonObject> legalNameNodeMap = ZkService.getInstance().getLegalNameNodeMap();
        Map<String, JsonObject> legalLogNodeMap = ZkService.getInstance().getLegalLogNodeMap();
        for (Map.Entry<String, JsonObject> entry : dataNodeMap.entrySet()) {
            String ipPort = entry.getKey();
            JsonObject jsonObject = entry.getValue();
            String nodeName = jsonObject.get("nodeName").getAsString();//NOSONAR
            if (isLegalNode(nodeName, ipPort, NodeType.DATANODE)) legalDataNodeMap.put(ipPort, jsonObject);
        }
        for (Map.Entry<String, JsonObject> entry : logNodeMap.entrySet()) {
            String ipPort = entry.getKey();
            JsonObject jsonObject = entry.getValue();
            String nodeName = jsonObject.get("nodeName").getAsString();
            if (isLegalNode(nodeName, ipPort, NodeType.LOGNODE)) legalLogNodeMap.put(ipPort, jsonObject);
        }
        for (Map.Entry<String, JsonObject> entry : nameNodeMap.entrySet()) {
            String ipPort = entry.getKey();
            JsonObject jsonObject = entry.getValue();
            String nodeName = jsonObject.get("nodeName").getAsString();
            if (isLegalNode(nodeName, ipPort, NodeType.NAMENODE)) legalNameNodeMap.put(ipPort, jsonObject);
        }
        ZkService.getInstance().setLegalDataNodeMap(legalDataNodeMap);
        ZkService.getInstance().setLegalNameNodeMap(legalNameNodeMap);
        ZkService.getInstance().setLegalLogNodeMap(legalLogNodeMap);
        if (log.isDebugEnabled()) {
            log.debug("--------非法节点注册为合法节点---------");
            log.debug("legalLogNodeMap:{}", ZkService.getInstance().getLegalLogNodeMap());
            log.debug("legalDataNodeMap:{}", ZkService.getInstance().getLegalDataNodeMap());
            log.debug("legalNameNodeMap:{}", ZkService.getInstance().getLegalNameNodeMap());
        }

    }

    /**
     * 判断节点是否合法，不合法的不添加
     *
     * @param nodeName
     * @param nodeType
     * @return
     */
    private boolean isLegalNode(String nodeName, String ipPort, NodeType nodeType) {
        String[] arr = ipPort.split(":");
        boolean hasNode = NodesManager.getInstance().hasNode(nodeName, arr[0], Integer.parseInt(arr[1]), nodeType);
        if (!hasNode) {
            if (log.isWarnEnabled())
                log.warn("非法节点#nodeName:{}, ipPort:{}, nodeType:{}", nodeName, ipPort, nodeType.name());//NOSONAR
        }
        return hasNode;
    }
}
