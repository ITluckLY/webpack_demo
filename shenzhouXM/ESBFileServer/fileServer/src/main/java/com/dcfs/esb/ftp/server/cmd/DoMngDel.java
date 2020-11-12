package com.dcfs.esb.ftp.server.cmd;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.server.file.EsbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件删除管理命令
 */
public class DoMngDel implements IFileCmd {
    private static final Logger log = LoggerFactory.getLogger(DoMngDel.class);

    /**
     * 文件删除处理指令
     */
    public Object doCommand(FileMsgBean bean, EsbFile file) throws FtpException {
        log.info("删除文件：{}", bean.getFileName());

        EsbFile file1 = new EsbFile(bean.getFileName(), EsbFile.SERVER);
        file1.deleteFile();
        //bean.setFileMsgFlag(FileMsgType.SUCC);//NOSONAR
        bean.setErrCode(FtpErrCode.SUCC);
        return bean;
    }

}
