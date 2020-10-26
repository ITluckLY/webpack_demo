/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.helper;

import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import com.google.gson.JsonObject;

import java.util.Map;

public class FtNodeMonitorHelper {

    public static void updateStateByZK(FtNodeMonitor ftNodeMonitor) {
        if (ftNodeMonitor == null) return;
        String state = "0";
        String ipPort = ftNodeMonitor.getIp() + ":" + ftNodeMonitor.getPort();
        String system = ftNodeMonitor.getSystem();
        Map<String, JsonObject> map = null;
        // 不考虑namenode
        if (system != null) map = ZkService.getInstance().getDataNodeMap();
        else map = ZkService.getInstance().getLogNodeMap();
        if (map != null && map.containsKey(ipPort)) state = "1";
        ftNodeMonitor.setState(state);
    }


}
