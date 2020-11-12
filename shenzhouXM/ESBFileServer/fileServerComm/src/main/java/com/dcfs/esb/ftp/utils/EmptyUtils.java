package com.dcfs.esb.ftp.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 判断是否null size==0 length==0
 * Created by cgmo on 2015/3/10.
 */
public class EmptyUtils {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(String[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isNotEmpty(String[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 支持Set、List、Map、String、数组，其他的只判断是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;

        if (obj instanceof String) {
            return isEmpty((String) obj);
        } else if (obj instanceof CharSequence) {
            return isEmpty((CharSequence) obj);
        } else if (obj instanceof Map) {
            return isEmpty((Map) obj);
        } else if (obj instanceof Collection) {//Set、List
            return isEmpty((Collection) obj);
        } else if (obj.getClass().isArray()) {//数组
            return Array.getLength(obj) == 0;
        }

        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    //@SafeVarargs
    public static <T> T firstNotNull(T... ts) {
        for (T t : ts) {
            if (t != null) return t;
        }
        return null;
    }

    protected EmptyUtils() {
    }
}
