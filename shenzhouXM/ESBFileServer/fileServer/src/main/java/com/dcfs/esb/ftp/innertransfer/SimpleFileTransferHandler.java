package com.dcfs.esb.ftp.innertransfer;

import com.dcfs.esb.ftp.utils.GsonUtil;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/25.
 */
public class SimpleFileTransferHandler extends AbstractFileTransferHandler {

    @Override
    public void beforeReceiveFile(FileTransferBean bean, FileTransferConnector conn) throws IOException {
        realFilePath = null;
    }

    @Override
    public void afterReceiveFile(FileTransferBean bean, FileTransferConnector conn) throws IOException {
        System.out.println(GsonUtil.toJson(fileBean));//NOSONAR
        System.out.println(GsonUtil.toJson(bean));//NOSONAR
    }
}
