package com.dcfs.esb.ftp.server.invoke.fileclean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/6.
 */
public class FileClean implements Serializable {
    private String id;
    private String srcPath;
    private long keepTime;//单位:分钟
    private boolean isBackup;
    private String backupPath;
    private String desc;
    private String system;

    public FileClean(String id, String srcPath, long keepTime, boolean isBackup, String backupPath, String desc, String system) {
        this.id = id;
        this.srcPath = srcPath;
        this.keepTime = keepTime;
        this.isBackup = isBackup;
        this.backupPath = backupPath;
        this.desc = desc;
        this.system = system;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public long getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(long keepTime) {
        this.keepTime = keepTime;
    }

    public boolean isBackup() {
        return isBackup;
    }

    public void setBackup(boolean backup) {
        isBackup = backup;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
