package com.dcfs.esb.ftp.utils;

import java.util.Date;

/**
 * Created by mocg on 2016/7/28.
 */
public class DateUtil {
    public static boolean compareToNowLess(Date date, long time) {
        if (date == null) return false;
        Date now = new Date();
        return now.getTime() - date.getTime() < time;
    }

    public static boolean compareToNowLess(long timestamp, long time) {
        return System.currentTimeMillis() - timestamp < time;
    }

    private DateUtil() {
    }
}
