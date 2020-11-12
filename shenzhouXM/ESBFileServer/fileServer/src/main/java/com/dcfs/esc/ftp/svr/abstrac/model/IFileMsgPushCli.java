package com.dcfs.esc.ftp.svr.abstrac.model;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushRspDto;

/**
 * Created by mocg on 2017/6/26.
 */
public interface IFileMsgPushCli {

    /**
     * @param params
     * @return
     * @throws FtpException
     */
    FileMsgPushRspDto pushFileMsg2Cli(FileMsgPushCliParams params) throws FtpException;

}
