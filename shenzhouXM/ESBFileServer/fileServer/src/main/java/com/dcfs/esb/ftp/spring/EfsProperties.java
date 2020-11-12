package com.dcfs.esb.ftp.spring;

import com.dcfs.esb.ftp.utils.BooleanTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by mocg on 2016/8/3.
 */
@Component
public class EfsProperties {
    @Value("${ifKfkGroupIdAppendHost:false}")
    private String ifKfkGroupIdAppendHost;//类型要为String 可以不配置的 为false
    @Value("${ifCfgSyncByStart:true}")
    private String ifCfgSyncByStart;
    @Value("${zookeeper.urls:}")
    private String zookeeperUrls;
    @Value("${kfk.bootstrap.servers:}")
    private String kfkBootstrapServers;

    public boolean getIfKfkGroupIdAppendHost() {
        return BooleanTool.toBoolean(ifKfkGroupIdAppendHost);
    }

    public boolean getIfCfgSyncByStart() {
        return BooleanTool.toBoolean(ifCfgSyncByStart);
    }

    public String getZookeeperUrls() {
        return zookeeperUrls;
    }

    public String getKfkBootstrapServers() {
        return kfkBootstrapServers;
    }
}
