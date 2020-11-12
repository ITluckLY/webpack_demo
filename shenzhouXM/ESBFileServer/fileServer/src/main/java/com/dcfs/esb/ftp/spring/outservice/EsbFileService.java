package com.dcfs.esb.ftp.spring.outservice;

import com.dcfs.esb.ftp.server.model.*;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.spring.service.FileRecord2FileService;
import com.dcfs.esb.ftp.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2016/7/13.
 */
public class EsbFileService {
    private static final Logger log = LoggerFactory.getLogger(EsbFileService.class);

    private static EsbFileService instance = new EsbFileService();
    private static FileRecord2FileService fileRecord2FileService = SpringContext.getInstance().getFileRecord2FileService();
    private static IFileRecord fileRecord = SpringContext.getInstance().getiFileRecord();

    private EsbFileService() {
    }

    public static EsbFileService getInstance() {
        return instance;
    }

    /**
     * 保存文件上传成功的记录
     * EFS_FILE_SAVE1 EFS_FILE_SAVE0
     */
    public void save(FileSaveRecord record) {
        if (log.isTraceEnabled()) log.trace("文件保存记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logSaveFile(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * 保存文件下载记录,无论下载是否成功
     * EFS_FILE_DOWNLOAD
     */
    public void logFileDownload(FileDownloadRecord record) {
        if (log.isTraceEnabled()) log.trace("文件下载记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logFileDownload(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * 保存文件下载完成后处理异常记录,
     * EFS_FILE_TRANS
     */
    public void logFileMsgDownloadResult(FileMsgDownloadResultRecord record) {
        if (log.isTraceEnabled()) log.trace("文件下载记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logFileMsgDownloadResult(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }


    /**
     * 保存文件上传记录,无论上传是否成功
     * EFS_FILE_UPLOAD
     */
    public void logFileUpload(FileUploadRecord record) {
        if (log.isTraceEnabled()) log.trace("文件上传记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logFileUpload(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * EFS_FILE_DEL
     */
    public void logFileDelete(FileDeleteRecord record) {
        if (log.isTraceEnabled()) log.trace("文件删除记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logDeleteFile(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * EFS_FILE_DISTRIBUTE
     */
    public void save(FileDistributeRecord record) {
        if (log.isTraceEnabled()) log.trace("文件分发记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logDistributeFile(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * EFS_FILE_DEL_SAME
     */
    public void send(SameFileDeleteRecord record) {
        if (log.isTraceEnabled()) log.trace("删除同名文件记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.send(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.send(record);
            }
        } else fileRecord2FileService.send(record);
    }

    /**
     * EFS_PUSH_FILE_MSG_2CLI
     */
    public void logPushFileMsg2Cli(PushFileMsg2CliRecord record) {
        if (log.isTraceEnabled()) log.trace("文件消息推送记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logPushFileMsg2Cli(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * EFS_NODE_LIST_GET
     */
    public void logNodeListGet(NodeListGetRecord record) {
        if (log.isTraceEnabled()) log.trace("获取节点列表记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logNodeListGet(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }
    /**
     * EFS_CLIENT_REGISTER 客户端注册
     */
    public void logClientRegister(ClientRegisterRecord record) {
        if (log.isTraceEnabled()) log.trace("获取客户端信息记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logClientRegister(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }
    /**
     * EFS_CLIENT_UNREGISTER 客户端注销
     */
    public void logClientUnregister(ClientRegisterRecord record) {
        if (log.isTraceEnabled()) log.trace("获取客户端信息记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logClientUnregister(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * EFS_USER_REGISTER 用户注册
     */
    public void logUserRegister(UserRegisterRecord record) {
        if (log.isTraceEnabled()) log.trace("获取客户端信息记录:{}", GsonUtil.toJson(record));
        if (fileRecord != null) {
            try {
                fileRecord.logUserRegister(record);
            } catch (Exception e) {
                log.error("", e);
                fileRecord2FileService.saveLog(record);
            }
        } else fileRecord2FileService.saveLog(record);
    }

    /**
     * EFS_FILE_UPLOAD_CLEAR  文件归档
     */
//    public void fileUploadClear(FileUploadClearRecord record) {
//        if (log.isTraceEnabled()) log.trace("获取客户端信息记录:{}", GsonUtil.toJson(record));
//        if (fileRecord != null) {
//            try {
//                fileRecord.fileUploadClear(record);
//            } catch (Exception e) {
//                log.error("", e);
//                fileRecord2FileService.saveLog(record);
//            }
//        } else fileRecord2FileService.saveLog(record);
//    }
}
