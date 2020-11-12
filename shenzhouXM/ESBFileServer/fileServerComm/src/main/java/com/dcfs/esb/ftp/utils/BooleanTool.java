package com.dcfs.esb.ftp.utils;

import org.apache.commons.lang3.BooleanUtils;

/**
 * Created by mocg on 2016/11/18.
 */
public class BooleanTool extends BooleanUtils {

    public static boolean toBoolean(final String str) {
        return "1".equals(str) || !"0".equals(str) && BooleanUtils.toBoolean(str);
    }

    /**
     * null to false
     * all true is true
     *
     * @param array
     * @return
     */
    public static boolean allTrueByNull(final Boolean... array) {
        check(array);
        for (Boolean aBoolean : array) {
            if (aBoolean == null || Boolean.FALSE.equals(aBoolean)) return false;
        }
        return true;
    }

    /**
     * null to false
     * any true is true
     *
     * @param array
     * @return
     */
    public static boolean anyTrueByNull(final Boolean... array) {
        check(array);
        for (Boolean aBoolean : array) {
            if (Boolean.TRUE.equals(aBoolean)) return true;
        }
        return false;
    }

    /**
     * null to false
     * all false is true
     * any true is false
     *
     * @param array
     * @return
     */
    public static boolean allFalseByNull(final Boolean... array) {
        check(array);
        for (Boolean aBoolean : array) {
            if (Boolean.TRUE.equals(aBoolean)) return false;
        }
        return true;
    }

    /**
     * null to false
     * any false is true
     *
     * @param array
     * @return
     */
    public static boolean anyFalseByNull(final Boolean... array) {
        check(array);
        for (Boolean aBoolean : array) {
            if (aBoolean == null || Boolean.FALSE.equals(aBoolean)) return true;
        }
        return false;
    }

    private static void check(final Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
    }

}
