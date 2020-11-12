package com.dcfs.esb.ftp.server.service;

/**
 * Created by mocg on 2016/10/18.
 */
public class GetAuthUser {
    private String uname;

    public GetAuthUser(String uname) {
        this.uname = uname;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetAuthUser that = (GetAuthUser) o;

        return uname != null ? uname.equals(that.uname) : that.uname == null;

    }

    @Override
    public int hashCode() {
        return uname != null ? uname.hashCode() : 0;
    }
}
