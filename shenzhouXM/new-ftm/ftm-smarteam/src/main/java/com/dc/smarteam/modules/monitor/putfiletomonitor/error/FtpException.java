package com.dc.smarteam.modules.monitor.putfiletomonitor.error;

import java.util.ArrayList;

/**
 * 文件服务器异常类
 */
public class FtpException extends Exception {

    private static final long serialVersionUID = 1L;
    private static ArrayList<String> errorList = new ArrayList<String>();
    private static int maxErrNum = 100;
    private String code = null;
    private String file = null;

    /**
     * 异常的构造方法
     *
     * @param code 异常代码，在FtpErrCode中定义
     */
    public FtpException(String code) {
        super(FtpErrCode.getCodeMsg(code));
        setCode(code);
        this.AddErrorList();
    }

    /**
     * 异常类的构造方法
     *
     * @param code    异常代码，在FtpErrCode中定义
     * @param message 错误信息
     */
    public FtpException(String code, String message) {
        super(message);
        this.setCode(code);
        this.AddErrorList();
    }

    /**
     * 异常类的构造方法
     *
     * @param code  异常代码
     * @param cause 异常对象
     */
    public FtpException(String code, Throwable cause) {
        super(FtpErrCode.getCodeMsg(code), cause);
        setCode(code);
        this.AddErrorList();
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

    /**
     * 将异常码增加到错误列表中
     */
    private void AddErrorList() {
        if (errorList.size() > maxErrNum)
            errorList.remove(0);
        errorList.add(this.code);
    }

    /**
     * 提取异常的错误码列表
     *
     * @return 错误列表，最多100个
     */
    public ArrayList<String> getErrorList() {
        ArrayList<String> tempList = new ArrayList<String>();
        ArrayList<String> retList = errorList;
        errorList = tempList;
        return retList;
    }
}
