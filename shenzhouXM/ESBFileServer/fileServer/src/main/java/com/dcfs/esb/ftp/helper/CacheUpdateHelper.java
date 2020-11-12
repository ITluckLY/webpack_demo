package com.dcfs.esb.ftp.helper;

/**
 * Created by mocg on 2016/12/27.
 */
public class CacheUpdateHelper {
    private static boolean latestDataNodeList = false;//缓存中的数据节点列表是否是最新的
    private static boolean latestNodeXml = true;

    public static boolean isLatestDataNodeList() {
        return latestDataNodeList;
    }

    public static void setLatestDataNodeList(boolean latestDataNodeList) {
        CacheUpdateHelper.latestDataNodeList = latestDataNodeList;
    }

    public static boolean isLatestNodeXml() {
        return latestNodeXml;
    }

    public static void setLatestNodeXml(boolean latestNodeXml) {
        CacheUpdateHelper.latestNodeXml = latestNodeXml;
    }

    private CacheUpdateHelper() {
    }
}
