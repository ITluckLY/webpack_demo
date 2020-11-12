package com.dcfs.esb.ftp.zk;

/**
 * Created by mocg on 2017/6/6.
 */
public class ZkServiceHelper {
    private static ZkServiceHelper ourInstance = new ZkServiceHelper();

    public static ZkServiceHelper getInstance() {
        return ourInstance;
    }

    private ZkServiceHelper() {
    }

    private static BaseZkService zkService;

    public static BaseZkService getZkService() {
        return zkService;
    }

    public static void setZkService(BaseZkService zkService) {
        ZkServiceHelper.zkService = zkService;
    }
}
