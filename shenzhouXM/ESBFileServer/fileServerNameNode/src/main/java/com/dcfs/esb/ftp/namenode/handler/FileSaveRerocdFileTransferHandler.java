package com.dcfs.esb.ftp.namenode.handler;

import com.dcfs.esb.ftp.innertransfer.AbstractFileTransferHandler;
import com.dcfs.esb.ftp.innertransfer.FileTransferBean;
import com.dcfs.esb.ftp.innertransfer.FileTransferConnector;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/25.
 */
public class FileSaveRerocdFileTransferHandler extends AbstractFileTransferHandler {
    @Override
    public void beforeReceiveFile(FileTransferBean bean, FileTransferConnector conn) throws IOException {
        //nothing
    }

    @Override
    public void afterReceiveFile(FileTransferBean bean, FileTransferConnector conn) throws IOException {
        //nothing
    }
}
