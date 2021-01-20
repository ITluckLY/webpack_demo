package com.dcfs.esc.ftp.comm.scrt.security.aes.exception;

public class SDKException extends Exception {
	private static final long serialVersionUID = 1L;
	private ExceptionEnums exceptionEnums;

	public ExceptionEnums getExceptionEnums() {
		return exceptionEnums;
	}

	public SDKException() {
        super();
    }

    public SDKException(String message, Throwable cause) {
        super(message, cause);
    }

    public SDKException(String message) {
        super(message);
    }

    public SDKException(Throwable cause) {
        super(cause);
    }

    public SDKException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
    }

	public SDKException(ExceptionEnums exceptionEnums) {
		super();
		this.exceptionEnums = exceptionEnums;
	}

	public SDKException(Throwable t, ExceptionEnums exceptionEnums) {
		super(t);
		this.exceptionEnums = exceptionEnums;
	}

	public String toString() {
		return super.toString() + "<" + getExceptionEnums().getCode() + ":" + getExceptionEnums().getMessage() + ">";
	}

}
