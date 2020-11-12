package com.dcfs.esc.ftp.datanode.context;

import com.dcfs.esc.ftp.comm.constant.SysCfg;

/**
 * Created by mocg on 2017/6/3.
 */
public class DownloadContextBean extends LoadCommContextBean {
    private boolean fileExists;
    private byte[] buf = new byte[SysCfg.getPieceNum()];
    /*一次文件下载标识*/
    private String downloadId;

    @Override
    public void clean() {
        super.clean();
        buf = null;
    }

    //getset

    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
}
