package com.dcfs.esb.ftp.server.schedule.task;

import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.distribute.DistributeConnector;
import com.dcfs.esb.ftp.distribute.DistributeFilePut;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import com.dcfs.esb.ftp.server.schedule.LoopTask;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.MD5Util;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by huangzbb on 2016/9/20.
 */
public class NodeFileUpload implements LoopTask {
    private static final Logger log = LoggerFactory.getLogger(NodeFileUpload.class);

    private Map<String, String> params;
    private String remoteFile;
    private boolean isDir;
    private String fileNameRegex;

    private String ip;
    private int receivePort;
    private String realDirPath;

    public void execute() throws Exception {
        String localFile = params.get("localFile");
        remoteFile = params.get("remoteFile");
        String nodeName = params.get("nodeName");
        boolean isAbs = BooleanTool.toBoolean(params.get("isAbs"));
        isDir = BooleanTool.toBoolean(params.get("isDir"));
        fileNameRegex = params.get("fileNameRegex");

        Node node = NodesWorker.getInstance().selNodeByName(nodeName);
        if (node == null) {
            log.error("定时任务#节点文件上传#节点不存在#{}", nodeName);
            return;
        }
        ip = node.getIp();
        receivePort = node.getReceivePort();

        String realFileName;
        if (isAbs) {
            realFileName = localFile;
        } else {
            String fileRootPath = FtpConfig.getInstance().getFileRootPath();
            realFileName = fileRootPath + localFile;
        }
        File realFile = new File(realFileName);

        if (!realFile.exists()) {
            log.error("定时任务#节点文件上传#本地路径不存在#localFile:{},realFile:{}", localFile, realFileName);
            return;
        }

        if (isDir && !realFile.isDirectory()) {
            log.error("定时任务#节点文件上传#参数错误#指定路径不是文件夹#localFile:{},realFile:{}", localFile, realFileName);
            return;
        }

        if (realFile.isFile()) {
            realDirPath = realFile.getParentFile().getCanonicalPath();
            uploadFile(realFile);
        } else if (realFile.isDirectory()) {
            realDirPath = realFile.getCanonicalPath();
            uploadDir(realFile);
        }
    }

    public void init(Map<String, String> params) {
        this.params = params;
    }

    private void uploadFile(File file) throws IOException {
        if (!file.isFile()) return;
        String realFileName = file.getCanonicalPath();
        File uploadedFile = new File(realFileName + SvrGlobalCons.DCFS_FILE_UPLOADED_FILE_EXT);
        if (uploadedFile.exists()) return;//上传过的不再上传

        File uploadingFile = new File(realFileName + SvrGlobalCons.DCFS_FILE_UPLOADING_FILE_EXT);
        boolean newUploadingFile = uploadingFile.createNewFile();
        if (!newUploadingFile) {
            log.debug("定时任务#节点上传文件#createNewFile失败#{}", realFileName);
            return;
        }
        //
        String remoteFile2 = this.remoteFile;
        if (isDir) {
            remoteFile2 = FileUtil.concatFilePath2Linux(this.remoteFile, realFileName.substring(realDirPath.length() + 1));
        }
        String cfgFileName = realFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT;
        File cfgFile = new File(cfgFileName);
        boolean existedFileProperties = cfgFile.exists();
        if (!existedFileProperties) {
            FileMsgBean bean = new FileMsgBean();
            bean.setClientIp(Cfg.getHostTrueIp());
            bean.setFileSize(FileUtils.sizeOf(file));
            bean.setFileName(remoteFile2);
            bean.setClientFileName(realFileName);
            bean.setMd5(MD5Util.md5(file));
            createFileProperties(bean, cfgFileName);
        }

        distributeToOne(ip, receivePort, realFileName, remoteFile2);
        if (!uploadedFile.createNewFile()) {
            log.debug("文件创建失败#path:{}", uploadedFile.getName());
        }
        FileUtils.deleteQuietly(uploadedFile);
        if (!existedFileProperties) FileUtils.deleteQuietly(cfgFile);
    }

    private void uploadDir(File dir) throws IOException {
        if (!dir.isDirectory()) return;
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                return file.isDirectory() || (
                        !fileName.endsWith(SvrGlobalCons.DCFS_CFG_FILE_EXT) && !fileName.endsWith(SvrGlobalCons.DCFS_FILE_UPLOADED_FILE_EXT)
                                && !fileName.endsWith(SvrGlobalCons.DCFS_FILE_UPLOADING_FILE_EXT)
                                && (fileNameRegex == null || fileName.matches(fileNameRegex))
                );
            }
        });
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) uploadDir(file);
                else uploadFile(file);
            }
        }
    }

    private void distributeToOne(String ip, int receivePort, String localFileName, String remoteFileName) throws IOException {
        DistributeConnector conn = new DistributeConnector(ip, receivePort);
        DistributeFilePut filePut = new DistributeFilePut(localFileName, remoteFileName, conn);
        filePut.doPut();
    }

    /**
     * 保存文件的配置信息
     */
    private void createFileProperties(FileMsgBean bean, String cfgFileName) throws IOException {
        Properties fileProperties = new Properties();
        Date now = new Date();
        String nowFormat = DateFormatUtils.format(now, ContextConstants.DATE_FORMAT_PATT);
        PropertiesTool.setByNullSafe(fileProperties, "ClientIp", bean.getClientIp());
        PropertiesTool.setByNullSafe(fileProperties, "FileName", bean.getFileName());
        PropertiesTool.setByNullSafe(fileProperties, "ClientFileName", bean.getClientFileName());
        PropertiesTool.setByNullSafe(fileProperties, "User", bean.getUid());
        PropertiesTool.setByNullSafe(fileProperties, "CreateTime", nowFormat);
        PropertiesTool.setByNullSafe(fileProperties, "FileSize", Long.toString(bean.getFileSize()));
        PropertiesTool.setByNullSafe(fileProperties, "version", Long.toString(now.getTime()));
        PropertiesTool.setByNullSafe(fileProperties, "ClientFileMd5", ScrtUtil.encryptEsb(bean.getMd5()));

        PropertiesTool.setByNullSafe(fileProperties, "UploadStartTime", nowFormat);
        PropertiesTool.setByNullSafe(fileProperties, "UploadEndTime", nowFormat);

        try (FileOutputStream os = new FileOutputStream(cfgFileName)) {
            fileProperties.store(os, "file properties");
        }
    }
}
