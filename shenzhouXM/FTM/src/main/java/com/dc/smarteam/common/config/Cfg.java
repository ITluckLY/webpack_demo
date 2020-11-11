package com.dc.smarteam.common.config;

import com.dc.smarteam.common.zk.ZkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by huangzbb on 2016/10/21.
 */
@Component
public class Cfg {
    @Value("${nameNodeIP}")
    private String nameNodeIP;
    @Value("${nameNodePort}")
    private String nameNodePort;
    @Value("${nameNodeName}")
    private String nameNodeName;

    private String masterNameNodeIP;
    private String masterNameNodePort;
    private String masterNameNodeName;
    private String masterNameNodeIpPort;

    public void loadZK() {
        String ipPort = ZkService.getInstance().getMasterNameNodeIpPort();
        if (ipPort == null || ipPort.equals(masterNameNodeIpPort)) return;
        String[] split = ipPort.split(":");
        masterNameNodeIP = split[0];
        masterNameNodePort = split[1];
        masterNameNodeName = ZkService.getInstance().getMasterNameNodeName();
        masterNameNodeIpPort = ipPort;
    }

    // /dcit/master/nn变化，需及时更新
    public String getNameNodeIP() {
        if (nameNodeIP == null || nameNodeIP.isEmpty() || "${nameNodeIP}".equals(nameNodeIP)) {
            loadZK();
            return masterNameNodeIP;
        }
        return nameNodeIP;
    }

    public String getNameNodePort() {
        if (nameNodePort == null || nameNodePort.isEmpty() || "${nameNodePort}".equals(nameNodePort)) {
            loadZK();
            return masterNameNodePort;
        }
        return nameNodePort;
    }

    public String getNameNodeName() {
        if (nameNodeName == null || nameNodeName.isEmpty() || "${nameNodeName}".equals(nameNodeName)) {
            loadZK();
            return masterNameNodeName;
        }
        return nameNodeName;
    }

    public String getNameNodeType() {
        return "namenode";
    }
}
