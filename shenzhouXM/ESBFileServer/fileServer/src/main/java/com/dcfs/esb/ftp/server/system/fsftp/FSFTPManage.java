package com.dcfs.esb.ftp.server.system.fsftp;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.distribute.DistributeConnector;
import com.dcfs.esb.ftp.distribute.DistributeFilePut;
import com.dcfs.esb.ftp.server.system.BaseProtocol;
import com.dcfs.esb.ftp.server.system.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mocg on 2016/9/20.
 */
public class FSFTPManage extends BaseProtocol {
    private static final Logger log = LoggerFactory.getLogger(FSFTPManage.class);
    private SystemInfo systemInfo;
    private String localFileName;
    private String remoteFileName;

    public FSFTPManage(SystemInfo systemInfo, String localFileName, String remoteFileName) {
        this.systemInfo = systemInfo;
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
    }

    @Override
    public boolean uploadBySync() throws FtpException {
        try {
            return distributeToOne(systemInfo.getIp(), systemInfo.getPort(), localFileName, remoteFileName);
        } catch (IOException e) {
            log.error("nano:{}#flowNo:{}#上传文件失败", routeArgs.getNano(), routeArgs.getFlowNo(), e);
            throw new FtpException(FtpErrCode.FILE_UP_ERROR, e);
        }
    }

    @Override
    public boolean uploadByAsync() throws FtpException {
        return uploadBySync();
    }

    @Override
    public boolean download() throws FtpException {
        return false;
    }

    private boolean distributeToOne(String ip, int receivePort, String localFileName, String remoteFileName) throws IOException {
        DistributeConnector conn = new DistributeConnector(ip, receivePort);
        DistributeFilePut filePut = new DistributeFilePut(localFileName, remoteFileName, conn);
        filePut.setNano(routeArgs.getNano());
        filePut.setFlowNo(routeArgs.getFlowNo());
        return filePut.doPut();
    }
}
