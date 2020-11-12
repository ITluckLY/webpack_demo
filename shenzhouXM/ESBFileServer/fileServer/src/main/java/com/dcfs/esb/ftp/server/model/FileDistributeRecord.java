package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mocg on 2016/8/8.
 */
public class FileDistributeRecord implements Serializable {
    private String sysname;
    private String nodeName;
    private String fileName;
    private String realFileName;
    //-1 分发失败，0 没有进行分发，1 分发成功
    private int state;
    private Date optTime;
    /*文件版本号*/
    private long fileVersion;

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getOptTime() {
        return optTime;
    }

    public void setOptTime(Date optTime) {
        this.optTime = optTime;
    }

    public long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(long fileVersion) {
        this.fileVersion = fileVersion;
    }
}
