package com.dcfs.esb.ftp.zk;

import com.dcfs.esb.ftp.spring.EfsProperties;
import com.dcfs.esb.ftp.spring.SpringContext;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by mocg on 2016/9/29.
 */
public class ZkClientTool {
    private static final Logger log = LoggerFactory.getLogger(ZkClientTool.class);
    private static ZkClientTool ourInstance = new ZkClientTool();
    private EfsProperties efsProperties = SpringContext.getInstance().getEfsProperties();
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
        String zookeeperUrls = efsProperties.getZookeeperUrls();
        log.info("createZkClient#zookeeperUrls:{}", zookeeperUrls);
        ZkClient zkClient = new ZkClient(zookeeperUrls, 6000, 5000, new ZkSerializer() {//NOSONAR
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {//NOSONAR
                //if (data == null) return null;//NOSONAR
                String dataStr;
                if (data instanceof String) dataStr = (String) data;
                else dataStr = String.valueOf(data);
                return dataStr.getBytes(Charset.forName("UTF-8"));
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {//NOSONAR
                if (bytes == null) return null;
                return new String(bytes, Charset.forName("UTF-8"));
            }
        });
        log.info("createZkClient succ");
        this.zkClient = zkClient;
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

    public void initFsZkTree(ZkClient zkClient) {//NOSONAR
        //node 子节点=ip:Cmdport
        createPersistent(ZkPathCons.DCFS_LIVE, true);
        createPersistent(ZkPathCons.DCFS_LIVE_NN, true);
        createPersistent(ZkPathCons.DCFS_LIVE_DN, true);
        createPersistent(ZkPathCons.DCFS_LIVE_LN, true);
        createPersistent(ZkPathCons.DCFS_LIVE_MN, true);
        //cfg sync 子节点=ip:Cmdport 子节点.data=json
        createPersistent(ZkPathCons.DCFS_CFGSYNC_DN, true);
        //cfg
        createPersistent("/dcfs/cfg", true);
        createPersistent("/dcfs/cfg/components.xml", true);
        createPersistent("/dcfs/cfg/crontab.xml", true);
        createPersistent("/dcfs/cfg/file.xml", true);
        createPersistent("/dcfs/cfg/file_clean.xml", true);
        createPersistent("/dcfs/cfg/file_rename.xml", true);
        createPersistent("/dcfs/cfg/flow.xml", true);
        createPersistent("/dcfs/cfg/nodes.xml", true);
        createPersistent("/dcfs/cfg/route.xml", true);
        createPersistent("/dcfs/cfg/rule.xml", true);
        createPersistent("/dcfs/cfg/services_info.xml", true);
        createPersistent("/dcfs/cfg/system.xml", true);
        createPersistent("/dcfs/cfg/user.xml", true);
        createPersistent("/dcfs/cfg/client_status.xml", true);

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
