package com.dc.smarteam.modules.model;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;

/**
 * Created by mocg on 2016/7/8.
 */
public class UserAuthModel implements CfgData{
    //    private String sysname;
    private String userName;
    private String path;
    private String auth;

    public UserAuthModel() {
    }

    public UserAuthModel(String userName, String path, String auth) {
//        this.sysname = sysname;
        this.userName = userName;
        this.path = path;
        this.auth = auth;
    }

//    public String getSysname() {
//        return sysname;
//    }

//    public void setSysname(String sysname) {
//        this.sysname = sysname;
//    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @Override
    public String getParamName() {
        return path+"/"+userName+"/"+auth;
    }
}
