package com.dcfs.esb.ftp.server.file;

/**
 * 用户与目录的权限关系
 * Created by mocg on 2016/6/22.
 */
public class UserPathAuth {
    private String user;
    private String name;
    private String path;
    private String authType;

    public UserPathAuth(String user, String name, String path, String authType) {
        this.user = user;
        this.name = name;
        this.path = path;
        this.authType = authType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

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

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }
}
