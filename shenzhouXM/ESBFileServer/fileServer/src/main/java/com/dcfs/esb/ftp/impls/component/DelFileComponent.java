package com.dcfs.esb.ftp.impls.component;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.cmd.DoMngDel;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by mocg on 2016/10/21.
 */
public class DelFileComponent implements IFileComponent {
    private static final Logger log = LoggerFactory.getLogger(DelFileComponent.class);

    @Override
    public EsbFile create(CachedContext context, FileMsgBean bean, FtpConnector conn) throws FtpException {
        // 文件删除
        EsbFile esbFile = null;
        log.info("删除文件信息：{}", bean.getFileName());
        // 判断本地文件是否存在
        String path = EsbFileManager.getInstance().getFileAbsolutePath(bean.getFileName());
        File locFile = new File(path);
        File locTmpFile = new File(path + SvrGlobalCons.DCFS_TMP_FILE_EXT);
        if (locTmpFile.exists()) {
            log.info("temp local file {} is exist!", locTmpFile);
        } else {
            if (!locFile.exists() || !locFile.isFile()) {
                log.debug("要删除的文件 {} 不存在", bean.getFileName());
                //删除远程文件时,文件不存在是正常,不应该抛异常
                throw new FtpException(FtpErrCode.FILE_NOT_FOUND_ERROR);
            }
        }
        esbFile = new EsbFile(bean.getFileName(), EsbFile.SERVER);
        context.getCxtBean().setEsbFile(esbFile);
        return esbFile;
    }

    @Override
    public void preProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        //nothing
    }

    @Override
    public void process(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        DoMngDel del = new DoMngDel();
        del.doCommand(bean, file);
    }

    @Override
    public void afterProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) {
        //nothing
    }

    @Override
    public void finish(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) {
        if (file != null) {
            try {
                file.close();
            } catch (Exception e) {
                log.error("资源回收失败", e);
            }
        }
    }
}
