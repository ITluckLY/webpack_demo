package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.server.config.FtpConfig;

public class NodeWorker {
    private static final Object lock = new Object();
    private static NodeWorker instance;

    private NodeWorker() {
    }

    public static NodeWorker getInstance() {
        if (instance != null) return instance;
        synchronized (lock) {
            if (instance == null) {
                instance = new NodeWorker();
            }
        }
        return instance;
    }

    public String getSysName() {
        //优先取cfg.xml中的
        //String currNodeSysName = NodesWorker.getInstance().getCurrNodeSysName();//NOSONAR
        //if (currNodeSysName != null) return currNodeSysName;//NOSONAR
        return FtpConfig.getInstance().getSystemName();
    }

}
