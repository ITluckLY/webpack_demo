package com.dcfs.esb.ftp.innertransfer;

import com.dcfs.esb.ftp.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mocg on 2016/7/25.
 */
public abstract class AbstractFileTransferHandler implements FileTransferHandler {
    private static final Logger log = LoggerFactory.getLogger(AbstractFileTransferHandler.class);

    protected FileTransferConnector conn;
    protected FileTransferBean transferBean;
    protected TransferFileBean fileBean;
    protected String realFilePath;

    /**
     * 可以设置realFilePath
     *
     * @param bean
     * @param conn
     * @throws IOException
     */
    public abstract void beforeReceiveFile(FileTransferBean bean, FileTransferConnector conn) throws IOException;

    /**
     * 接收完文件后进行进一步的处理
     *
     * @param bean
     * @param conn
     * @throws IOException
     */
    public abstract void afterReceiveFile(FileTransferBean bean, FileTransferConnector conn) throws IOException;

    @Override
    public final void execute(FileTransferBean bean, FileTransferConnector conn) throws IOException {
        transferBean = bean;
        this.conn = conn;
        fileBean = new TransferFileBean();
        beforeReceiveFile(bean, conn);
        receiveFile();
        afterReceiveFile(bean, conn);
    }

    private void receiveFile() throws IOException {
        log.info("文件传输#开始接收文件#{}", transferBean.getFileName());
        fileBean.setRequestFilePath(transferBean.getFileName());
        fileBean.setFilePath(transferBean.getFileName());
        if (realFilePath == null) {
            realFilePath = FilenameUtils.concat(FileUtils.getTempDirectoryPath(), FileUtil.randomTimeFileName(transferBean.getFileName()));
            File file = new File(realFilePath);
            if (file.exists()) {
                String ext = FilenameUtils.getExtension(realFilePath);
                int index = realFilePath.length() - ext.length() - 1;
                realFilePath = realFilePath.substring(0, index) + "_R" + RandomUtils.nextInt() + "." + ext;
                log.info("文件传输#接收文件重命名#{}", realFilePath);
            } else {
                FileUtil.mkdir(realFilePath);
            }
        }

        fileBean.setRealFilePath(realFilePath);

        String realFilePathTmp = realFilePath + ".itmp";
        try (FileOutputStream fos = new FileOutputStream(realFilePathTmp)) {
            int index = 0;
            while (true) {
                byte[] bytes = conn.readFileContent();
                if (bytes == null) {
                    log.info("文件传输#接收文件完成#{}", realFilePath);
                    break;
                }
                fos.write(bytes);
                log.info("文件传输#接收了文件第{}片", ++index);
            }
            fos.flush();
        }

        boolean rename = new File(realFilePathTmp).renameTo(new File(realFilePath));
        log.info("文件传输#重命名成功?{}#{}", rename, realFilePath);
        if (!rename) throw new IOException("重命名失败#" + realFilePath);
    }
}
