package com.dcfs.esc.ftp.svr.comm.cons;

/**
 * Created by mocg on 2017/8/18.
 */
public class ServerCfgCons {
    private ServerCfgCons() {
    }

    /*交易码的最小优先级别 从1开始*/
    public static final int TRAN_CODE_MIN_PRIORITY = 1;
    /*交易码的最大优先级别 默认5*/
    public static final int TRAN_CODE_MAX_PRIORITY = 5;
}
