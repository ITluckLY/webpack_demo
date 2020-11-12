package com.dcfs.esb.ftp.innertransfer;

import com.dcfs.esb.ftp.server.config.FtpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/25.
 */
public class InnerTcpServerDispatcher implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(InnerTcpServerDispatcher.class);

    private FileTransferConnector conn;
    private FileTransferBean bean;

    public InnerTcpServerDispatcher(FileTransferConnector conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        try {
            bean = readFileTransferBean();
            String taskType = bean.getTaskType();
            Class handlerCls = FileTransferHandlerFactory.getInstance().get(taskType);
            if (handlerCls == null) {
                bean.setAuthFlag(false);
            } else doAuth();
            //设置分片大小
            bean.setPieceNum(FtpConfig.getInstance().getPieceNum());
            conn.writeHead(bean);

            if (handlerCls == null) return;
            FileTransferHandler handler = (FileTransferHandler) handlerCls.newInstance();
            handler.execute(bean, conn);
            finish();
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            log.error("", e);
        } finally {
            log.debug("close conn");
            conn.closeQuietly();
        }
    }

    private void doAuth() {
        bean.setAuthFlag(true);
    }

    private FileTransferBean readFileTransferBean() throws IOException {
        return conn.readHead();
    }

    private void finish() throws IOException {
        conn.writeFinish();
        conn.readFinish();
    }
}
