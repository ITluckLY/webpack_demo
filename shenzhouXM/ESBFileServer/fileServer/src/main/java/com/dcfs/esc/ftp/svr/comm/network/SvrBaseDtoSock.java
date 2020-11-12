package com.dcfs.esc.ftp.svr.comm.network;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.constant.SysConst;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.network.BaseDtoSock;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by mocg on 2017/6/8.
 */
public abstract class SvrBaseDtoSock extends BaseDtoSock {

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends BaseDto> T readDtoAndCheck(Socket socket, InputStream in, Class<T> tClass) throws IOException, FtpException {
        int timeOutInterval = SysConst.DEF_SO_TIME_OUT_INTERVAL;
        int timeOutRetryCount = SysConst.DEF_SO_TIME_OUT_RETRY_COUNT;
        BaseDto dto = readDtoAndCheck(socket, in, tClass, timeOutInterval, timeOutRetryCount);
        return (T) dto;
    }

}
