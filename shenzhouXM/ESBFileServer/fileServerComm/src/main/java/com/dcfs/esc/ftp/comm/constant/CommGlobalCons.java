package com.dcfs.esc.ftp.comm.constant;

/**
 * 字段名带DCFS的表示是内部逻辑标识，其值改变需慎重(需带dcfs)
 * Created by mocg on 2016/8/29.
 */
public class CommGlobalCons {
    protected CommGlobalCons() {
    }

    public static final String TRANSMIT_ENCODING = "UTF-8";
    public static final String SUCC_CODE = "0000";
    public static final String DCFS_CFG_FILE_EXT = ".dcfscfg";
    public static final String DCFS_TMP_FILE_EXT = ".dcfstmp";
    public static final String DCFS_DEL_FILE_EXT = ".dcfsdel";
    public static final String DCFS_LOCK_FILE_EXT = ".dcfslock";
    public static final String DCFS_FILE_UPLOADING_FILE_EXT = ".dcfsfileuploading";
    public static final String DCFS_FILE_UPLOADED_FILE_EXT = ".dcfsfileuploaded";
    //时间戳基数
    public static final long TWEPOCH = 1479830400000L;//2016-11-23

    public static final String TAIL_FILE_PATH_KEY = "str.tailFilePath";
    public static final String SEQ_KEY = "str.seq";
    //密码认证通过才有值
    public static final String CURR_USER_PWDMD5_KEY = "str.currUserPwdMd5";//NOSONAR
    //平台的路径分隔符
    public static final String SELF_FILE_SEPARATOR = "/";
    public static final char SELF_FILE_SEPARATOR_CHAR = '/';

    /*不确定*/
    public static final int RESULT_STATE_NOTSURE = 0;
    /*成功*/
    public static final int RESULT_STATE_SUCCESS = 1;
    /*失败*/
    public static final int RESULT_STATE_FAIL = -1;
    /*出错有异常*/
    public static final int RESULT_STATE_EXCEPTION = -2;

}
