package com.dcfs.esb.ftp.common.error;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;

/**
 * Created by mocg on 2017/7/15.
 */
public class FtpIOException extends FtpException {

    public FtpIOException(long nano, IOException e) {
        super(getErrCode(e), nano, e);
    }

    public FtpIOException(long nano, IOException e, String message) {
        super(getErrCode(e), nano, e, message);
    }

    public FtpIOException(Long nano, IOException e) {
        super(getErrCode(e), nano, e);
    }

    public FtpIOException(Long nano, IOException e, String message) {
        super(getErrCode(e), nano, e, message);
    }

    public FtpIOException(String flowNo, long nano, IOException e) {
        super(getErrCode(e), flowNo, nano, e);
    }

    public FtpIOException(String flowNo, long nano, IOException e, String message) {
        super(getErrCode(e), flowNo, nano, e, message);
    }

    public FtpIOException(String flowNo, Long nano, IOException e) {
        super(getErrCode(e), flowNo, nano, e);
    }

    public FtpIOException(String flowNo, Long nano, IOException e, String message) {
        super(getErrCode(e), flowNo, nano, e, message);
    }

    public FtpIOException(IOException e) {
        super(getErrCode(e), e);
    }

    private static String getErrCode(IOException e) {
        if (e instanceof ConnectException) return FtpErrCode.CONNECT_EXCEPTION;
        if (e instanceof SocketException) return FtpErrCode.SOCKET_ERROR;
        return FtpErrCode.IO_EXCEPTION;
    }
}
