package com.dcfs.esb.ftp.utils;

/**
 * Created by mocg on 2016/12/7.
 */
public class NullDefTool {
    private NullDefTool() {
    }

    public static int defNull(Integer integer, int def) {
        if (integer == null) return def;
        return integer;
    }

    public static long defNull(Long l, long def) {
        if (l == null) return def;
        return l;
    }

    public static String defNull(String str, String def) {
        if (str == null) return def;
        return str;
    }

    public static <T> T defNull(T tObj, T def) {
        if (tObj == null) return def;
        return tObj;
    }
}
