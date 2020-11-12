package com.dcfs.esb.ftp.innertransfer;

import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.GsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by mocg on 2016/7/25.
 */
public class TransferFileReceiver {
    private static final Logger log = LoggerFactory.getLogger(TransferFileReceiver.class);
    private Socket socket;
    private FileTransferConnector conn;
    private FileTransferBean putBean;
    private TransferFileBean fileBean;
    private long timestamp = System.currentTimeMillis();

    private String tranFileTempDir = "";

    public TransferFileReceiver(Socket socket) {
        this.socket = socket;
        tranFileTempDir = FileUtils.getTempDirectoryPath();
    }

    public void doReceive() {
        try {
            conn = new FileTransferConnector(socket);
            putBean = new FileTransferBean();
            putBean = conn.readHead();
            putBean.setPieceNum(FtpConfig.getInstance().getPieceNum());
            doAuth();
            conn.writeHead(putBean);
            if (!putBean.isAuthFlag()) return;

            //校验通过后开始接收文件
            String nodeName = FtpConfig.getInstance().getNodeName();
            fileBean = new TransferFileBean();
            fileBean.setState(1);//表示分发过来的
            fileBean.setNodeName(nodeName);

            //接收文件
            receiveFile();
            saveOptLog();
            finishReceive();
        } catch (Exception e) {
            log.error("文件传输 err", e);
        } finally {
            IOUtils.closeQuietly(socket);
        }
    }

    private void doAuth() {
        putBean.setAuthFlag(true);
    }

    private void receiveFile() throws IOException {
        log.info("文件传输#开始接收文件#{}", putBean.getFileName());
        fileBean.setRequestFilePath(putBean.getFileName());
        fileBean.setFilePath(putBean.getFileName());
        String realFilePath = FilenameUtils.concat(tranFileTempDir, FileUtil.randomTimeFileName(putBean.getFileName()));
        File file = new File(realFilePath);
        if (file.exists()) {
            String delFilePath = realFilePath + "." + timestamp + ".trtmp";
            boolean rename = FileUtil.renameTo(file, new File(delFilePath));
            log.info("文件分发#接收文件已存在,重命名逻辑删除?{}#{},delFilePath:{}", rename, realFilePath, delFilePath);
            /*String ext = FilenameUtils.getExtension(realFilePath);//NOSONAR
            int index = realFilePath.length() - ext.length() - 1;//NOSONAR
            realFilePath = realFilePath.substring(0, index) + "_D" + RandomUtils.nextInt() + "." + ext;//NOSONAR
            log.info("文件传输#接收文件重命名#{}", realFilePath);*///NOSONAR
        } else {
            FileUtil.mkdir(realFilePath);
        }
        fileBean.setRealFilePath(realFilePath);

        try (FileOutputStream fos = new FileOutputStream(realFilePath)) {
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
    }

    private void saveOptLog() {
        if (log.isDebugEnabled()) log.debug("optlog#fileBean:{}", GsonUtil.toJson(fileBean));
    }

    private void finishReceive() {
        //
    }
}
