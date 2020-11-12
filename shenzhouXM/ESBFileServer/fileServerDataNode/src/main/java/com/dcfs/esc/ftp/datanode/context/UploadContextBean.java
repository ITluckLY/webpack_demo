package com.dcfs.esc.ftp.datanode.context;

import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;

/**
 * Created by mocg on 2017/6/3.
 *  文件信息
 */
public class UploadContextBean extends LoadCommContextBean {
    /*文件服务器的文件路径,服务端生成的,与fileName(客户端传给的)可能不同*/
    private String svrFilePath;
    /*临时文件路径*/
    private String tmpFilePath;
    /*上次传输的服务器上的临时文件路径，用于断点续传*/
    private String lastTmpFilePath;
    /*原始文件名*/
    private String oriFilename;

    /*服务端文件重命名*/
    private boolean fileRename;
    /*上传后不路由*/
    private boolean dontRoute;
    /*文件路由结果 0-不确定 1-成功 -1-失败 -2-出错有异常*/
    private int fileRouteResult = CommGlobalCons.RESULT_STATE_NOTSURE;
    /*文件灾备结果 0-不确定 1-成功 -1-失败 -2-出错有异常*/
    private int fileBackupResult = CommGlobalCons.RESULT_STATE_NOTSURE;
    /*一次文件上传标识*/
    private String uploadId;

    //getter setter

    public String getSvrFilePath() {
        return svrFilePath;
    }

    public void setSvrFilePath(String svrFilePath) {
        this.svrFilePath = svrFilePath;
    }

    public boolean isFileRename() {
        return fileRename;
    }

    public void setFileRename(boolean fileRename) {
        this.fileRename = fileRename;
    }

    public String getTmpFilePath() {
        return tmpFilePath;
    }

    public void setTmpFilePath(String tmpFilePath) {
        this.tmpFilePath = tmpFilePath;
    }

    public String getLastTmpFilePath() {
        return lastTmpFilePath;
    }

    public void setLastTmpFilePath(String lastTmpFilePath) {
        this.lastTmpFilePath = lastTmpFilePath;
    }

    public boolean isDontRoute() {
        return dontRoute;
    }

    public void setDontRoute(boolean dontRoute) {
        this.dontRoute = dontRoute;
    }

    public int getFileRouteResult() {
        return fileRouteResult;
    }

    public void setFileRouteResult(int fileRouteResult) {
        this.fileRouteResult = fileRouteResult;
    }

    public int getFileBackupResult() {
        return fileBackupResult;
    }

    public String getOriFilename() {
        return oriFilename;
    }

    public void setOriFilename(String oriFilename) {
        this.oriFilename = oriFilename;
    }

    public void setFileBackupResult(int fileBackupResult) {
        this.fileBackupResult = fileBackupResult;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
