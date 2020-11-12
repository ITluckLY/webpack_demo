package com.dcfs.esb.ftp.utils;

/**
 * Created by mocg on 2017/2/24.
 */
public class SafeArrayUtil {
    private SafeArrayUtil() {
    }

    public static <T> T get(T[] arr, int index, T def) {
        if (arr == null || arr.length < index) return def;
        return arr[index];
    }

    public static <T> T get(T[] arr, int index) {
        return get(arr, index, null);
    }
}
