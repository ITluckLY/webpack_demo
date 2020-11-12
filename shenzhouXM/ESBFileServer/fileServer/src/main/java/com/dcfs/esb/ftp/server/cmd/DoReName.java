package com.dcfs.esb.ftp.server.cmd;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.server.file.EsbFile;

/**
 * 文件重命名处理命令
 */
public class DoReName implements IFileCmd {

    private static final String SUCC_MSG = "交易成功";

    /**
     * 文件重命名命令
     */
    public Object doCommand(FileMsgBean bean, EsbFile file) throws FtpException {
        file.renameFile();
        //bean.setFileMsgFlag(FileMsgType.SUCC);//NOSONAR
        bean.setErrCode(FtpErrCode.SUCC);
        bean.setFileRetMsg(SUCC_MSG);
        return bean;
    }
}
