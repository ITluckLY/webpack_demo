package com.dcfs.esb.ftp.distribute;

import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by mocg on 2016/7/14.
 */
public class DistributeFilePutByStream {
    private static final Logger log = LoggerFactory.getLogger(DistributeFilePutByStream.class);

    private long fileSize;
    private String remoteFileName = null;
    private DistributeConnector conn = null;
    private DistributeFileMsgBean bean = null;
    private int pieceNum;

    public DistributeFilePutByStream(long fileSize, String remoteFileName, DistributeConnector conn) {
        this.fileSize = fileSize;
        this.remoteFileName = remoteFileName;
        this.conn = conn;
    }

    /**
     * 进行用户口令及文件读取权限的认证,返回对象的authFlag等于true则认证成功，否则认证失败
     *
     * @return FileMsgBean，文件消息对象，认证结果存放在该对象中
     */
    public boolean doAuth() throws IOException {
        bean = new DistributeFileMsgBean();
        bean.setClientFileName(null);
        bean.setFileName(remoteFileName);
        bean.setFileSize(fileSize);

        log.debug("文件分发#开始进行认证.");
        bean.setFileMsgFlag(FileMsgType.PUT_AUTH);
        conn.writeHead(bean);
        bean = conn.readHead();
        log.debug("文件分发#用户认证结束.");
        pieceNum = bean.getPieceNum();
        if (log.isTraceEnabled()) {
            log.trace("文件分发#返回对象:{}", GsonUtil.toJson(bean));
        }
        return BooleanTool.toBoolean(bean.isAuthFlag());
    }

    public void putFile(byte[] bytes, int start, int len) throws IOException {
        //读取文件结束
        if (len == 0 || bytes.length == 0) {
            conn.writeFileContent(new byte[0]);
            log.info("文件分发#上传文件完成");
        } else {
            conn.writeFileContent(bytes, start, len);
        }
    }

    public void putPropertiesFile(File cfgFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(cfgFile)) {
            byte[] buff = new byte[pieceNum];
            int index = 0;
            while (true) {
                int read = fis.read(buff);
                //读取文件结束
                if (read == -1) {
                    conn.writeFileContent(new byte[0]);
                    log.info("文件分发#上传属性文件完成");
                    break;
                }
                conn.writeFileContent(buff, 0, read);
                log.debug("文件分发#上传了属性文件第{}片", ++index);
            }
        }
    }

    public Integer finishReceive() throws IOException {
        bean = conn.readHead();
        return bean.getResult();
    }

    public void close() {
        log.debug("close conn...");
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (IOException e) {
                log.error("close conn", e);
            }
        }
    }
}
