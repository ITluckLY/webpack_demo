package com.dcfs.esb.ftp.server.auth;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {
    private String id;
    private String home;
    private String sysname;
    private String chnName;
    private Map<String, Boolean> list;

    public UserInfo(String id, String home, String sysname, String chnName) {
        this.id = id;
        this.home = home;
        this.sysname = sysname;
        this.chnName = chnName;
        list = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Boolean> getList() {
        return list;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getChnName() {
        return chnName;
    }

    public void setChnName(String chnName) {
        this.chnName = chnName;
    }

    public void addIP(String ip, boolean b) {
        if (ip == null)
            return;
        list.put(ip, b);
    }

    public void addIP(String ip, int b) {
        if (ip == null)
            return;
        list.put(ip, b > 0);
    }
}
