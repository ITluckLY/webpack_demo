package com.dc.smarteam.tool;

import java.util.List;

/**
 * Created by Administrator on 2019/12/3.
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