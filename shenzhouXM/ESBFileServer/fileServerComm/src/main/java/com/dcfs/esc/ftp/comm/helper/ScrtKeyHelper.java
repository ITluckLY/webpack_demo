package com.dcfs.esc.ftp.comm.helper;

import com.dcfs.esb.ftp.common.scrt.Des;
import com.dcfs.esb.ftp.utils.MD5Util;

/**
 * Created by mocg on 2017/6/15.
 */
public class ScrtKeyHelper {
    protected ScrtKeyHelper() {
    }

    public static byte[] convert(String pwdmd5, String seq) {
        if (seq == null) return pwdmd5.getBytes(Des.charset);
        final int keyLen = 24;
        return MD5Util.md5(seq + pwdmd5).substring(0, keyLen).getBytes(Des.charset);
    }
}
