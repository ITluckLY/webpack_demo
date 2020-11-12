package com.dcfs.esb.ftp.server.invoke.nodesync;

import com.dcfs.esb.ftp.spring.SpringContext;

/**
 * Created by mocg on 2016/10/10.
 */
public class CfgFileReceiverFace {
    private static CfgFileReceiverFace ourInstance = new CfgFileReceiverFace();
    private AbstractCfgFileReceiver cfgFileReceiver;

    private CfgFileReceiverFace() {
    }

    public static CfgFileReceiverFace getInstance() {
        return ourInstance;
    }

    public AbstractCfgFileReceiver getCfgFileReceiver() {
        if (cfgFileReceiver == null) {
            SpringContext.getInstance().init();
            cfgFileReceiver = SpringContext.getInstance().getBeanIgnoreExist("cfgFileReceiver");
        }
        return cfgFileReceiver;
    }
}
