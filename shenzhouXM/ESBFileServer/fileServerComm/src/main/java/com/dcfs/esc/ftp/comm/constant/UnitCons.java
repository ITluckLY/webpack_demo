package com.dcfs.esc.ftp.comm.constant;

/**
 * Created by mocg on 2017/8/17.
 */
public class UnitCons {
    protected UnitCons() {
    }

    /**
     * The number of bytes in a kilobyte.
     */
    public static final int ONE_KB = 1024;
    public static final long ONE_KB_LONG = ONE_KB;

    /**
     * The number of bytes in a megabyte.
     */
    public static final int ONE_MB = ONE_KB * ONE_KB;
    public static final long ONE_MB_LONG = ONE_MB;

    public static final int ONE_GB = ONE_MB * ONE_KB;
    public static final long ONE_GB_LONG = ONE_GB;

    public static final int ONE_SECONDS_MILLIS = 1000;

}
