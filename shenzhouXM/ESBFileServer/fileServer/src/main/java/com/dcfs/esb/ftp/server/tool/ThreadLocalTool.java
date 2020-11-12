package com.dcfs.esb.ftp.server.tool;

import java.util.List;

/**
 * Created by mocg on 2016/6/30.
 */
public class ThreadLocalTool {
    private ThreadLocalTool() {
    }

    private static ThreadLocal<String> threadLocalOfBusDeal = new ThreadLocal<>();
    private static ThreadLocal<String> threadLocalOfTcpServer = new ThreadLocal<>();
    private static ThreadLocal<List<String>> threadLocalOfDebug = new ThreadLocal<>();

    public static ThreadLocal<String> getThreadLocalOfBusDeal() {
        return threadLocalOfBusDeal;
    }

    public static ThreadLocal<String> getThreadLocalOfTcpServer() {
        return threadLocalOfTcpServer;
    }

    public static ThreadLocal<List<String>> getThreadLocalOfDebug() {
        return threadLocalOfDebug;
    }
}
