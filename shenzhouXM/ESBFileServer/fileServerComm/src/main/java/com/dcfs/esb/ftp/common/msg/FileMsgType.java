package com.dcfs.esb.ftp.common.msg;

/**
 * 文件系统消息类型
 */
public class FileMsgType {
    private FileMsgType() {
    }

    public static final String PUT_ADDR = "001";//获取上传可用节点地址
    public static final String GET_ADDR = "002";//获取下载可用节点地址
    public static final String PUT_AUTH = "101";
    public static final String GET_AUTH = "102";
    public static final String PUT = "201";
    public static final String GET = "202";
    public static final String DEL = "203";
    public static final String RNAM = "204";
    public static final String PUT_SMALL_FILE = "301";//上传小文件
    public static final String GET_NODESINFO = "302";//获取dataNode节点列表
}
