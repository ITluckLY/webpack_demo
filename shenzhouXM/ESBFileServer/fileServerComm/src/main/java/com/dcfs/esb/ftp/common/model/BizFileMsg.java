package com.dcfs.esb.ftp.common.model;

import java.util.List;

/**
 * Created by huangzbb on 2016/10/20.
 */
public class BizFileMsg {
    private String filePath;
    private Long fileSize;
    private String fileMD5;
    private List<BizFileInfoMsg> fileInfo;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public List<BizFileInfoMsg> getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(List<BizFileInfoMsg> fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }
}
