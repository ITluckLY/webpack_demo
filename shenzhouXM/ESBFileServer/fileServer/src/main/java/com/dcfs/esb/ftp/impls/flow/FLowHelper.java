package com.dcfs.esb.ftp.impls.flow;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.interfases.context.CachedContext;

public class FLowHelper {
    protected FLowHelper() {
    }

    public static boolean hasError(CachedContext context) {
        return context.getCxtBean().getErrorCode() != null;
    }

    public static void setError(CachedContext context, String code, String msg) {
        context.getCxtBean().setErrorCode(code);
        context.getCxtBean().setErrorMsg(msg);
    }

    public static void setError(CachedContext context, String code) {
        setError(context, code, FtpErrCode.getCodeMsg(code));
    }

    public static String getErrorCode(CachedContext context) {
        return context.getCxtBean().getErrorCode();
    }

    public static String getErrorMsg(CachedContext context) {
        return context.getCxtBean().getErrorMsg();
    }

}
