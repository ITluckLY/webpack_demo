package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mocg on 2016/7/27.
 */
public class FileDeleteRecord implements Serializable {
    /*节点名称*/
    private String nodeName;
    /*文件平台内绝对路径*/
    private String filePath;

    private Date delTime;

    private String sysname;
    /*文件版本号*/
    private long fileVersion;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(long fileVersion) {
        this.fileVersion = fileVersion;
    }
}
