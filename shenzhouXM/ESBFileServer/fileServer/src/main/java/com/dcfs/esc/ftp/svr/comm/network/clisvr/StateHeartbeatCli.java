package com.dcfs.esc.ftp.svr.comm.network.clisvr;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatRspDto;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import com.dcfs.esc.ftp.svr.comm.network.SvrBaseDtoSock;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 客户端的服务端的客户端 应用存活状态心跳探测
 * Created by mocg on 2017/6/19.
 */
public class StateHeartbeatCli extends SvrBaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(StateHeartbeatCli.class);

    private String ip;
    private int port;

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private StateHeartbeatReqDto reqDto;
    private StateHeartbeatRspDto rspDto;
    private long nano;
    private String seq;//NOSONAR

    public StateHeartbeatCli(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean heartbeat() throws FtpException {
        try {
            connect();
            initReq();
            doPush();
        } catch (FtpException e) {
            log.debug("nano:{}#存活状态心跳探测出错#errCode:{},errMsg:{}", nano, e.getCode(), e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, nano, e);
        } finally {
            IOUtils.closeQuietly(socket);
        }
        return rspDto != null;
    }

    private void connect() throws IOException, FtpException {
        socket = new Socket(ip, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();
        InitDto initDto = readDtoAndCheck(socket, in, InitDto.class);
        nano = initDto.getNano();
        seq = initDto.getSeq();
    }

    private void initReq() {
        reqDto = new StateHeartbeatReqDto();
    }

    private void doPush() throws IOException, FtpException {
        DtoStreamChunkHelper.writeAndFlushDto(out, reqDto);
        rspDto = readDtoAndCheck(socket, in, StateHeartbeatRspDto.class);
    }

}
