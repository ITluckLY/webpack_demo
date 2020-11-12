package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户注册请求流水信息。
 */
public class UserRegisterRecord implements Serializable {
    private String uid;//用户名称,系统内唯一,全部英文
    private String home;//用户目录=/系统名称/用户名称
    private String sysname;//系统名称,全部英文
    private String describe;//用户描述，如中文名称
    private String passwd;
    private String IPText;
    private String status;
    private String IPDescribe;

    private String grant;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getIPText() {
        return IPText;
    }

    public void setIPText(String IPText) {
        this.IPText = IPText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIPDescribe() {
        return IPDescribe;
    }

    public void setIPDescribe(String IPDescribe) {
        this.IPDescribe = IPDescribe;
    }

    public String getGrant() {
        return grant;
    }

    public void setGrant(String grant) {
        this.grant = grant;
    }
}
