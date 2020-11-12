package com.dcfs.esb.ftp.server.service;

/**
 * Created by mocg on 2016/10/18.
 */
public class PutAuthUser {
    private String uname;
    private String directoy;
    private boolean isRename;//重命名

    public PutAuthUser(String uname, String directoy, boolean isRename) {
        this.uname = uname;
        this.directoy = directoy;
        this.isRename = isRename;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getDirectoy() {
        return directoy;
    }

    public void setDirectoy(String directoy) {
        this.directoy = directoy;
    }

    public boolean isRename() {
        return isRename;
    }

    public void setRename(boolean rename) {
        isRename = rename;
    }
}
