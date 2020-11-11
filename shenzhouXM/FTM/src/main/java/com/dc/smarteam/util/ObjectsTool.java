package com.dc.smarteam.util;

/**
 * Created by mocg on 2016/12/8.
 */
public class ObjectsTool {
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
