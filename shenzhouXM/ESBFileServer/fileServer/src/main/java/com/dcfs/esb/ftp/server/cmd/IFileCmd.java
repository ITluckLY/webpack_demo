package com.dcfs.esb.ftp.server.cmd;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.server.file.EsbFile;

/**
 * 文件服务器处理动作接口
 */
public interface IFileCmd {
    /**
     * 文件服务器处理动作接口
     *
     * @param bean 文件消息对象
     * @param file 文件处理对象
     * @return 返回结果对象
     * @throws FtpException
     */
    Object doCommand(FileMsgBean bean, EsbFile file) throws FtpException;
}
