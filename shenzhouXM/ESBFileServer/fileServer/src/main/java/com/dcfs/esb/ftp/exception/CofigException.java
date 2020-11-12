package com.dcfs.esb.ftp.exception;

public class CofigException extends Exception {
    private static final long serialVersionUID = -7431658569239753893L;
    private String errCode;//NOSONAR

    public CofigException(String code, String msg) {
        super(msg);
        this.errCode = code;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
