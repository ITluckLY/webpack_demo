package com.dc.smarteam.modules.monitor.putfiletomonitor.client;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.file.entity.FtFileRollbackLog;
import com.dc.smarteam.modules.file.service.FtFileRollbackLogService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

/**
 * Created by huangzbb on 2016/12/16.
 */
public class FtRollbackThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(FtRollbackThread.class);

    private String msg;
    private FtServiceNode ftServiceNode;
    private String dataNodeName;
    private String bakFileName;
    private String monitorIp;
    private String monitorCmdPort;
    private FtFileRollbackLogService ftFileRollbackLogService;

    public FtRollbackThread(String msg, FtServiceNode ftServiceNode, String dataNodeName, String bakFileName, FtFileRollbackLogService ftFileRollbackLogService) {
        this.msg = msg;
        this.ftServiceNode = ftServiceNode;
        this.dataNodeName = dataNodeName;
        this.bakFileName = bakFileName;
        this.monitorIp = ftServiceNode.getIpAddress();
        this.monitorCmdPort = ftServiceNode.getCmdPort();
        this.ftFileRollbackLogService = ftFileRollbackLogService;
    }

    public void run() {
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode, String.class, true);//发送报文
        FtFileRollbackLog ftFileRollbackLog = new FtFileRollbackLog();
        ftFileRollbackLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        ftFileRollbackLog.setDataNodeName(dataNodeName);
        ftFileRollbackLog.setBakFileName(bakFileName);
        ftFileRollbackLog.setMonitorIp(monitorIp);
        ftFileRollbackLog.setMonitorCmdPort(monitorCmdPort);
        ftFileRollbackLog.setRetCode(resultDto.getCode());
        ftFileRollbackLog.setRetData(resultDto.getData());
        ftFileRollbackLog.setRetMsg(resultDto.getMessage());
        Date date = new Date();
        ftFileRollbackLog.setCreateDate(date);
        ftFileRollbackLog.setUpdateDate(date);
        ftFileRollbackLogService.save(ftFileRollbackLog);
    }
}
