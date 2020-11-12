package com.dcfs.esb.ftp.server.client;


/**
 * 客户端状态类
 */
public class ClientStatusInfo {
//    id="127.0.0.1:18000" ip:port
//    status="stop"客户端状态
//    type 类型：启用“enable”和禁用“disable”只有注册，默认为启用。
//    mode重传模式，自动"auto" 和手动"manual"
    private String id = null;
    private String status = null;
    private String type = null;
    private String mode = null;
    private String name = null;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id=" + this.id + ",name=" + this.name + ", status=" + this.status + ",type=" + this.type+ ",mode=" + this.mode;
    }
}
