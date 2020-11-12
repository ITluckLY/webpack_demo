package com.dcfs.esb.ftp.common.helper;

import com.dcfs.esb.ftp.utils.MD5Util;

/**
 * Created by mocg on 2017/6/15.
 */
public class PasswordHelper {
    private PasswordHelper() {
    }

    public static String convert(String pwdmd5, String seq) {
        if (seq == null) return pwdmd5;
        return MD5Util.md5(pwdmd5 + seq);
    }
}
