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

    private int maxsPeed;   // 最大传输速度单位为MB
    private int sleepTime;   // 最大睡眠时间
    private int scanTime ;  //扫描时间

    private String readLimit; //读取宽带
    private String writeLimit; //写入宽带

    public int getMaxsPeed() {
        return maxsPeed;
    }

    public void setMaxsPeed(int maxsPeed) {
        this.maxsPeed = maxsPeed;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getScanTime() {
        return scanTime;
    }

    public void setScanTime(int scanTime) {
        this.scanTime = scanTime;
    }

    public String getReadLimit() {
        return readLimit;
    }

    public void setReadLimit(String readLimit) {
        this.readLimit = readLimit;
    }

    public String getWriteLimit() {
        return writeLimit;
    }

    public void setWriteLimit(String writeLimit) {
        this.writeLimit = writeLimit;
    }

    public UserAuthModel() {
    }

    public UserAuthModel(String userName, String path, String auth) {
//        this.sysname = sysname;
        this.userName = userName;
        this.path = path;
        this.auth = auth;
    }

    public UserAuthModel(String userName, String path, String auth, int maxsPeed, int sleepTime, int scanTime, String readLimit, String writeLimit) {
        this.userName = userName;
        this.path = path;
        this.auth = auth;
        this.maxsPeed = maxsPeed;
        this.sleepTime = sleepTime;
        this.scanTime = scanTime;
        this.readLimit = readLimit;
        this.writeLimit = writeLimit;
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
