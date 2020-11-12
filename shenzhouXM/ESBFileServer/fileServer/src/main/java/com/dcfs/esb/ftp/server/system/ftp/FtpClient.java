package com.dcfs.esb.ftp.server.system.ftp;

/**
 * Created by Administrator on 2016/6/22.
 */
public class FtpClient extends org.apache.commons.net.ftp.FTPClient {
    String encoding = null;

    public void setEncoding(String ec) {
        encoding = ec;
    }
}
