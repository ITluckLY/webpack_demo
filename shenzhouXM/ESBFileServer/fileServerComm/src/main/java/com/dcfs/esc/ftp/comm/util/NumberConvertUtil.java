package com.dcfs.esc.ftp.comm.util;

/**
 * Created by mocg on 2017/8/3.
 */
public class NumberConvertUtil {

    private NumberConvertUtil() {
    }

    public static int longToInt(long val) {
        if (val > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (val < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) val;
    }

}
