package com.dcfs.esb.ftp.server.invoke.file;

public class FileCfgInfo {
    private String fileName;
    private String clientIp;
    private String fileSize;
    private String clientFileName;
    private String clientFileMd5;
    private String createTime;
    private String user;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public String getClientFileMd5() {
        return clientFileMd5;
    }

    public void setClientFileMd5(String clientFileMd5) {
        this.clientFileMd5 = clientFileMd5;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
