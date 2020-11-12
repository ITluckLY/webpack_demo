package com.dcfs.esb.ftp.datanode.config;

import com.dcfs.esb.ftp.server.config.Cfg;

/**
 * Created by mocg on 2016/8/30.
 */
public class DataCfg extends Cfg {
    private DataCfg() {
    }

    private static boolean useNameServer = false;

    public static boolean isUseNameServer() {
        return useNameServer;
    }

    public static void setUseNameServer(boolean useNameServer) {
        DataCfg.useNameServer = useNameServer;
    }
}
