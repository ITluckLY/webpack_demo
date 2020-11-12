package com.dcfs.esc.ftp.datanode.svrimpl;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushRspDto;
import com.dcfs.esc.ftp.svr.abstrac.model.FileMsgPushCliParams;
import com.dcfs.esc.ftp.svr.abstrac.model.IFileMsgPushCli;
import com.dcfs.esc.ftp.svr.comm.network.clisvr.FileMsgPushCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/6/26.
 */
public class FileMsgPushCliImpl implements IFileMsgPushCli {
    private static final Logger log = LoggerFactory.getLogger(FileMsgPushCliImpl.class);

    @Override
    public FileMsgPushRspDto pushFileMsg2Cli(FileMsgPushCliParams params) throws FtpException {
        String toUid = params.getToUid();
        String ip = params.getIp();
        int port = params.getPort();
        String sysname = params.getSysname();
        String tranCode = params.getTranCode();
        String serverFileName = params.getServerFileName();
        String clientFileName = params.getClientFileName();
        boolean sync = params.isSync();
        long nano = params.getNano();
        String flowNo = params.getFlowNo();
        log.debug("nano:{}#flowNo:{}#pushFileMsg2Cli begin...", nano, flowNo);
        log.debug("nano:{}#flowNo:{}#pushFileMsg2Cli#toUid:{},ip:{}, port:{}, sysname:{},tranCode:{}, serverFileName:{}, clientFileName:{}"
                , nano, flowNo, toUid, ip, port, sysname, tranCode, serverFileName, clientFileName);
        FileMsgPushCli pushCli = new FileMsgPushCli(toUid, ip, port, sysname, serverFileName, clientFileName);
        pushCli.setTranCode(tranCode);
        pushCli.setPrenano(nano);
        pushCli.setFlowNo(flowNo);
        pushCli.setMsgId(params.getMsgId());
        //消息接收与文件下载同步
        pushCli.setSync(sync);
        return pushCli.push();
    }
}
