package com.dc.smarteam.common.zk;

import com.dc.smarteam.common.json.GsonUtil;
import com.dc.smarteam.cons.GlobalCons;
import com.dc.smarteam.util.ShortUrlUtil;
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
    private Map<String, JsonObject> nameNodeMap = new HashMap<String, JsonObject>();
    private Map<String, JsonObject> dataNodeMap = new HashMap<>();
    private Map<String, JsonObject> logNodeMap = new HashMap<>();
    private Map<String, JsonObject> monitorNodeMap = new HashMap<String, JsonObject>();
    private String masterNameNodeIpPort;
    private String masterNameNodeName;

    private ZkService() {
        init();
    }

    public static ZkService getInstance() {
        return ourInstance;
    }

    @Override
    public void init() {
        super.init();
        noticeStart();
    }

    public void noticeStart() {
        log.info("zk注册...");
        ZkClientTool.getInstance().initFsZkTree();
        ephemeralNode(zkClient);
    }

    public void subscribe() {
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
                if (log.isInfoEnabled()) {
                    log.info("{}:{}", parentPath, Arrays.toString(currentChilds.toArray()));//NOSONAR
                }
                //
                List<String> delList = new ArrayList<String>();
                List<String> addList = new ArrayList<String>();
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
        log.info("nnList:{}", Arrays.toString(nnList.toArray()));
        addNameNodes(nnList);

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

        List<String> mnList = zkClient.subscribeChildChanges(ZkPathCons.DCFS_LIVE_MN, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if (currentChilds == null) return;
                if (log.isInfoEnabled()) {
                    log.info("{}:{}", parentPath, Arrays.toString(currentChilds.toArray()));
                }
                //
                List<String> delList = new ArrayList<String>();
                List<String> addList = new ArrayList<String>();
                Set<String> keySet = monitorNodeMap.keySet();
                for (String child : keySet) {
                    if (!currentChilds.contains(child)) delList.add(child);
                }
                for (String child : currentChilds) {
                    if (!keySet.contains(child)) addList.add(child);
                }
                delMonitorNodes(delList);
                addMonitorNodes(addList);
            }
        });
        log.info("mnList:{}", Arrays.toString(mnList.toArray()));
        addMonitorNodes(mnList);

        List<String> dnList = zkClient.subscribeChildChanges(ZkPathCons.DCFS_LIVE_DN, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if (currentChilds == null) return;
                if (log.isInfoEnabled()) {
                    log.info("{}:{}", parentPath, Arrays.toString(currentChilds.toArray()));
                }
                //
                List<String> delList = new ArrayList<String>();
                List<String> addList = new ArrayList<String>();
                Set<String> keySet = dataNodeMap.keySet();
                for (String child : keySet) {
                    if (!currentChilds.contains(child)) delList.add(child);
                }
                for (String child : currentChilds) {
                    if (!keySet.contains(child)) addList.add(child);
                }
                delDataNodes(delList);
                addDataNodes(addList);
            }
        });
        log.info("dnList:{}", Arrays.toString(dnList.toArray()));
        addDataNodes(dnList);

        //logNode
        List<String> lnList = zkClient.subscribeChildChanges(ZkPathCons.DCFS_LIVE_LN, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if (currentChilds == null) return;
                if (log.isInfoEnabled()) {
                    log.info("{}:{}", parentPath, Arrays.toString(currentChilds.toArray()));
                }
                //
                List<String> delList = new ArrayList<String>();
                List<String> addList = new ArrayList<String>();
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
        log.info("lnList:{}", Arrays.toString(lnList.toArray()));
        addLogNodes(lnList);
    }


    private synchronized void delNameNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = nameNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            nameNodeMap.remove(ipPort);
        }
    }

    private synchronized void addNameNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = readDataNN(ipPort);
            nameNodeMap.put(ipPort, jsonObject);
        }
    }

    private void delMonitorNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = monitorNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            monitorNodeMap.remove(ipPort);
        }
    }

    private void addMonitorNodes(List<String> list) {
        for (String ipPort : list) {
            Stat stat = new Stat();
            JsonObject jsonObject = readDataMN(ipPort, stat);
            monitorNodeMap.put(ipPort, jsonObject);

            // 版本发布之后，及时更新zk path数据
            zkClient.subscribeDataChanges(ZkPathCons.PRE_DCFS_LIVE_MN + ipPort, new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {
                    log.info("monitorNodeDataChange#dataPath:{},data:{}", dataPath, data);
                    String ipPort0 = dataPath.substring(dataPath.lastIndexOf('/') + 1);
                    monitorNodeMap.put(ipPort0, GsonUtil.toJsonObj((String) data));
                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    //nothing
                }
            });
        }
    }

    private synchronized void addDataNodes(List<String> list) {
        for (String ipPort : list) {
            Stat stat = new Stat();
            JsonObject jsonObject = readDataDN(ipPort, stat);
            dataNodeMap.put(ipPort, jsonObject);
        }
    }

    private synchronized void delDataNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = dataNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            dataNodeMap.remove(ipPort);
        }
    }

    private synchronized void addLogNodes(List<String> list) {
        for (String ipPort : list) {
            Stat stat = new Stat();
            JsonObject jsonObject = readDataLN(ipPort, stat);
            logNodeMap.put(ipPort, jsonObject);
        }
    }

    private synchronized void delLogNodes(List<String> list) {
        for (String ipPort : list) {
            JsonObject jsonObject = logNodeMap.get(ipPort);
            if (jsonObject == null) continue;
            logNodeMap.remove(ipPort);
        }
    }

    private synchronized void ephemeralNode(ZkClient zkClient) {
        //nothing
    }

    public Map<String, JsonObject> getNameNodeMap() {
        return nameNodeMap;
    }

    public Map<String, JsonObject> getMonitorNodeMap() {
        return monitorNodeMap;
    }

    public String getMasterNameNodeIpPort() {
        return masterNameNodeIpPort;
    }

    public String getMasterNameNodeName() {
        JsonObject jsonObject = nameNodeMap.get(masterNameNodeIpPort);
        if (jsonObject == null) return null;
        masterNameNodeName = jsonObject.get("nodeName").getAsString();
        return masterNameNodeName;
    }

    public Map<String, JsonObject> getDataNodeMap() {
        return dataNodeMap;
    }

    public Map<String, JsonObject> getLogNodeMap() {
        return logNodeMap;
    }
}
