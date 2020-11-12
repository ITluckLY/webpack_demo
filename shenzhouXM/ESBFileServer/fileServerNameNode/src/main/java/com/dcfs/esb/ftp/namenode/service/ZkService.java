package com.dcfs.esb.ftp.namenode.service;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.cons.NodeState;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.invoke.component.ComponentManager;
import com.dcfs.esb.ftp.server.invoke.crontab.CrontabManager;
import com.dcfs.esb.ftp.server.invoke.file.FileManager;
import com.dcfs.esb.ftp.server.invoke.node.NodeManager;
import com.dcfs.esb.ftp.server.invoke.node.NodeWorker;
import com.dcfs.esb.ftp.server.invoke.node.NodesManager;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.ShortUrlUtil;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esb.ftp.zk.BaseZkService;
import com.dcfs.esb.ftp.zk.ZkClientTool;
import com.dcfs.esb.ftp.zk.ZkPathCons;
import com.dcfs.esb.ftp.zk.ZkServiceHelper;
import com.google.gson.JsonObject;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by mocg on 2016/9/30.
 */
public class ZkService extends BaseZkService {
    private static final Logger log = LoggerFactory.getLogger(ZkService.class);
    private static ZkService ourInstance = new ZkService();
    private SysContent sysContent = SysContent.getInstance();
    private Map<String, JsonObject> nameNodeMap = new HashMap<>();
    private Map<String, JsonObject> dataNodeMap = new HashMap<>();
    private Map<String, JsonObject> logNodeMap = new HashMap<>();
    private Map<String, Long> nameNodeTimeMap = new HashMap<>();
    private String currIpPort;
    private String nameMasterIpPort;
    private Map<String, JsonObject> legalNameNodeMap = new HashMap<>();
    private Map<String, JsonObject> legalDataNodeMap = new HashMap<>();
    private Map<String, JsonObject> legalLogNodeMap = new HashMap<>();

    private static final String NODE_NAME = "nodeName";//NOSONAR
    private static final String SYS_NAME = "sysName";

    private ZkService() {
        init();
        ZkServiceHelper.setZkService(this);
    }

    public static ZkService getInstance() {
        return ourInstance;
    }

    public void noticeStart() {
        log.info("zk注册...");
        //启动时把本地配置中的全部节点状态设置为未启动
        NodesManager.getInstance().changeAllNodeState(NodeState.STOP);
        ZkClientTool.getInstance().initFsZkTree(zkClient);
        ephemeralNode(zkClient);
        initCfgVersion();
    }

    public void subscribe() {//NOSONAR
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                log.info("KeeperState:{}", state);
            }

            @Override
            public void handleNewSession() throws Exception {
                ephemeralNode(zkClient);
            }

            @Override
            public void handleSessionEstablishmentError(Throwable error) throws Exception {
                log.error("zookeeper SessionEstablishmentError", error);
            }
        });

        List<String> nnList = zkClient.subscribeChildChanges(ZkPathCons.DCFS_LIVE_NN, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if (currentChilds == null) return;
                if (log.isInfoEnabled()) {
                    log.info("{}:{}", parentPath, Arrays.toString(currentChilds.toArray()));//NOSONAR
                }
                //
                List<String> delList = new ArrayList<>();
                List<String> addList = new ArrayList<>();
                Set<String> keySet = nameNodeMap.keySet();
                for (String child : keySet) {
                    if (!currentChilds.contains(child)) delList.add(child);
                }
                for (String child : currentChilds) {
                    if (!keySet.contains(child)) addList.add(child);
                }

                delNameNodes(delList);
                addNameNodes(addList);
                selectNameNodeMaster();
            }
        });

        if (log.isInfoEnabled()) log.info("nnList:{}", Arrays.toString(nnList.toArray()));
        addNameNodes(nnList);
        selectNameNodeMaster();

        List<String> dnList = zkClient.subscribeChildChanges(ZkPathCons.DCFS_LIVE_DN, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if (currentChilds == null) return;
                if (log.isInfoEnabled()) {
                    log.info("{}:{}", parentPath, Arrays.toString(currentChilds.toArray()));
                }
                //
                List<String> delList = new ArrayList<>();
                List<String> addList = new ArrayList<>();
                Set<String> keySet = dataNodeMap.keySet();
                for (String child : keySet) {
                    if (!currentChilds.contains(child)) delList.add(child);
                }
                for (String child : currentChilds) {
                    if (!keySet.contains(child)) addList.add(child);
                }
                Set<String> sysNameSet = new HashSet<>();
                delDataNodes(delList, sysNameSet);
                addDataNodes(addList, sysNameSet);
                updateNodelistVersion();
            }
        });
        if (log.isInfoEnabled()) log.info("dnList:{}", Arrays.toString(dnList.toArray()));
        addDataNodes(dnList, null);
        updateNodelistVersion();

        List<String> lnList = zkClient.subscribeChildChanges(ZkPathCons.DCFS_LIVE_LN, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if (currentChilds == null) return;
                if (log.isInfoEnabled()) {
                    log.info("{}:{}", parentPath, Arrays.toString(currentChilds.toArray()));
                }
                //
                List<String> delList = new ArrayList<>();
                List<String> addList = new ArrayList<>();
                Set<String> keySet = logNodeMap.keySet();
                for (String child : keySet) {
                    if (!currentChilds.contains(child)) delList.add(child);
                }
                for (String child : currentChilds) {
                    if (!keySet.contains(child)) addList.add(child);
                }
                delLogNodes(delList);
                addLogNodes(addList);
            }
        });
        if (log.isInfoEnabled()) log.info("lnList:{}", Arrays.toString(lnList.toArray()));
        addLogNodes(lnList);

        //配置同步
        subscribe4Cfgsync();
    }

    private void delNameNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = nameNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.STOP);
            nameNodeMap.remove(ipPort);
            nameNodeTimeMap.remove(ipPort);
            legalNameNodeMap.remove(ipPort);
        }
    }

    private void addNameNodes(List<String> list) {
        for (String ipPort : list) {
            Stat stat = new Stat();
            JsonObject jsonObject = readDataNN(ipPort, stat);
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            nameNodeMap.put(ipPort, jsonObject);
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.RUNNING);
            //
            nameNodeTimeMap.put(ipPort, stat.getCzxid());
            if (isLegalNode(nodeName, ipPort, NodeType.NAMENODE)) legalNameNodeMap.put(ipPort, jsonObject);
        }
    }

    private void delDataNodes(List<String> list, Set<String> sysNameSet) {
        for (String ipPort : list) {
            JsonObject jsonObject = dataNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            String sysName = jsonObject.get(SYS_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.STOP);
            dataNodeMap.remove(ipPort);
            if (sysNameSet != null) sysNameSet.add(sysName);
            legalDataNodeMap.remove(ipPort);
        }
    }

    private void addDataNodes(List<String> list, Set<String> sysNameSet) {
        for (String ipPort : list) {
            JsonObject jsonObject = readDataDN(ipPort);
            dataNodeMap.put(ipPort, jsonObject);
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            String sysName = jsonObject.get(SYS_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.RUNNING);
            if (sysNameSet != null) sysNameSet.add(sysName);

            //启动时同步配置 nameNode为master时才同步给其他dataNode
            /*if (SysContent.getInstance().isMaster()) {//NOSONAR
                String[] ipPortArr = ipPort.split(":");
                JsonObject syncJsonObj = readCfgSyncDataDN(ipPort);
                boolean ifCfgSyncByStart = syncJsonObj.get("ifCfgSyncByStart").getAsBoolean();
                boolean syncedByStart = syncJsonObj.get("syncedByStart").getAsBoolean();
                log.debug("启动时是否同步配置#{}#{},已同步过:{}", ipPort, ifCfgSyncByStart, syncedByStart);
                if (ifCfgSyncByStart && !syncedByStart) {
                    boolean succ = NodeCfgSyncService.getInstance().syncByStartWithCmdport(sysName, ipPortArr[0], Integer.parseInt(ipPortArr[1]));
                    if (succ) {
                        syncJsonObj.addProperty("syncedByStart", true);
                        writeCfgSyncDataDN(ipPort, syncJsonObj);
                    }
                }
            }*/
            if (isLegalNode(nodeName, ipPort, NodeType.DATANODE)) legalDataNodeMap.put(ipPort, jsonObject);
        }
    }

    private void delLogNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = logNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.STOP);
            logNodeMap.remove(ipPort);
            legalLogNodeMap.remove(ipPort);
        }
    }

    private void addLogNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = readDataLN(ipPort);
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.RUNNING);
            logNodeMap.put(ipPort, jsonObject);
            if (isLegalNode(nodeName, ipPort, NodeType.LOGNODE)) legalLogNodeMap.put(ipPort, jsonObject);
        }
    }

    private void ephemeralNode(ZkClient zkClient) {
        setMaster(false);
        String ipPort = Cfg.getHostTrueIp() + ":" + FtpConfig.getInstance().getCommandPort();
        currIpPort = ipPort;
        String portInfo = generatePortInfo();
        log.info("zk Ephemeral#/dcfs/live/nn/{},data:{}", ipPort, portInfo);
        zkClient.createEphemeral(ZkPathCons.PRE_DCFS_LIVE_NN + ipPort, portInfo);
    }

    private String generatePortInfo() {
        Map<String, Object> portMap = new HashMap<>();
        NodeManager manager = NodeManager.getInstance();
        portMap.put("cmd", StringTool.toInteger(manager.getEntryValue("CMD_PORT")));
        portMap.put("ftpServ", StringTool.toInteger(manager.getEntryValue("FTP_SERV_PORT")));
        portMap.put("ftpManage", StringTool.toInteger(manager.getEntryValue("FTP_MANAGE_PORT")));
        portMap.put("dstbReceive", StringTool.toInteger(manager.getEntryValue("DISTRIBUTE_FILE_RECEIVE_PORT")));
        portMap.put(NODE_NAME, FtpConfig.getInstance().getNodeName());
        portMap.put(SYS_NAME, NodeWorker.getInstance().getSysName());
        portMap.put("hostName", Cfg.getHostName());
        return GsonUtil.toJson(portMap);
    }

    private JsonObject readCfgSyncDataDN(String ipPort) {//NOSONAR
        String data = zkClient.readData(ZkPathCons.PRE_DCFS_CFGSYNC_DN + ipPort);
        return GsonUtil.toJsonObj(data);
    }

    private void writeCfgSyncDataDN(String ipPort, JsonObject syncJsonObj) {//NOSONAR
        zkClient.writeData(ZkPathCons.PRE_DCFS_CFGSYNC_DN + ipPort, GsonUtil.toJson(syncJsonObj));
    }

    private void initCfgVersion() {
        zkClient.writeData("/dcfs/cfg/components.xml", ComponentManager.getInstance().getVersion());
        zkClient.writeData("/dcfs/cfg/crontab.xml", CrontabManager.getInstance().getVersion());
        zkClient.writeData("/dcfs/cfg/file.xml", FileManager.getInstance().getVersion());
        zkClient.writeData("/dcfs/cfg/file_clean.xml", "1");
        zkClient.writeData("/dcfs/cfg/file_rename.xml", "1");
        zkClient.writeData("/dcfs/cfg/flow.xml", "1");
        zkClient.writeData("/dcfs/cfg/nodes.xml", NodesManager.getInstance().getVersion());
        zkClient.writeData("/dcfs/cfg/route.xml", "1");
        zkClient.writeData("/dcfs/cfg/rule.xml", "1");
        zkClient.writeData("/dcfs/cfg/services_info.xml", "1");
        zkClient.writeData("/dcfs/cfg/system.xml", "1");
        zkClient.writeData("/dcfs/cfg/user.xml", "1");
    }

    private void setMaster(boolean isMaster) {
        sysContent.setMaster(isMaster);
        if (isMaster) {
            zkClient.writeData(ZkPathCons.DCFS_MASTER_NN, currIpPort);
        }
    }

    // 选举新的master
    private void selectNameNodeMaster() {
        if (log.isInfoEnabled()) log.info("开始选举 - {}", nameNodeTimeMap.toString());
        if (nameNodeTimeMap.size() == 0) return;
        long min = Long.MAX_VALUE;
        String masterIpPort = null;
        for (Map.Entry<String, Long> entry : nameNodeTimeMap.entrySet()) {
            if (entry.getValue() < min) {
                masterIpPort = entry.getKey();
                min = entry.getValue();
            }
        }
        nameMasterIpPort = masterIpPort;
        String thisIpPort = currIpPort;
        log.debug("thisIpPort:{},masterIpPort:{}", thisIpPort, masterIpPort);
        if (thisIpPort.equals(masterIpPort)) {
            setMaster(true);
            log.info("master nameNode - ip:port = {}", thisIpPort);
        } else setMaster(false);
    }

    //dataNode节点状态改变则更新nodelist版本号
    public boolean updateNodelistVersion() {//NOSONAR
        if (!sysContent.isMaster()) return false;
        String version = String.valueOf(ShortUrlUtil.short36(System.currentTimeMillis() - SvrGlobalCons.TWEPOCH));
        log.debug("zk中的节点列表版本号{}", version);
        try {
            zkClient.writeData(ZkPathCons.DCFS_VERSION_NODELIST, version);
        } catch (Exception e) {
            log.error("更新zk中的节点列表版本号失败", e);
            return false;
        }
        return true;
    }

    // 运行期间，非法节点注册为合法节点
    private void subscribe4Cfgsync() {//NOSONAR
        Set<String> systemSet = NodesManager.getInstance().listSystem();
        String fileName = "nodes.xml";
        for (String sysname : systemSet) {
            zkClient.subscribeDataChanges(ZkPathCons.PRE_DCFS_CFGSYNC_DN_SYS + sysname + "/" + fileName, new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {
                    if (data == null) return;//fix 节点创建时
                    for (Map.Entry<String, JsonObject> entry : dataNodeMap.entrySet()) {
                        String ipPort = entry.getKey();
                        JsonObject jsonObject = entry.getValue();
                        String nodeName = jsonObject.get("nodeName").getAsString();
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
                    if (log.isDebugEnabled()) {
                        log.debug("--------非法节点注册为合法节点---------");
                        log.debug("legalLogNodeMap:{}", GsonUtil.toJson(legalLogNodeMap));
                        log.debug("legalDataNodeMap:{}", GsonUtil.toJson(legalDataNodeMap));
                        log.debug("legalNameNodeMap:{}", GsonUtil.toJson(legalNameNodeMap));
                    }
                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    //nothing
                }
            });
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
            if (log.isWarnEnabled()) log.warn("非法节点#nodeName:{}, ipPort:{}, nodeType:{}", nodeName, ipPort, nodeType.name());//NOSONAR
        }
        return hasNode;
    }

    public Map<String, JsonObject> getNameNodeMap() {
        return nameNodeMap;
    }

    public Map<String, JsonObject> getDataNodeMap() {
        return dataNodeMap;
    }

    public Map<String, JsonObject> getlogNodeMap() {
        return logNodeMap;
    }

    public String getNameMasterIpPort() {
        return nameMasterIpPort;
    }

    public Map<String, JsonObject> getLegalNameNodeMap() {
        return legalNameNodeMap;
    }

    public Map<String, JsonObject> getLegalDataNodeMap() {
        return legalDataNodeMap;
    }

    public Map<String, JsonObject> getLegalLogNodeMap() {
        return legalLogNodeMap;
    }
}
