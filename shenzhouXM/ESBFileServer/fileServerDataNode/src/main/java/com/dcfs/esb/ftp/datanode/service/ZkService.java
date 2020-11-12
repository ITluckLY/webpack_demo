package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.cons.NodeState;
import com.dcfs.esb.ftp.helper.CacheUpdateHelper;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.invoke.node.NodeManager;
import com.dcfs.esb.ftp.server.invoke.node.NodeWorker;
import com.dcfs.esb.ftp.server.invoke.node.NodesManager;
import com.dcfs.esb.ftp.spring.EfsProperties;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.ShortUrlUtil;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esb.ftp.zk.BaseZkService;
import com.dcfs.esb.ftp.zk.ZkClientTool;
import com.dcfs.esb.ftp.zk.ZkPathCons;
import com.dcfs.esb.ftp.zk.ZkServiceHelper;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
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
    private EfsProperties efsProperties = SpringContext.getInstance().getEfsProperties();
    private Map<String, JsonObject> nameNodeMap = new HashMap<>();
    private Map<String, JsonObject> dataNodeMap = new HashMap<>();
    private Map<String, JsonObject> logNodeMap = new HashMap<>();
    private String masterNameNodeIpPort;
    private Map<String, Long> dataNodeTimeMap = new HashMap<>();
    private String currIpPort;
    //private String dataMasterIpPort;//NOSONAR
    private Map<String, JsonObject> legalNameNodeMap = new HashMap<>();
    private Map<String, JsonObject> legalDataNodeMap = new HashMap<>();
    private Map<String, JsonObject> legalLogNodeMap = new HashMap<>();

    private static final String NODE_NAME = "nodeName";

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
        //nameNode
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
            }
        });
        if (log.isInfoEnabled()) {
            log.info("nnList:{}", Arrays.toString(nnList.toArray()));
        }
        addNameNodes(nnList);
        //dataNode
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
                delDataNodes(delList);
                addDataNodes(addList);
                selectDataNodeMaster();
                updateNodelistVersion();
                CacheUpdateHelper.setLatestDataNodeList(false);
            }
        });
        if (log.isInfoEnabled()) log.info("dnList:{}", Arrays.toString(dnList.toArray()));
        addDataNodes(dnList);
        selectDataNodeMaster();
        updateNodelistVersion();

        //logNode
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

        //masterNameNodeIpPort
        zkClient.subscribeDataChanges(ZkPathCons.DCFS_MASTER_NN, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                log.info("masterNameNodeIpPort#dataPath:{},data:{}", dataPath, data);
                masterNameNodeIpPort = (String) data;
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                masterNameNodeIpPort = null;
            }
        });
        masterNameNodeIpPort = zkClient.readData(ZkPathCons.DCFS_MASTER_NN, true);
        log.info("masterNameNodeIpPort:{}", masterNameNodeIpPort);

        //nodelist.version
        zkClient.subscribeDataChanges(ZkPathCons.DCFS_VERSION_NODELIST, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                log.info("nodelistVersion#dataPath:{},data:{}", dataPath, data);
                sysContent.setNodelistVersion((String) data);
                CacheUpdateHelper.setLatestDataNodeList(false);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                sysContent.setNodelistVersion(null);
            }
        });
        String nodelistVersion = zkClient.readData(ZkPathCons.DCFS_VERSION_NODELIST, true);
        sysContent.setNodelistVersion(nodelistVersion);
        log.info("nodelistVersion:{}", nodelistVersion);

        //配置同步
        subscribe4Cfgsync();
    }

    private void subscribe4Cfgsync() {
        CfgZkService.getInstance().subscribe4Cfgsync();
    }

    /**
     *   添加名称节点
     * @param list
     */
    private synchronized void addNameNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = readDataNN(ipPort);
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            nameNodeMap.put(ipPort, jsonObject);
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.RUNNING);
            if (isLegalNode(nodeName, ipPort, NodeType.NAMENODE)) legalNameNodeMap.put(ipPort, jsonObject);
        }
    }

    /**
     *   删除名称节点
     * @param list
     */
    private synchronized void delNameNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = nameNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.STOP);
            nameNodeMap.remove(ipPort);
            legalNameNodeMap.remove(ipPort);
        }
    }

    /**
     *   添加数据节点
     * @param list
     */
    private synchronized void addDataNodes(List<String> list) {
        for (String ipPort : list) {
            Stat stat = new Stat();
            JsonObject jsonObject = readDataDN(ipPort, stat);
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            dataNodeMap.put(ipPort, jsonObject);
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.RUNNING);
            //
            dataNodeTimeMap.put(ipPort, stat.getCzxid());
            if (isLegalNode(nodeName, ipPort, NodeType.DATANODE)) legalDataNodeMap.put(ipPort, jsonObject);
        }
    }

    /**
     *  删除数据节点
     * @param list
     */
    private synchronized void delDataNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = dataNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.STOP);
            dataNodeMap.remove(ipPort);
            dataNodeTimeMap.remove(ipPort);
            legalDataNodeMap.remove(ipPort);
        }
    }

    /**
     *  添加日志节点
     * @param list
     */
    private synchronized void addLogNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = readDataLN(ipPort);
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            logNodeMap.put(ipPort, jsonObject);
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.RUNNING);
            if (isLegalNode(nodeName, ipPort, NodeType.LOGNODE)) legalLogNodeMap.put(ipPort, jsonObject);
        }
    }

    /**
     *   删除日志节点
     * @param list
     */
    private synchronized void delLogNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = logNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            String nodeName = jsonObject.get(NODE_NAME).getAsString();
            NodesManager.getInstance().changeNodeState(nodeName, ipPort, NodeState.STOP);
            logNodeMap.remove(ipPort);
            legalLogNodeMap.remove(ipPort);
        }
    }

    /**
     *      临时节点
     * @param zkClient
     */
    private synchronized void ephemeralNode(ZkClient zkClient) {
        setMaster(false);
        String ipPort = Cfg.getHostTrueIp() + ":" + FtpConfig.getInstance().getCommandPort();
        currIpPort = ipPort;
        String portInfo = generatePortInfo();
        log.info("zk Ephemeral#/dcfs/live/dn/{},data:{}", ipPort, portInfo);
        zkClient.createEphemeral(ZkPathCons.PRE_DCFS_LIVE_DN + ipPort, portInfo);

        String cfgSyncInfo = generateCfgSyncInfo();
        log.info("zk Ephemeral#/dcfs/cfgsync/dn/{},data:{}", ipPort, cfgSyncInfo);
        zkClient.createEphemeral(ZkPathCons.PRE_DCFS_CFGSYNC_DN + ipPort, cfgSyncInfo);
    }

    private String generatePortInfo() {
        Map<String, Object> portMap = new HashMap<>();
        NodeManager manager = NodeManager.getInstance();
        portMap.put("cmd", StringTool.toInteger(manager.getEntryValue("CMD_PORT")));
        portMap.put("ftpServ", StringTool.toInteger(manager.getEntryValue("FTP_SERV_PORT")));
        portMap.put("ftpManage", StringTool.toInteger(manager.getEntryValue("FTP_MANAGE_PORT")));
        portMap.put("dstbReceive", StringTool.toInteger(manager.getEntryValue("DISTRIBUTE_FILE_RECEIVE_PORT")));
        portMap.put(NODE_NAME, FtpConfig.getInstance().getNodeName());
        portMap.put("sysName", NodeWorker.getInstance().getSysName());
        portMap.put("hostName", Cfg.getHostName());
        return GsonUtil.toJson(portMap);
    }

    private String generateCfgSyncInfo() {
        Map<String, Object> portMap = new HashMap<>();
        boolean ifCfgSyncByStart = true;
        if (efsProperties != null && !efsProperties.getIfCfgSyncByStart()) {
            log.info("节点启动时不同步配置同步");
            ifCfgSyncByStart = false;
        }
        portMap.put("ifCfgSyncByStart", ifCfgSyncByStart);
        portMap.put("syncedByStart", false);
        portMap.put("version", 1);
        return GsonUtil.toJson(portMap);
    }

    private void setMaster(boolean isMaster) {
        sysContent.setMaster(isMaster);
        if (isMaster) {
            zkClient.writeData(ZkPathCons.DCFS_MASTER_DN, currIpPort);
        }
    }

    // 选举新的master
    private void selectDataNodeMaster() {
        if (log.isInfoEnabled()) log.info("开始选举 - {}", dataNodeTimeMap.toString());
        if (dataNodeTimeMap.size() == 0) return;
        long min = Long.MAX_VALUE;
        String masterIpPort = null;
        for (Map.Entry<String, Long> entry : dataNodeTimeMap.entrySet()) {
            if (entry.getValue() < min) {
                masterIpPort = entry.getKey();
                min = entry.getValue();
            }
        }
        //dataMasterIpPort = masterIpPort;//NOSONAR
        String thisIpPort = currIpPort;
        log.debug("thisIpPort:{},masterIpPort:{}", thisIpPort, masterIpPort);
        if (thisIpPort.equals(masterIpPort)) {
            setMaster(true);
            log.info("master dataNode - ip:port = {}", thisIpPort);
        } else setMaster(false);
    }

    //dataNode节点状态改变则更新nodelist版本号
    public boolean updateNodelistVersion() {
        //有live的nameNode节点则由nameNode的主节点更新
        if (nameNodeMap.size() > 0) return false;
        if (!sysContent.isMaster()) return false;
        String version = String.valueOf(ShortUrlUtil.short36(System.currentTimeMillis() - SvrGlobalCons.TWEPOCH));
        log.debug("zk中的节点列表版本号{}", version);
        try {
            zkClient.writeData(ZkPathCons.DCFS_VERSION_NODELIST, version);
            sysContent.setNodelistVersion(version);
        } catch (Exception e) {
            log.error("更新zk中的节点列表版本号失败", e);
            return false;
        }
        return true;
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

    public Map<String, JsonObject> getLogNodeMap() {
        return logNodeMap;
    }

    public String getMasterNameNodeIpPort() {
        //需要确保是存活状态的
        if (nameNodeMap.containsKey(masterNameNodeIpPort)) return masterNameNodeIpPort;
        return null;
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

    public void setLegalNameNodeMap(Map<String, JsonObject> legalNameNodeMap) {
        this.legalNameNodeMap = legalNameNodeMap;
    }

    public void setLegalDataNodeMap(Map<String, JsonObject> legalDataNodeMap) {
        this.legalDataNodeMap = legalDataNodeMap;
    }

    public void setLegalLogNodeMap(Map<String, JsonObject> legalLogNodeMap) {
        this.legalLogNodeMap = legalLogNodeMap;
    }
}
