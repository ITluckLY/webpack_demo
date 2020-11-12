package com.dcfs.esb.ftp.innertransfer;

import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by mocg on 2016/7/25.
 */
public class TransferFilePut {
    private static final Logger log = LoggerFactory.getLogger(TransferFilePut.class);
    int pieceNum;
    private String taskType = null;
    private String localFileName = null;
    private String remoteFileName = null;
    private FileTransferConnector conn = null;
    private FileTransferBean putBean = null;
    private File file;
    private InputStream inputStream;

    public TransferFilePut(String taskType, String localFileName, String remoteFileName, FileTransferConnector conn) throws FileNotFoundException {
        this.taskType = taskType;
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
        this.conn = conn;
        file = new File(localFileName);
        if (!file.exists() || !file.isFile()) throw new NestedRuntimeException("文件传输#文件不存在#" + localFileName);
        inputStream = new FileInputStream(localFileName);
    }

    public TransferFilePut(String taskType, InputStream inputStream, String remoteFileName, FileTransferConnector conn) {
        this.taskType = taskType;
        this.inputStream = inputStream;
        this.remoteFileName = remoteFileName;
        this.conn = conn;
    }

    public void doPut() throws IOException {
        putBean = new FileTransferBean();
        putBean.setTaskType(taskType);
        putBean.setClientFileName(localFileName);
        putBean.setFileName(remoteFileName);
        doAuth();
        if (log.isTraceEnabled()) log.trace("文件传输#返回对象:{}", GsonUtil.toJson(putBean));
        if (!putBean.isAuthFlag()) throw new NestedRuntimeException("认证不通过");
        pieceNum = putBean.getPieceNum();
        putFile();
        finish();
    }

    /**
     * 进行用户口令及文件读取权限的认证,返回对象的authFlag等于true则认证成功，否则认证失败
     *
     * @return FileMsgBean，文件消息对象，认证结果存放在该对象中
     */
    private FileTransferBean doAuth() throws IOException {
        log.info("文件传输#开始进行认证.");
        putBean.setFileMsgFlag(FileMsgType.PUT_AUTH);
        conn.writeHead(putBean);
        putBean = conn.readHead();
        log.info("文件传输#用户认证结束.");
        return putBean;
    }

    private void putFile() throws IOException {
        byte[] buff = new byte[pieceNum];
        int index = 0;
        while (true) {
            int read = inputStream.read(buff);
            //读取文件结束
            if (read == -1) {
                conn.writeFileEnd();
                log.info("文件传输#上传文件完成");
                break;
            }
            conn.writeFileContent(buff, 0, read);
            log.info("文件传输#上传了文件第{}片", ++index);
        }
        inputStream.close();
    }


    private void finish() throws IOException {
        conn.readFinish();
        conn.writeFinish();
    }
}
