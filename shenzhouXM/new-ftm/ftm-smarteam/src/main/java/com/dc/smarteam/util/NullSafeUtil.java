package com.dc.smarteam.util;

import java.util.Collections;
import java.util.List;

/**
 * Created by mocg on 2017/5/8.
 */
public class NullSafeUtil {

    public static <T> List<T> null2Empty(List<T> list) {
        if (list == null) return Collections.emptyList();
        return list;
    }
}
