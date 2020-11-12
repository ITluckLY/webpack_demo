package com.dcfs.esb.ftp.server.socket;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;

import java.io.DataOutputStream;

/**
 * Created by mocg on 2016/7/1.
 */
public class FtpConnectorNotOut extends FtpConnector {
    private FtpConnector conn;//NOSONAR

    public FtpConnectorNotOut(FtpConnector conn) throws FtpException {
        super(conn.getSocket());
        this.conn = conn;
    }

    @Override
    public DataOutputStream getOut() {
        throw new NestedRuntimeException("不允许在此处调用输出方法");//NOSONAR
    }

    @Override
    public void writeHead(FileMsgBean bean) throws FtpException {
        throw new NestedRuntimeException("不允许在此处调用输出方法");
    }

    @Override
    public void writeHead(FileMsgBean bean, boolean isRenm) throws FtpException {
        throw new NestedRuntimeException("不允许在此处调用输出方法");
    }

    @Override
    public void writeFileContent(FileMsgBean bean) throws FtpException {
        throw new NestedRuntimeException("不允许在此处调用输出方法");
    }

}
