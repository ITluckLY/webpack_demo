package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mocg on 2016/7/27.
 */
public class SameFileDeleteRecord implements Serializable {
    /*节点名称*/
    private String fromNodeName;
    private String sysname;
    /*文件平台内绝对路径*/
    private String filePath;
    /*文件版本号 文件创建时间*/
    private long newVersion;

    private Date delTime;

    public String getFromNodeName() {
        return fromNodeName;
    }

    public void setFromNodeName(String fromNodeName) {
        this.fromNodeName = fromNodeName;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(long newVersion) {
        this.newVersion = newVersion;
    }

    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }
}
