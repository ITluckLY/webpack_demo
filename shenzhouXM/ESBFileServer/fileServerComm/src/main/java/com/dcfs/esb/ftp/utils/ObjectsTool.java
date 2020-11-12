package com.dcfs.esb.ftp.utils;

/**
 * Created by mocg on 2016/12/8.
 */
public class ObjectsTool {
    private ObjectsTool() {
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
