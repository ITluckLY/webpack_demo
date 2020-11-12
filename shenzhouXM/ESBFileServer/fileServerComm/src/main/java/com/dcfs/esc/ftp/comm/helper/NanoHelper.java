package com.dcfs.esc.ftp.comm.helper;

/**
 * Created by mocg on 2017/7/10.
 */
public class NanoHelper {
    private NanoHelper() {
    }

    private static int flag = 0;

    public static long nanos() {
        //尾部4个位给flag
        final int mult = 10000;
        return System.nanoTime() * mult + flag;
    }

    public static int getFlag() {
        return flag;
    }

    public static void setFlag(int flag) {
        NanoHelper.flag = flag;
    }
}
