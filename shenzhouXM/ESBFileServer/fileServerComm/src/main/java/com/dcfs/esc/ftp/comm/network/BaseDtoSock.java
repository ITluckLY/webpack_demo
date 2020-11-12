package com.dcfs.esc.ftp.comm.network;

import com.dcfs.esb.ftp.common.compress.CompressFactory;
import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.utils.InetAddressUtil;
import com.dcfs.esc.ftp.comm.constant.SysConst;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ExceptionDto;
import com.dcfs.esc.ftp.comm.dto.HeartbeatDto;
import com.dcfs.esc.ftp.comm.helper.DtoPrintHelper;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by mocg on 2017/6/8.
 */
public abstract class BaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(BaseDtoSock.class);
    private static final int MAX_READ_DTO_TRY_COUNT = 180;

    protected void checkDtoEnded(BaseDto dto) throws FtpException {
        if (dto.isEnd()) throw new FtpException("end");
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseDto> T readDtoAndCheck(Socket socket, InputStream in, Class<T> tClass) throws FtpException, IOException {
        int timeOutInterval = SysConst.DEF_SO_TIME_OUT_INTERVAL;
        int timeOutRetryCount = SysConst.DEF_SO_TIME_OUT_RETRY_COUNT;
        BaseDto dto = readDtoAndCheck(socket, in, tClass, timeOutInterval, timeOutRetryCount);
        return (T) dto;
    }

    @SuppressWarnings("unchecked")
    protected final <T extends BaseDto> T readDtoAndCheck(Socket socket, InputStream in, Class<T> tClass, int timeOutInterval, int timeOutRetryCount) throws FtpException, IOException {//NOSONAR
        BaseDto dto = null;
        int currTimeOut = 0;
        int totalCount = 0;
        for (int i = 1; i <= timeOutRetryCount; i++) {
            if (totalCount++ > MAX_READ_DTO_TRY_COUNT) {
                throw new FtpException(FtpErrCode.READ_DTO_TRY_COUNT_OUT_OF_LIMIT);
            }
            try {
                currTimeOut = i * timeOutInterval;
                dto = readDtoAndCheck(socket, in, tClass, currTimeOut);
                if (dto instanceof HeartbeatDto) {
                    if (log.isTraceEnabled()) log.trace("nano:{}#recieve heartbeat", dto.getNano());
                    //回复心跳
                    DtoStreamChunkHelper.sendHeartbeat(socket);
                    //重新等待
                    i = 1;//NOSONAR
                } else break;
            } catch (SocketTimeoutException e) {
                if (log.isDebugEnabled()) {
                    InetAddress inetAddress = socket.getInetAddress();
                    log.debug("connect failed#addr:{}:{}#超时次数:{}, 时间:{}"
                            , InetAddressUtil.getHostAddress(inetAddress), socket.getPort(), i, currTimeOut, e);
                }
                if (i == timeOutRetryCount) throw e;
            }
        }
        return (T) dto;
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseDto> T readDtoAndCheck(Socket socket, InputStream in, Class<T> tClass, int timeout) throws FtpException, IOException {
        socket.setSoTimeout(timeout);
        BaseDto dto = DtoStreamChunkHelper.readDto(in, tClass);
        DtoPrintHelper.print(dto);
        if (dto instanceof ExceptionDto) {
            ExceptionDto exceptionDto = (ExceptionDto) dto;
            throw new FtpException(exceptionDto.getErrCode(), exceptionDto.getNano(), exceptionDto.getErrMsg());
        }
        //if (dto instanceof HeartbeatDto) return (T) dto;//NOSONAR
        //checkDtoEnded(dto);//NOSONAR
        //if (!tClass.isAssignableFrom(dto.getClass())) throw new FtpException("class类型不对");//NOSONAR
        return (T) dto;
    }

    public byte[] compress(byte[] src, String compressFlag) throws FtpException, IOException {//NOSONAR
        try {
            return CompressFactory.compress(src, compressFlag);
        } catch (Exception e) {
            throw new FtpException("", e);
        }
    }

    protected byte[] decompress(byte[] src, String compressFlag) throws FtpException, IOException {//NOSONAR
        try {
            return CompressFactory.decompress(src, compressFlag);
        } catch (Exception e) {
            throw new FtpException("", e);
        }
    }

}
