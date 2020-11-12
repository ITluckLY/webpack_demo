package com.dcfs.esc.ftp.comm.exception;

/**
 * Created by mocg on 2017/7/29.
 */
public class CorruptedChunkException extends RuntimeException {
    public CorruptedChunkException() {
    }

    public CorruptedChunkException(String message) {
        super(message);
    }

    public CorruptedChunkException(String message, Throwable cause) {
        super(message, cause);
    }
}
