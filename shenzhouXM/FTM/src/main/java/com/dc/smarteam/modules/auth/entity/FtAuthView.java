package com.dc.smarteam.modules.auth.entity;

/**
 * Created by mocg on 2016/8/1.
 */
public class FtAuthView {
    private String name;
    private String path;
    private String sysname;
    private String user;
    private String permType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPermType() {
        return permType;
    }

    public void setPermType(String permType) {
        this.permType = permType;
    }
}
