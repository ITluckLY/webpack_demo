package com.dc.smarteam.modules.monitor.putfiletomonitor.client;

import com.dc.smarteam.modules.file.entity.FtFileUpload;
import com.dc.smarteam.modules.file.entity.FtFileUploadLog;
import com.dc.smarteam.modules.file.service.FtFileUploadLogService;
import com.dc.smarteam.modules.file.service.FtFileUploadService;
import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpException;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

/**
 * Created by huangzbb on 2016/11/24.
 */
public class FtpPutThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(FtpPutThread.class);

    private String localFile;
    private String remoteFile;
    private String msgType;
    private String tempName;
    private FtpClientConfig config = null;
    private FtFileUploadService ftFileUploadService;
    private FtFileUploadLogService ftFileUploadLogService;

    public FtpPutThread(String localFile, String remoteFile, String msgType, String ip, int port,
                        String tempName, FtFileUploadService ftFileUploadService, FtFileUploadLogService ftFileUploadLogService) {
        this.localFile = localFile;
        this.remoteFile = remoteFile;
        this.msgType = msgType;
        this.tempName = tempName;
        this.ftFileUploadService = ftFileUploadService;
        this.ftFileUploadLogService = ftFileUploadLogService;
        config = FtpClientConfig.getInstance();
        config.setServerIp(ip);
        config.setPort(port);
    }

    public void run() {
        try {
            FtpPutSimple ftpPut = new FtpPutSimple(localFile, remoteFile, msgType, config);
            FileMsgBean fileMsgBean = ftpPut.doFupFile();
            // save db
            FtFileUpload ftFileUpload = ftFileUploadService.get(tempName);
            ftFileUpload.setRealFileName(fileMsgBean.getRealFileName());
            ftFileUpload.setBakFileName(fileMsgBean.getBakFileName());
            ftFileUpload.setRetCode(fileMsgBean.getFileMsgFlag());
            ftFileUpload.setRetMsg(fileMsgBean.getFileRetMsg());
            ftFileUpload.setUpdateDate(new Date());
            ftFileUploadService.update(ftFileUpload);

            //----------------------保存到历史记录中-------------------------
            FtFileUpload ftFileUploadTemp = ftFileUploadService.get(ftFileUpload);
            FtFileUploadLog ftFileUploadLog = new FtFileUploadLog();
            ftFileUploadLog.setRenameFileName(ftFileUploadTemp.getRenameFileName());
            ftFileUploadLog.setFileName(ftFileUploadTemp.getFileName());
            ftFileUploadLog.setFileType(ftFileUploadTemp.getFileType());
            ftFileUploadLog.setRealFileName(ftFileUploadTemp.getRealFileName());
            ftFileUploadLog.setBakFileName(ftFileUploadTemp.getBakFileName());
            ftFileUploadLog.setFileSize(ftFileUploadTemp.getFileSize());
            ftFileUploadLog.setMonitorForDataNodePort(ftFileUploadTemp.getMonitorForDataNodePort());
            ftFileUploadLog.setMonitorNodeIp(ftFileUploadTemp.getMonitorNodeIp());
            ftFileUploadLog.setNodeName(ftFileUploadTemp.getNodeName());
            ftFileUploadLog.setRetCode(ftFileUploadTemp.getRetCode());
            ftFileUploadLog.setRetMsg(ftFileUploadTemp.getRetMsg());
            ftFileUploadLog.setSendNodeName(ftFileUploadTemp.getSendNodeName());
            ftFileUploadLog.setSystemName(ftFileUploadTemp.getSystemName());
            ftFileUploadLog.setUpdateType(ftFileUploadTemp.getUpdateType());
            ftFileUploadLog.setUploadPath(ftFileUploadTemp.getUploadPath());
            ftFileUploadLog.setUploadUser(ftFileUploadTemp.getUploadUser());
            ftFileUploadLog.setCreateBy(ftFileUploadTemp.getCreateBy());
            ftFileUploadLog.setCreateDate(ftFileUploadTemp.getCreateDate());
            ftFileUploadLog.setUpdateBy(ftFileUploadTemp.getUpdateBy());
            ftFileUploadLog.setUpdateDate(ftFileUploadTemp.getUpdateDate());
            ftFileUploadLog.setCurrentUser(ftFileUploadTemp.getCurrentUser());
            ftFileUploadLog.setDelFlag(ftFileUploadTemp.getDelFlag());
            ftFileUploadLog.setId(UUID.randomUUID().toString());
            ftFileUploadLog.setIsNewRecord(ftFileUploadTemp.getIsNewRecord());
            ftFileUploadLog.setRemarks(ftFileUploadTemp.getRemarks());
            ftFileUploadLogService.save(ftFileUploadLog);
            //-----------------------------------------------

        } catch (FtpException e) {
            log.error("", e);
            // save db
            FtFileUpload ftFileUpload = ftFileUploadService.get(tempName);
            ftFileUpload.setRetCode(e.getCode());
            ftFileUpload.setRetMsg(e.getMessage());
            ftFileUpload.setRealFileName(null);
            ftFileUpload.setUpdateDate(new Date());
            ftFileUploadService.update(ftFileUpload);

            FtFileUpload ftFileUploadTemp = ftFileUploadService.get(ftFileUpload);
            FtFileUploadLog ftFileUploadLog = new FtFileUploadLog();
            ftFileUploadLog.setRenameFileName(ftFileUploadTemp.getRenameFileName());
            ftFileUploadLog.setFileName(ftFileUploadTemp.getFileName());
            ftFileUploadLog.setFileType(ftFileUploadTemp.getFileType());
            ftFileUploadLog.setBakFileName(ftFileUploadTemp.getBakFileName());
            ftFileUploadLog.setRealFileName(ftFileUploadTemp.getRealFileName());
            ftFileUploadLog.setFileSize(ftFileUploadTemp.getFileSize());
            ftFileUploadLog.setMonitorForDataNodePort(ftFileUploadTemp.getMonitorForDataNodePort());
            ftFileUploadLog.setMonitorNodeIp(ftFileUploadTemp.getMonitorNodeIp());
            ftFileUploadLog.setNodeName(ftFileUploadTemp.getNodeName());
            ftFileUploadLog.setRetCode(ftFileUploadTemp.getRetCode());
            ftFileUploadLog.setRetMsg(ftFileUploadTemp.getRetMsg());
            ftFileUploadLog.setSendNodeName(ftFileUploadTemp.getSendNodeName());
            ftFileUploadLog.setSystemName(ftFileUploadTemp.getSystemName());
            ftFileUploadLog.setUpdateType(ftFileUploadTemp.getUpdateType());
            ftFileUploadLog.setUploadPath(ftFileUploadTemp.getUploadPath());
            ftFileUploadLog.setUploadUser(ftFileUploadTemp.getUploadUser());
            ftFileUploadLog.setCreateBy(ftFileUploadTemp.getCreateBy());
            ftFileUploadLog.setCreateDate(ftFileUploadTemp.getCreateDate());
            ftFileUploadLog.setUpdateBy(ftFileUploadTemp.getUpdateBy());
            ftFileUploadLog.setUpdateDate(ftFileUploadTemp.getUpdateDate());
            ftFileUploadLog.setCurrentUser(ftFileUploadTemp.getCurrentUser());
            ftFileUploadLog.setDelFlag(ftFileUploadTemp.getDelFlag());
            ftFileUploadLog.setId(UUID.randomUUID().toString());
            ftFileUploadLog.setIsNewRecord(ftFileUploadTemp.getIsNewRecord());
            ftFileUploadLog.setRemarks(ftFileUploadTemp.getRemarks());
            ftFileUploadLogService.save(ftFileUploadLog);
            //-----------------------------------------------
        }
    }
}
