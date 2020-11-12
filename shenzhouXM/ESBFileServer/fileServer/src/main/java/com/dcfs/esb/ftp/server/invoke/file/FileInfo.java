package com.dcfs.esb.ftp.server.invoke.file;

public class FileInfo {
    private String fileName;
    private String path;
    private FileCfgInfo fileCfgInfo;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileCfgInfo getFileCfgInfo() {
        return fileCfgInfo;
    }

    public void setFileCfgInfo(FileCfgInfo fileCfgInfo) {
        this.fileCfgInfo = fileCfgInfo;
    }


}
