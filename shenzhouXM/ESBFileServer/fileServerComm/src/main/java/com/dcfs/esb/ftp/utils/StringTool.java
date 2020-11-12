package com.dcfs.esb.ftp.utils;

/**
 * Created by mocg on 2016/8/24.
 */
public class StringTool {
    protected StringTool() {
    }

    public static int toInt(String str) {
        return toInteger(str, 0);
    }

    public static int toInt(String str, int def) {
        return toInteger(str, def);
    }

    public static Integer toInteger(String str) {
        return toInteger(str, null);
    }

    public static Integer toInteger(String str, Integer def) {
        if (str == null || str.length() == 0) return def;
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static long tolong(String str) {
        return toLong(str, 0L);
    }

    public static long tolong(String str, long def) {
        return toLong(str, def);
    }

    public static Long toLong(String str) {//NOSONAR
        return toLong(str, null);
    }

    public static Long toLong(String str, Long def) {//NOSONAR
        if (str == null || str.length() == 0) return def;
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static int parseInt(String str, int def) {
        return toInt(str, def);
    }

    public static long parseLong(String str, long def) {
        return tolong(str, def);
    }

    public static String toString(Integer i) {
        if (i == null) return null;
        return i.toString();
    }

    public static String toString(Long l) {
        if (l == null) return null;
        return l.toString();
    }

    public static String toString(Boolean b) {
        if (b == null) return null;
        return b.toString();
    }

    public static String toString(Object obj) {
        return toString(obj, null);
    }

    public static String toString(Object obj, String def) {
        if (obj == null) return def;
        return obj.toString();
    }

    public static boolean isNotTrimEmpty(final String str) {
        return str != null && str.trim().length() > 0;
    }
}
