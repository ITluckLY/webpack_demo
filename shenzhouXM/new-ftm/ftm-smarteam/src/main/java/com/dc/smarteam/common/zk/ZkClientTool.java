package com.dc.smarteam.common.zk;

import com.dc.smarteam.common.config.Global;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * Created by mocg on 2016/9/29.
 */
@Component
public class ZkClientTool {
    private static final Logger log = LoggerFactory.getLogger(ZkClientTool.class);
    private static ZkClientTool ourInstance = new ZkClientTool();
    private ZkClient zkClient;

    private ZkClientTool() {
    }

    public static ZkClientTool getInstance() {
        return ourInstance;
    }

    public ZkClient getZkClient() {
        if (zkClient != null) return zkClient;
        return createZkClient();
    }

    public ZkClient createZkClient() {
        log.info("create zk...");
        String zookeeperUrls = Global.getZookeeperUrls();
        log.info("createZkClient#zookeeperUrls:{}", zookeeperUrls);
        zkClient = new ZkClient(zookeeperUrls, 6000, 5000, new ZkSerializer() {
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                String dataStr;
                if (data instanceof String) dataStr = (String) data;
                else dataStr = String.valueOf(data);
                return dataStr.getBytes(Charset.forName("UTF-8"));
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                if (bytes == null) return null;
                return new String(bytes, Charset.forName("UTF-8"));
            }
        });
        log.info("createZkClient succ");
        return zkClient;
    }

    public void close() {
        log.info("close zk...");
        if (zkClient != null) {
            try {
                zkClient.close();
            } catch (ZkInterruptedException e) {
                log.error("close zk err", e);
            }
        }
    }

    public void initFsZkTree() {
        //node 子节点=ip:Cmdport
        createPersistent(ZkPathCons.DCFS_LIVE, true);
        createPersistent(ZkPathCons.DCFS_LIVE_NN, true);
        createPersistent(ZkPathCons.DCFS_LIVE_DN, true);
        createPersistent(ZkPathCons.DCFS_LIVE_LN, true);
        createPersistent(ZkPathCons.DCFS_LIVE_MN, true);
        //cfg sync 子节点=ip:Cmdport 子节点.data=json
        createPersistent(ZkPathCons.DCFS_CFGSYNC_DN, true);
        createPersistent(ZkPathCons.DCFS_CFGSYNC_DN_COMM, true);
        createPersistent(ZkPathCons.DCFS_CFGSYNC_DN_SYS, true);
        createPersistent(ZkPathCons.DCFS_CFGSYNC_DN_NODE, true);
        //masterNode data=ip:Cmdport
        createPersistent(ZkPathCons.DCFS_MASTER_NN, true);
        createPersistent(ZkPathCons.DCFS_MASTER_DN, true);
        createPersistent(ZkPathCons.DCFS_MASTER_LN, true);
        //version data=具体版本号
        createPersistent(ZkPathCons.DCFS_VERSION_API, true);
        createPersistent(ZkPathCons.DCFS_VERSION_NODELIST, true);
    }

    private void createPersistent(String path, boolean createParents) {
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path, createParents);
        }
    }
}
