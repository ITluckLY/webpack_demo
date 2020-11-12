package com.dcfs.esb.ftp.distribute;

import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by mocg on 2016/7/14.
 */
public class DistributeFilePut {
    private static final Logger log = LoggerFactory.getLogger(DistributeFilePut.class);

    private String localFileName = null;
    private String remoteFileName = null;
    private DistributeConnector conn = null;
    private DistributeFileMsgBean bean = null;
    private File file;//NOSONAR
    private File cfgFile;
    private int pieceNum;
    private Integer result;
    private long nano;
    private String flowNo;

    public DistributeFilePut(String localFileName, String remoteFileName, DistributeConnector conn) {
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
        this.conn = conn;
    }

    public boolean doPut() throws IOException {
        bean = new DistributeFileMsgBean();
        bean.setClientFileName(localFileName);
        bean.setFileName(remoteFileName);
        file = new File(localFileName);
        cfgFile = new File(localFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT);
        if (!file.exists() || !file.isFile())
            throw new NestedRuntimeException("flowNo:" + flowNo + "nano:" + nano + "#文件分发#文件不存在#" + localFileName);//NOSONAR
        if (!cfgFile.exists() || !cfgFile.isFile())
            throw new NestedRuntimeException("flowNo:" + flowNo + "nano:" + nano + "#文件分发#属性文件不存在#" + localFileName);

        bean.setFileSize(file.length());
        doAuth();
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#文件分发#返回对象:{}", nano, flowNo, GsonUtil.toJson(bean));
        }
        if (!BooleanTool.toBoolean(bean.isAuthFlag()))
            throw new NestedRuntimeException("flowNo:" + flowNo + "nano:" + nano + "#认证不通过");
        pieceNum = bean.getPieceNum();
        try {
            putFile();
            putPropertiesFile();
            finishReceive();
        } finally {
            close();
        }
        return result != null && result == 1;
    }

    /**
     * 进行用户口令及文件读取权限的认证,返回对象的authFlag等于true则认证成功，否则认证失败
     *
     * @return FileMsgBean，文件消息对象，认证结果存放在该对象中
     */
    private DistributeFileMsgBean doAuth() throws IOException {
        log.debug("nano:{}#flowNo:{}#文件分发#开始进行认证.", nano, flowNo);
        bean.setFileMsgFlag(FileMsgType.PUT_AUTH);
        conn.writeHead(bean);
        bean = conn.readHead();
        log.debug("nano:{}#flowNo:{}#文件分发#用户认证结束.", nano, flowNo);
        return bean;
    }

    private void putFile() throws IOException {
        FileInputStream fis = null;
        fis = new FileInputStream(localFileName);
        try {
            byte[] buff = new byte[pieceNum];
            int index = 0;
            while (true) {
                int read = fis.read(buff);
                //读取文件结束
                if (read == -1) {
                    conn.writeFileContent(new byte[0]);
                    log.info("nano:{}#flowNo:{}#文件分发#上传文件完成", nano, flowNo);
                    break;
                }
                conn.writeFileContent(buff, 0, read);
                log.debug("nano:{}#flowNo:{}#文件分发#上传了文件第{}片", nano, flowNo, ++index);
            }
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    private void putPropertiesFile() throws IOException {
        try (FileInputStream fis = new FileInputStream(cfgFile)) {
            byte[] buff = new byte[pieceNum];
            int index = 0;
            while (true) {
                int read = fis.read(buff);
                //读取文件结束
                if (read == -1) {
                    conn.writeFileContent(new byte[0]);
                    log.info("nano:{}#flowNo:{}#文件分发#上传属性文件完成", nano, flowNo);
                    break;
                }
                conn.writeFileContent(buff, 0, read);
                log.debug("nano:{}#flowNo:{}#文件分发#上传了属性文件第{}片", nano, flowNo, ++index);
            }
        }
    }

    private void finishReceive() throws IOException {
        bean = conn.readHead();
        result = bean.getResult();
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                log.error("nano:{}#flowNo:{}#close conn", nano, flowNo, e);
            }
        }
    }

    //getter setter


    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
