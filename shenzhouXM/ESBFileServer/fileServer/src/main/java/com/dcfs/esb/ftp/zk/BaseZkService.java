package com.dcfs.esb.ftp.zk;

import com.dcfs.esb.ftp.utils.GsonUtil;
import com.google.gson.JsonObject;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

import java.util.Map;

/**
 * Created by mocg on 2016/11/2.
 */
public abstract class BaseZkService {
    protected ZkClient zkClient;

    public void init() {
        zkClient = ZkClientTool.getInstance().getZkClient();
    }

    protected JsonObject readDataNN(String  ipPort) {
        String data = zkClient.readData(ZkPathCons.PRE_DCFS_LIVE_NN + ipPort);
        return GsonUtil.toJsonObj(data);
    }

    protected JsonObject readDataDN(String ipPort) {
        String data = zkClient.readData(ZkPathCons.PRE_DCFS_LIVE_DN + ipPort);
        return GsonUtil.toJsonObj(data);
    }

    protected JsonObject readDataLN(String ipPort) {
        String data = zkClient.readData(ZkPathCons.PRE_DCFS_LIVE_LN + ipPort);
        return GsonUtil.toJsonObj(data);
    }

    protected JsonObject readDataNN(String ipPort, Stat stat) {
        String data = zkClient.readData(ZkPathCons.PRE_DCFS_LIVE_NN + ipPort, stat);
        return GsonUtil.toJsonObj(data);
    }

    protected JsonObject readDataDN(String ipPort, Stat stat) {
        String data = zkClient.readData(ZkPathCons.PRE_DCFS_LIVE_DN + ipPort, stat);
        return GsonUtil.toJsonObj(data);
    }

    protected JsonObject readDataLN(String ipPort, Stat stat) {
        String data = zkClient.readData(ZkPathCons.PRE_DCFS_LIVE_LN + ipPort, stat);
        return GsonUtil.toJsonObj(data);
    }

    public abstract Map<String, JsonObject> getDataNodeMap();
}
