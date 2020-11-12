package com.dcfs.esc.ftp.svr.comm.model;

/**
 * Created by huangzbb on 2017/7/24.
 */
public class NodeNameValue {
    private String filePath;
    private Long fileVersion;
    private Long fileSize;
    private String time;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(Long fileVersion) {
        this.fileVersion = fileVersion;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
