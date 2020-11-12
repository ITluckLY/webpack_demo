package com.dcfs.esc.ftp.datanode.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.dto.FileDownloadAuthReqDto;
import com.dcfs.esc.ftp.comm.dto.FileDownloadAuthRspDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadAuthReqDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadAuthRspDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.DownloadContextBean;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.process.AuthProcess;
import org.springframework.stereotype.Service;

/**
 * Created by mocg on 2017/6/3.
 */
@Service
public class AuthService {

    private AuthProcess authProcess = new AuthProcess();

    public FileUploadAuthRspDto doAuth(ChannelContext channelContext, UploadContextBean cxtBean, FileUploadAuthReqDto reqDto) throws FtpException {
        return authProcess.doProcess(channelContext, cxtBean, reqDto);
    }

    public FileDownloadAuthRspDto doAuth(ChannelContext channelContext, DownloadContextBean cxtBean, FileDownloadAuthReqDto reqDto) throws FtpException {
        return authProcess.doProcess(channelContext, cxtBean, reqDto);
    }
}
