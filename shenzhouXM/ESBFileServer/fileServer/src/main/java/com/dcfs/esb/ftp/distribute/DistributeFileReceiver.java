package com.dcfs.esb.ftp.distribute;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.helper.BizFileHelper;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodeWorker;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import com.dcfs.esb.ftp.server.model.FileSaveRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;

/**
 * Created by mocg on 2016/7/14.
 */
public class DistributeFileReceiver implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DistributeFileReceiver.class);
    private Socket socket;
    private DistributeFileMsgBean bean;
    private DistributeConnector conn;
    private String rootPath;//文件存放根目录(本地路径)
    private FileSaveRecord saveRecord;
    private String realFilePath;
    private String realCfgFilePath;
    private long timestamp;

    public DistributeFileReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            conn = new DistributeConnector(socket);
            bean = new DistributeFileMsgBean();
            bean = conn.readHead();
            bean.setPieceNum(FtpConfig.getInstance().getPieceNum());
            doAuth();
            conn.writeHead(bean);
            if (!BooleanTool.toBoolean(bean.isAuthFlag())) return;

            timestamp = System.currentTimeMillis();
            //校验通过后开始接收文件
            rootPath = FtpConfig.getInstance().getFileRootPath();
            String nodeName = FtpConfig.getInstance().getNodeName();
            saveRecord = new FileSaveRecord();
            saveRecord.setState(1);//表示分发过来的
            saveRecord.setNodeName(nodeName);

            //接收文件
            receiveFile();
            receivePropertiesFile();
            setBizFileByCfgProperties();
            saveOptLog();
            finishReceive();
        } catch (EOFException e1){
            log.debug("请求报文头为空，检测到监控探测#socket:{}", socket);
        } catch (Exception e) {
            log.error("文件分发出错#socket:{}", socket, e);
        } finally {
            IOUtils.closeQuietly(socket);
            try {
                if (conn != null) conn.close();
            } catch (IOException e) {
                log.error("关闭DistributeConnector出错", e);
            }
        }
    }

    private void doAuth() {
        int currNodeIsolState = NodesWorker.getInstance().getCurrNodeIsolState();
        if (currNodeIsolState == 1) {
            bean.setAuthFlag(false);
            bean.setErrCode(FtpErrCode.NODE_ISOLSTATE);
        } else bean.setAuthFlag(true);
    }

    private void receiveFile() throws IOException {
        log.debug("文件分发#开始接收文件#{}", bean.getFileName());
        saveRecord.setRequestFilePath(bean.getFileName());
        saveRecord.setFilePath(bean.getFileName());
        realFilePath = FileUtil.concatFilePath(rootPath, bean.getFileName());
        realFilePath = FileUtil.convertToLocalPath(realFilePath);
        File file = new File(realFilePath);
        if (file.exists()) {
            String delFilePath = realFilePath + "." + timestamp + ".drtmp";
            boolean rename = FileUtil.renameTo(file, new File(delFilePath));
            log.info("文件分发#接收文件已存在,重命名逻辑删除?{}#{},delFilePath:{}", rename, realFilePath, delFilePath);
        } else {
            FileUtil.mkdir(realFilePath);
        }

        String realFilePathTmp = realFilePath + "." + timestamp + ".dtmp";
        try (FileOutputStream fos = new FileOutputStream(realFilePathTmp)) {
            int index = 0;
            while (true) {
                byte[] bytes = conn.readFileContent();
                if (bytes == null) {
                    log.info("文件分发#接收文件完成#{}", realFilePath);
                    break;
                }
                fos.write(bytes);
                log.debug("文件分发#接收了文件第{}片", ++index);
            }
            fos.flush();
        }
        boolean rename = FileUtil.renameTo(new File(realFilePathTmp), new File(realFilePath));
        log.info("文件分发#重命名成功?{}#{}", rename, realFilePath);
        if (!rename) throw new IOException("重命名失败#" + realFilePath);
    }

    private void receivePropertiesFile() throws IOException {
        Properties fileProperties = new Properties();
        log.debug("文件分发#开始接收属性文件#{}", bean.getFileName());
        realCfgFilePath = realFilePath + SvrGlobalCons.DCFS_CFG_FILE_EXT;

        String realCfgFilePathTmp = realCfgFilePath + "." + timestamp + ".dtmp";
        try (FileOutputStream fos = new FileOutputStream(realCfgFilePathTmp);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            int index = 0;
            while (true) {
                byte[] bytes = conn.readFileContent();
                if (bytes == null) {
                    log.info("文件分发#接收属性文件完成#{}", realCfgFilePath);
                    break;
                }
                bos.write(bytes);
                log.debug("文件分发#接收了属性文件第{}片", ++index);
            }
            fileProperties.load(new ByteArrayInputStream(bos.toByteArray()));
            fileProperties.setProperty("FileName", saveRecord.getFilePath());
            fileProperties.setProperty("CreateTime", DateFormatUtils.format(new Date(), ContextConstants.DATE_FORMAT_PATT));
            fileProperties.store(fos, "file properties from distribute");
            fos.flush();
        }
        boolean rename = FileUtil.renameTo(new File(realCfgFilePathTmp), new File(realCfgFilePath));
        log.info("文件分发#重命名成功?{}#{}", rename, realCfgFilePath);
        if (!rename) throw new IOException("重命名失败#" + realCfgFilePath);
    }

    private void setBizFileByCfgProperties() throws IOException {
        Properties properties = new Properties();
        PropertiesTool.load(properties, new File(realCfgFilePath));
        String sysuser = properties.getProperty("User");
        //String[] sysuserArr = sysuser.split("_");//NOSONAR
        saveRecord.setSystemName(NodeWorker.getInstance().getSysName());
        saveRecord.setClientUserName(sysuser);
        Date uploadStartTime = null;
        Date uploadEndTime = null;
        try {
            String uploadStartTimeStr = properties.getProperty("UploadStartTime");
            String uploadEndTimeStr = properties.getProperty("UploadEndTime");
            if (StringUtils.isNoneEmpty(uploadStartTimeStr))
                uploadStartTime = DateUtils.parseDate(uploadStartTimeStr, ContextConstants.DATE_FORMAT_PATT);
            if (StringUtils.isNoneEmpty(uploadEndTimeStr))
                uploadEndTime = DateUtils.parseDate(uploadEndTimeStr, ContextConstants.DATE_FORMAT_PATT);
        } catch (Exception e) {
            log.error("上传时间转换出错", e);
        }

        saveRecord.setClientFilePath(properties.getProperty("ClientFileName"));
        String originalFilePath = properties.getProperty("OriginalFilePath");
        saveRecord.setOriginalFilePath(StringUtils.defaultIfEmpty(originalFilePath, null));
        saveRecord.setClientIp(properties.getProperty("ClientIp"));
        saveRecord.setFileSize(Long.parseLong(properties.getProperty("FileSize", "-1")));
        saveRecord.setUploadStartTime(uploadStartTime);
        saveRecord.setUploadEndTime(uploadEndTime);
        //saveRecord.setFileMd5(properties.get("ClientFileMd5").toString())
        saveRecord.setFileVersion(Long.parseLong(properties.getProperty("version", "0")));
        saveRecord.setNano(100L);//NOSONAR
        saveRecord.setFlowNo(StringUtils.defaultIfEmpty(properties.getProperty("flowNo"), null));
        BizFileHelper.setFileNameExt(saveRecord);

    }

    private void saveOptLog() {
        EsbFileService.getInstance().save(saveRecord);
    }

    private void finishReceive() throws IOException {
        bean.setResult(1);
        conn.writeHead(bean);
    }
}
