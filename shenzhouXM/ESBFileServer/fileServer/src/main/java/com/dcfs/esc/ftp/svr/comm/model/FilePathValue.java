package com.dcfs.esc.ftp.svr.comm.model;

/**
 * Created by huangzbb on 2017/7/24.
 */
public class FilePathValue {
    private String nodeName;
    private Long fileVersion;
    private Long fileSize;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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

}
