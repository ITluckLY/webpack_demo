package com.dcfs.esb.ftp.innertransfer;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/25.
 */
public interface FileTransferHandler {
    void execute(FileTransferBean bean, FileTransferConnector conn) throws IOException;
}
