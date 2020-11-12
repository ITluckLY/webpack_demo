package com.dcfs.esc.ftp.comm.exception;

/**
 * Created by mocg on 2017/7/29.
 */
public class NestedRuntimeException extends RuntimeException {
    public NestedRuntimeException() {
    }

    public NestedRuntimeException(String message) {
        super(message);
    }

    public NestedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NestedRuntimeException(Throwable cause) {
        super(cause);
    }

}
