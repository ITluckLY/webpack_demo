package com.dcfs.esb.ftp.impls.component;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.file.EsbFile;

/**
 * Created by mocg on 2016/10/21.
 */
public interface IFileComponent {
    EsbFile create(CachedContext context, FileMsgBean bean, FtpConnector conn) throws FtpException;

    void preProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException;

    void process(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException;

    void afterProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException;

    void finish(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file);
}
