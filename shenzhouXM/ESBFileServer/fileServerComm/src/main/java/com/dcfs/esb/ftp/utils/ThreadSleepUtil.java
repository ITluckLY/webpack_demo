package com.dcfs.esb.ftp.utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by mocg on 2016/12/28.
 */
public class ThreadSleepUtil {
    private ThreadSleepUtil() {
    }

    public static void sleepSecondIngoreEx(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (Exception ignored) {
            //nothing
        }
    }

    public static void sleepIngoreEx(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (Exception ignored) {
            //nothing
        }
    }
}
