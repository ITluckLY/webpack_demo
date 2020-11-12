package com.dcfs.esc.ftp.svr.comm.cons;

import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;

/**
 * 字段名带DCFS的表示是内部逻辑标识，其值改变需慎重(需带dcfs)
 * ref com.dcfs.esc.ftp.comm.constant.CommGlobalCons
 * <p>
 * Created by mocg on 2016/8/29.
 */
public class SvrGlobalCons {
    private SvrGlobalCons() {
    }

    public static final String DCFS_CFG_FILE_EXT = CommGlobalCons.DCFS_CFG_FILE_EXT;// ".dcfscfg"
    public static final String DCFS_TMP_FILE_EXT = CommGlobalCons.DCFS_TMP_FILE_EXT;//".dcfstmp"
    public static final String DCFS_DEL_FILE_EXT = CommGlobalCons.DCFS_DEL_FILE_EXT;//".dcfsdel"
    public static final String DCFS_LOCK_FILE_EXT = CommGlobalCons.DCFS_LOCK_FILE_EXT;// ".dcfslock"
    public static final String DCFS_FILE_UPLOADING_FILE_EXT = CommGlobalCons.DCFS_FILE_UPLOADING_FILE_EXT;// ".dcfsfileuploading"
    public static final String DCFS_FILE_UPLOADED_FILE_EXT = CommGlobalCons.DCFS_FILE_UPLOADED_FILE_EXT;//".dcfsfileuploaded"
    //时间戳基数
    public static final long TWEPOCH = CommGlobalCons.TWEPOCH;//2016-11-23

    public static final String TAILER_MANAGER_KEY = "obj.tailerManager";

}
