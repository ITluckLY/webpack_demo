package com.dcfs.esb.ftp.common.error;

/**
 * 文件服务器异常类
 */
public class FtpException extends Exception {

    private static final long serialVersionUID = 1L;
    private String code;
    private String file;
    private long nano;
    private String flowNo;

    /**
     * 异常类的构造方法
     *
     * @param code    异常代码，在FtpErrCode中定义
     * @param message 错误信息
     */
    public FtpException(String code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * 异常的构造方法
     *
     * @param code 异常代码，在FtpErrCode中定义
     */
    public FtpException(String code) {
        this(code, FtpErrCode.getCodeMsg(code));
    }

    public FtpException(String code, long nano) {
        this(code);
        setNano(nano);
    }

    public FtpException(String code, Long nano) {
        this(code);
        setNano(nano);
    }

    public FtpException(String code, long nano, String message) {
        this(code, message);
        setNano(nano);
    }

    public FtpException(String code, Long nano, String message) {
        this(code, message);
        setNano(nano);
    }

    public FtpException(String code, String flowNo, long nano) {
        this(code);
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(String code, String flowNo, Long nano) {
        this(code);
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(String code, String flowNo, long nano, String message) {
        this(code, message);
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(String code, String flowNo, Long nano, String message) {
        this(code, message);
        setFlowNo(flowNo);
        setNano(nano);
    }

    /**
     * 异常类的构造方法
     *
     * @param code  异常代码
     * @param cause 异常对象
     */
    public FtpException(String code, Throwable cause, String message) {
        super(message, cause);
        setCode(code);
    }

    public FtpException(String code, Throwable cause) {
        this(code, cause, FtpErrCode.getCodeMsg(code));
    }

    public FtpException(String code, long nano, Throwable cause) {
        this(code, cause);
        setNano(nano);
    }

    public FtpException(String code, Long nano, Throwable cause) {
        this(code, cause);
        setNano(nano);
    }

    public FtpException(String code, long nano, Throwable cause, String message) {
        this(code, cause, message);
        setNano(nano);
    }

    public FtpException(String code, Long nano, Throwable cause, String message) {
        this(code, cause, message);
        setNano(nano);
    }

    public FtpException(Throwable cause, long nano) {
        this(FtpErrCode.getErrCode(cause), cause, cause.getMessage());
        setNano(nano);
    }

    public FtpException(Throwable cause, Long nano) {
        this(FtpErrCode.getErrCode(cause), cause, cause.getMessage());
        setNano(nano);
    }

    public FtpException(String code, String flowNo, long nano, Throwable cause) {
        this(code, cause);
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(String code, String flowNo, Long nano, Throwable cause) {
        this(code, cause);
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(String code, String flowNo, long nano, Throwable cause, String message) {
        this(code, cause, message);
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(String code, String flowNo, Long nano, Throwable cause, String message) {
        this(code, cause, message);
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(Throwable cause, String flowNo, long nano) {
        this(FtpErrCode.getErrCode(cause), cause, cause.getMessage());
        setFlowNo(flowNo);
        setNano(nano);
    }

    public FtpException(Throwable cause, String flowNo, Long nano) {
        this(FtpErrCode.getErrCode(cause), cause, cause.getMessage());
        setFlowNo(flowNo);
        setNano(nano);
    }


    /**
     * 货物异常的文件名称
     *
     * @return 文件名称
     */
    public String getFile() {
        return file;
    }

    /**
     * 设置异常的文件名称
     *
     * @param file 文件名称
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * 获取异常的错误码
     *
     * @return 错误代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置异常的错误码
     *
     * @param code 设置异常的代码
     */
    public void setCode(String code) {
        this.code = code;
    }

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public void setNano(Long nano) {
        this.nano = nano == null ? 0 : nano;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
