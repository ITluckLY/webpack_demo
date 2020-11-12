package com.dcfs.esb.ftp.servcomm.dto;

/**
 * Created by mocg on 2016/8/9.
 */
public class ResultDto<T> {
    private String code;
    private String message;
    private T data;

    public ResultDto() {
    }

    public ResultDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
