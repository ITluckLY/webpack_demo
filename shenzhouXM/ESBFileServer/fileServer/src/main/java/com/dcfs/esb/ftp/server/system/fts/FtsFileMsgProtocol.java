package com.dcfs.esb.ftp.server.system.fts;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.model.PushFileMsg2CliRecord;
import com.dcfs.esb.ftp.server.system.BaseProtocol;
import com.dcfs.esb.ftp.server.system.SystemInfo;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.ThreadSleepUtil;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushRspDto;
import com.dcfs.esc.ftp.svr.abstrac.FacsFactory;
import com.dcfs.esc.ftp.svr.abstrac.factory.IFileMsgPushCliFactory;
import com.dcfs.esc.ftp.svr.abstrac.model.FileMsgPushCliParams;
import com.dcfs.esc.ftp.svr.abstrac.model.IFileMsgPushCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/6/26.
 */
public class FtsFileMsgProtocol extends BaseProtocol {
    private static final Logger log = LoggerFactory.getLogger(FtsFileMsgProtocol.class);
    private SystemInfo systemInfo;
    //private String localFileName
    private String remoteFileName;

    public FtsFileMsgProtocol(SystemInfo systemInfo, String localFileName, String remoteFileName) {//NOSONAR
        this.systemInfo = systemInfo;
        //this.localFileName = localFileName
        this.remoteFileName = remoteFileName;
    }

    @Override
    public boolean uploadBySync() throws FtpException {
        return doSend();
    }

    @Override
    public boolean uploadByAsync() throws FtpException {
        return doSend();
    }

    @Override
    public boolean download() throws FtpException {
        return false;
    }

    private boolean doSend() throws FtpException {
        final long nano = routeArgs.getNano();
        final String flowNo = routeArgs.getFlowNo();
        //另外一个客户端保存在 上传用户ID/上传用户指定的路径(targetFileName)
        remoteFileName = FileUtil.delDuplicateSeparator(routeArgs.getUploadUid() + '/' + remoteFileName);
        FtpConfig ftpConfig = FtpConfig.getInstance();
        String serverFileName = FileUtil.convertToSelfPath(routeArgs.getSvrFilePath());
        //record
        PushFileMsg2CliRecord record = new PushFileMsg2CliRecord();
        long msgId = routeArgs.getMsgId();
        msgId = msgId > 0 ? msgId : UUIDService.nextId();
        record.setMsgId(msgId);
        record.setPrenano(nano);
        record.setRouteName(systemInfo.getName());
        record.setFromUid(routeArgs.getUploadUid());
        record.setToUid(systemInfo.getUsername());
        record.setIp(systemInfo.getIp());
        record.setPort(systemInfo.getPort());
        record.setSysname(ftpConfig.getSystemName());
        record.setTranCode(routeArgs.getTranCode());
        record.setServerFileName(serverFileName);
        record.setClientFileName(remoteFileName);
        record.setSync(routeArgs.isSync());
        record.setFlowNo(flowNo);
        record.setNodeName(ftpConfig.getNodeName());
        //cliParams
        FileMsgPushCliParams cliParams = new FileMsgPushCliParams();
        cliParams.setToUid(systemInfo.getUsername());
        cliParams.setIp(systemInfo.getIp());
        cliParams.setPort(systemInfo.getPort());
        cliParams.setSysname(ftpConfig.getSystemName());
        cliParams.setTranCode(routeArgs.getTranCode());
        cliParams.setServerFileName(serverFileName);
        cliParams.setClientFileName(remoteFileName);
        cliParams.setSync(routeArgs.isSync());
        cliParams.setNano(nano);
        cliParams.setFlowNo(flowNo);
        cliParams.setMsgId(msgId);
        // 延迟推送，避免kafka流水入库过慢导致lognode查不到记录；休眠时间小于等于0时不休眠。
        long pushDelayTime = FtpConfig.getInstance().getFileMsgPushDelayTime();
        if (pushDelayTime > 0) {
            ThreadSleepUtil.sleepIngoreEx(pushDelayTime);
        }
        try {
            FileMsgPushRspDto pushRspDto = pushFileMsg2Cli(cliParams);
            if (pushRspDto != null) {
                record.setNano(pushRspDto.getNano());
                record.setSucc(pushRspDto.isSucc());
                record.setErrCode(pushRspDto.getErrCode());
                record.setErrMsg(pushRspDto.getErrMsg());
            } else {
                record.setSucc(false);
                record.setErrCode(FtpErrCode.FAIL);
                record.setErrMsg("pushRspDto is null");
            }
        } catch (FtpException e) {
            record.setNano(e.getNano());
            record.setErrCode(e.getCode());
            record.setErrMsg(e.getMessage());
            throw e;
        } catch (Exception e) {
            String errCode = FtpErrCode.getErrCode(e);
            record.setErrCode(errCode);
            record.setErrMsg(e.getMessage());
            throw new FtpException(errCode, nano, e);
        } finally {
            EsbFileService.getInstance().logPushFileMsg2Cli(record);
        }
        return record.isSucc();
    }

    private FileMsgPushRspDto pushFileMsg2Cli(FileMsgPushCliParams cliParams) throws FtpException {
        IFileMsgPushCliFactory fileMsgPushCliFactory = FacsFactory.getInstance().getFileMsgPushCliFactory();
        long nano = routeArgs.getNano();
        String flowNo = routeArgs.getFlowNo();
        if (fileMsgPushCliFactory == null) {
            log.debug("nano:{}#flowNo:{}#fileMsgPushCliFactory is null", nano, flowNo);
            return null;
        }
        IFileMsgPushCli fileMsgPushCli = fileMsgPushCliFactory.getFileMsgPushCli();
        if (fileMsgPushCli != null) {
            return fileMsgPushCli.pushFileMsg2Cli(cliParams);
        } else {
            log.warn("nano:{}#flowNo:{}#fileMsgPushCli is null", nano, flowNo);
        }
        return null;
    }

}
