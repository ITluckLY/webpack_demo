package com.dcfs.esb.ftp.server.system;


import com.dcfs.esb.ftp.common.error.FtpException;

public interface IProtocol {

    /**
     * 同步上传
     *
     * @return
     * @throws FtpException
     */
    boolean uploadBySync() throws FtpException;

    /**
     * 异步上传
     *
     * @return
     * @throws FtpException
     */
    boolean uploadByAsync() throws FtpException;

    boolean download() throws FtpException;

    void setFileRouteArgs(FileRouteArgs routeArgs);
}
