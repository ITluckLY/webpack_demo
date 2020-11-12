package com.dcfs.esb.ftp.report;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by mocg on 2016/12/8.
 */
public class CfgSyncReport {
    private Map<String, String> nodeMsgMap = new Hashtable<String, String>();//NOSONAR

    public void put(String nodeName, String syncResult) {
        nodeMsgMap.put(nodeName, syncResult);
    }

    public Map<String, String> getNodeMsgMap() {
        return nodeMsgMap;
    }

    public void setNodeMsgMap(Map<String, String> nodeMsgMap) {
        this.nodeMsgMap = nodeMsgMap;
    }
}
