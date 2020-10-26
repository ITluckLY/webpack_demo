package com.dc.smarteam.helper;

import com.dc.smarteam.common.utils.StringUtils;

/**
 * Created by mocg on 2017/3/15.
 */
public class IDHelper {

    public static boolean isEmpty(String id) {
        return StringUtils.isBlank(id);
    }

    public static boolean isNotEmpty(String id) {
        return !isEmpty(id);
    }

    public static boolean isEmpty(Long id) {
        return id == null || id <= 0;
    }

    public static boolean isNotEmpty(Long id) {
        return !isEmpty(id);
    }
}
