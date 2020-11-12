package com.dcfs.esb.ftp.helper;

/**
 * Created by mocg on 2017/8/25.
 */
public class NodeListHelper {

    private NodeListHelper() {
    }

    /*默认的节点列表字符串 便于测试 生产环境一般为null*/
    private static String defNodeListStrForTest = null;

    public static String getDefNodeListStrForTest() {
        return defNodeListStrForTest;
    }

    public static void setDefNodeListStrForTest(String defNodeListStrForTest) {
        NodeListHelper.defNodeListStrForTest = defNodeListStrForTest;
    }
}
