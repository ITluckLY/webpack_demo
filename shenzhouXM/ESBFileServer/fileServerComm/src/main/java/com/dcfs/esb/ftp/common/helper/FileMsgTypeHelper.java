package com.dcfs.esb.ftp.common.helper;

import com.dcfs.esb.ftp.common.msg.FileMsgType;

/**
 * Created by mocg on 2016/12/28.
 */
public class FileMsgTypeHelper {
    protected FileMsgTypeHelper() {
    }

    public static boolean isPut(String fileMsgFlag) {
        return isPutFileAuth(fileMsgFlag) || isPutFile(fileMsgFlag);
    }

    public static boolean isGet(String fileMsgFlag) {
        return isGetFileAuth(fileMsgFlag) || isGetFile(fileMsgFlag);
    }

    public static boolean isPutFileAuth(String fileMsgFlag) {
        return FileMsgType.PUT_AUTH.equals(fileMsgFlag) || FileMsgType.PUT_SMALL_FILE.equals(fileMsgFlag)
                || FileMsgType.PUT_ADDR.equals(fileMsgFlag);
    }

    public static boolean isGetFileAuth(String fileMsgFlag) {
        return FileMsgType.GET_AUTH.equals(fileMsgFlag) || FileMsgType.GET_ADDR.equals(fileMsgFlag);
    }

    public static boolean isPutFile(String fileMsgFlag) {
        return FileMsgType.PUT.equals(fileMsgFlag) || FileMsgType.PUT_SMALL_FILE.equals(fileMsgFlag);
    }

    public static boolean isGetFile(String fileMsgFlag) {
        return FileMsgType.GET.equals(fileMsgFlag);
    }

    public static boolean isNeedShowUidPwd(String fileMsgFlag) {
        return FileMsgType.PUT_AUTH.equals(fileMsgFlag) || FileMsgType.GET_AUTH.equals(fileMsgFlag)
                || FileMsgType.PUT_ADDR.equals(fileMsgFlag) || FileMsgType.GET_ADDR.equals(fileMsgFlag)
                || FileMsgType.GET_NODESINFO.equals(fileMsgFlag) || FileMsgType.PUT_SMALL_FILE.equals(fileMsgFlag);
    }
}
