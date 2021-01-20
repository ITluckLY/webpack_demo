package com.dcfs.esc.ftp.datanode.http.httpimpl;

/**
 * Created by Tianyza on 2019/12/20.
 */
public enum TransferOpEnum {
    UPLOAD_POST(1,"UPLOAD_POST") ,
    DOWNLOAD_POST(2,"DOWNLOAD_POST") ,
    UPLOAD_GET(3,"UPLOAD_GET") ,
    DOWNLOAD_GET(4,"DOWNLOAD_GET"),
    PUT(5,"PUT"),
    DELETE(6,"DELETE"),
    UNKNOWN(7,"UNKNOWN"),
    OPTIONS(8,"OPTIONS");


    private int code;
    private String  msg;

    TransferOpEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;

    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer  code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static TransferOpEnum getByValue(Integer value){
        for(TransferOpEnum transactType : values()){
            if (transactType.getCode() == value) {
                return transactType;
            }
        }
        return null;
    }
    public static String getMsgByValue(Integer value){
        for(TransferOpEnum transactType : values()){
            if (transactType.getCode() == value) {
                return transactType.getMsg();
            }
        }
        return null;
    }
}
