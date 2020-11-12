package com.dcfs.esb.ftp.common.helper;

/**
 * Created by mocg on 2017/2/20.
 */
public class ThreadLocalHelper {
    private ThreadLocalHelper() {
    }

    private static ThreadLocal<Long> nanoThreadLocal = new ThreadLocal<Long>();

    public static ThreadLocal<Long> getNanoThreadLocal() {
        return nanoThreadLocal;
    }
}
