package com.dcfs.esb.ftp.namenode.service;

import com.dcfs.esb.ftp.common.cons.FileTransferTaskType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.innertransfer.FileTransferHandlerFactory;
import com.dcfs.esb.ftp.innertransfer.InnerTcpServer;
import com.dcfs.esb.ftp.innertransfer.SimpleFileTransferHandler;
import com.dcfs.esb.ftp.namenode.handler.FileSaveRerocdFileTransferHandler;
import com.dcfs.esb.ftp.server.config.Cfg;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/25.
 */
public class InnerFileTranService {
    public static void initHandler() {
        FileTransferHandlerFactory.getInstance().put("simple", SimpleFileTransferHandler.class);
        FileTransferHandlerFactory.getInstance().put(FileTransferTaskType.FILE_SAVE_REROCD, FileSaveRerocdFileTransferHandler.class);
    }

    public static void main(String[] args) throws IOException, FtpException {
        Cfg.initConfigPath();
        Cfg.loadFileConfig();
        Cfg.loadFtpConfig();
        initHandler();
        new InnerFileTranService().start();
    }

    public void start() throws IOException {
        final int port = 12345;
        InnerTcpServer innerTcpServer = new InnerTcpServer(port);
        new Thread(innerTcpServer).start();
    }


}
