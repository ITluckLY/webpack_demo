package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.common.cons.KfkTopic;
import com.dcfs.esb.ftp.datanode.config.DataCfg;
import com.dcfs.esb.ftp.kafka.KfkProducer;
import com.dcfs.esb.ftp.server.model.*;
import com.dcfs.esb.ftp.spring.outservice.IFileRecord;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.datanode.nework.namecli.NameServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/26.
 */
@Service("fileRecordService")
public class FileRecordService implements IFileRecord {
    private static final Logger log = LoggerFactory.getLogger(FileRecordService.class);

    @Override
    public void logFileDownload(FileDownloadRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_FILE_DOWNLOAD, GsonUtil.toJson(record), record.getNano(), record.getFlowNo());
    }

    @Override
    public void logFileMsgDownloadResult(FileMsgDownloadResultRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_FILE_MSGDOWNLOADRESULT, GsonUtil.toJson(record), record.getNano(),record.getFlowNo());
    }

    @Override
    public void logFileUpload(FileUploadRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_FILE_UPLOAD, GsonUtil.toJson(record), record.getNano(), record.getFlowNo());
    }

    @Override
    public void logSaveFile(FileSaveRecord record) throws IOException {
        if (DataCfg.isUseNameServer()) {
            //发送文件保存记录到NameServer
            try {
                NameServerClient.getInstance().put(record);
            } catch (Throwable e) {//NOSONAR
                log.error("nano:{}#flowNo:{}#发送文件保存记录到NameServer出错", record.getNano(), record.getFlowNo(), e);
            }
        }

        //根据文件是否重命名，发送到不同的topic上
        String originalFilePath = record.getOriginalFilePath();
        String filePath = record.getFilePath();
        boolean isRename = originalFilePath == null || !filePath.endsWith(originalFilePath);
        KfkTopic kfkTopic = isRename ? KfkTopic.EFS_FILE_SAVE1 : KfkTopic.EFS_FILE_SAVE0;
        KfkProducer.getInstance().send(kfkTopic, GsonUtil.toJson(record), record.getNano(), record.getFlowNo());
    }

    @Override
    public void logDeleteFile(FileDeleteRecord record) throws IOException {
        if (DataCfg.isUseNameServer()) {
            //发送文件删除记录到NameServer
            try {
                NameServerClient.getInstance().del(record);
            } catch (Throwable e) {//NOSONAR
                log.error("发送文件删除记录到NameServer出错", e);
            }
        }
        KfkProducer.getInstance().send(KfkTopic.EFS_FILE_DEL, GsonUtil.toJson(record));
    }

    @Override
    public void logDistributeFile(FileDistributeRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_FILE_DISTRIBUTE, GsonUtil.toJson(record));
    }

    @Override
    public void send(SameFileDeleteRecord record) throws IOException {
        String sysname = record.getSysname();
        if (sysname != null) sysname = sysname.toUpperCase();
        KfkProducer.getInstance().send(sysname + "_" + KfkTopic.EFS_FILE_DEL_SAME, GsonUtil.toJson(record));
    }

    @Override
    public void logPushFileMsg2Cli(PushFileMsg2CliRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_PUSH_FILE_MSG_2CLI, GsonUtil.toJson(record), record.getNano(), record.getFlowNo());
    }

    @Override
    public void logNodeListGet(NodeListGetRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_NODE_LIST_GET, GsonUtil.toJson(record));
    }

    @Override
    public void logClientUnregister(ClientRegisterRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_CLIENT_UNREGISTER, GsonUtil.toJson(record));
    }

    @Override
    public void logClientRegister(ClientRegisterRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_CLIENT_REGISTER, GsonUtil.toJson(record));
    }
    @Override
    public void logUserRegister(UserRegisterRecord record) throws IOException {
        KfkProducer.getInstance().send(KfkTopic.EFS_USER_REGISTER, GsonUtil.toJson(record));
    }
//    @Override
//    public void fileUploadClear(FileUploadClearRecord record) throws IOException {
//        KfkProducer.getInstance().send(KfkTopic.EFS_FILE_UPLOAD_CLEAR, GsonUtil.toJson(record));
//    }
}
