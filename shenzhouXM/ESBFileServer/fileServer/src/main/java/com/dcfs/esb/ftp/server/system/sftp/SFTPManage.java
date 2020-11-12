package com.dcfs.esb.ftp.server.system.sftp;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.system.BaseProtocol;
import com.dcfs.esb.ftp.server.system.SystemInfo;

public class SFTPManage extends BaseProtocol {
    private SystemInfo systemInfo;
    private String localFileName;
    private String remoteFileName;

    public SFTPManage(SystemInfo systemInfo, String localFileName, String remoteFileName) {
        this.systemInfo = systemInfo;
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
    }

    /**
     * 文件上传
     */
    @Override
    public boolean uploadBySync() throws FtpException {
        return false;
    }

    @Override
    public boolean uploadByAsync() {
        return false;
    }

    /**
     * 文件下载
     */
    @Override
    public boolean download() {
        return false;
    }

}
