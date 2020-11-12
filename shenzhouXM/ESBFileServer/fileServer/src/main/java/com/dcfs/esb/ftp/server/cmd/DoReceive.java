package com.dcfs.esb.ftp.server.cmd;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.server.file.EsbFile;

/**
 * 文件上传处理命令
 */
public class DoReceive implements IFileCmd {
    /**
     * 文件接收命令
     */
    public Object doCommand(FileMsgBean bean, EsbFile file) throws FtpException {
        file.write(bean);
        //bean.setFileMsgFlag(FileMsgType.SUCC);//NOSONAR
        bean.setErrCode(FtpErrCode.SUCC);
        return bean;
    }
}
