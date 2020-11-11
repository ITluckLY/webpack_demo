package com.dc.smarteam.modules.monitor.putfiletomonitor.msg;

/**
 * 文件系统消息类型
 */
public class FileMsgType {

    public static final String PUT_AUTH = "101";
    public static final String GET_AUTH = "102";
    public static final String PUT = "201";
    public static final String GET = "202";
    public static final String DEL = "203";
    public static final String RNAM = "204";
    public static final String F5 = "205";
    public static final String F5_RUN = "206";
    public static final String F5_STOP = "207";
    public static final String SUCC = "000000";
    public static final String FAIL = "999999";
    public static final String FINISH = "finish";
    public static final String WHOLE_UPDATE = "301";//全量更新
    public static final String INCRE_UPDATE = "302";//增量更新
}
