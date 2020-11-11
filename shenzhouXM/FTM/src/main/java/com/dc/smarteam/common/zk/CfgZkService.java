package com.dc.smarteam.common.zk;

import com.dc.smarteam.cons.GlobalCons;
import com.dc.smarteam.util.ShortUrlUtil;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by mocg on 2017/5/8.
 */
@Service
public class CfgZkService {
    private Logger log = LoggerFactory.getLogger(CfgZkService.class);
    private ZkClient zkClient = ZkClientTool.getInstance().getZkClient();

    /**
     *   zk 写入
     *
     * @param sysname
     * @param fileName
     * @param cfgContent
     */
    public void write(String sysname, String fileName, String cfgContent) {
        String path = ZkPathCons.PRE_DCFS_CFGSYNC_DN_SYS + sysname.toLowerCase() + '/' + fileName.toLowerCase(); // 拼接文件 XML 路径
        //createPersistent(path, true)
        String path0 = path + "/0";
        createPersistent(path0, true);
        zkClient.writeData(path0, cfgContent);
        zkClient.writeData(path, System.currentTimeMillis());

        if (fileName.equalsIgnoreCase("nodes.xml")) {
            updateNodelistVersion();
        }
    }

    private void createPersistent(String path, boolean createParents) {
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path, createParents);
        }
    }

    //nodes.xml内容改变则更新nodelist版本号
    private boolean updateNodelistVersion() {
        String version = String.valueOf(ShortUrlUtil.short36(System.currentTimeMillis() - GlobalCons.twepoch));
        log.debug("zk中的节点列表版本号{}", version);
        try {
            zkClient.writeData(ZkPathCons.DCFS_VERSION_NODELIST, version);
        } catch (Exception e) {
            log.error("更新zk中的节点列表版本号失败", e);
            return false;
        }
        return true;
    }
}
