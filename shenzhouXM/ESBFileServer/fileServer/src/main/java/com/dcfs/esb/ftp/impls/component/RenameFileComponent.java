package com.dcfs.esb.ftp.impls.component;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.cmd.DoReName;
import com.dcfs.esb.ftp.server.file.EsbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2016/10/21.
 */
public class RenameFileComponent implements IFileComponent {
    private static final Logger log = LoggerFactory.getLogger(RenameFileComponent.class);

    @Override
    public EsbFile create(CachedContext context, FileMsgBean bean, FtpConnector conn) throws FtpException {
        // 文件重命名
        EsbFile esbFile = null;
        log.info("重命名文件 [{}]", bean.getFileName());

        // 修改重命名的文件
        String renameFile = bean.getFileName();
        //todo
        bean.setFileName(renameFile);
        esbFile = new EsbFile(bean.getFileName(), bean.getClientFileName(), EsbFile.SERVER);
        context.getCxtBean().setEsbFile(esbFile);
        return esbFile;
    }

    @Override
    public void preProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        //nothing
    }

    @Override
    public void process(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        DoReName rnm = new DoReName();
        rnm.doCommand(bean, file);
        conn.writeHead(bean, true);
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
