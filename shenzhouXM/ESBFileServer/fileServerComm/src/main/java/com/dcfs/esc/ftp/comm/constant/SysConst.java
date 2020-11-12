package com.dcfs.esc.ftp.comm.constant;

import java.nio.charset.Charset;

/**
 * Created by mocg on 2017/6/3.
 */
public class SysConst {
    private SysConst() {
    }

    public static final Charset DEF_CHARSET = Charset.forName("UTF-8");

    public static final int DEF_PIECE_NUM = 511 * UnitCons.ONE_KB;//byte
    public static final int MIN_PIECE_NUM = UnitCons.ONE_KB;//byte
    public static final int MAX_PIECE_NUM = UnitCons.ONE_MB;//byte
    public static final int DEF_CONNECT_TIME_OUT_INTERVAL = 0;//间隔0秒
    public static final int DEF_CONNECT_TIME_OUT_RETRY_COUNT = 1;
    public static final int DEF_SO_TIME_OUT_INTERVAL = 20 * UnitCons.ONE_SECONDS_MILLIS;//间隔20秒
    public static final int DEF_SO_TIME_OUT_RETRY_COUNT = 3;

}
