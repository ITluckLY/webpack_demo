package com.dcfs.esc.ftp.svr.comm.network.clisvr;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.error.FtpIOException;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushRspDto;
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
 * 客户端的服务端的客户端 文件消息推送
 * Created by mocg on 2017/6/19.
 */
public class FileMsgPushCli extends SvrBaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(FileMsgPushCli.class);
    /*接收消息的用户UID,用于校验*/
    private String toUid;
    private String ip;
    private int port;
    private String tranCode;
    private String sysname;
    //服务器上的文件路径(平台内绝对)
    private String serverFileName;
    //客户端上的文件路径（以配置路径为根路径）
    private String clientFileName;
    private long prenano;//前面的nano，即fts-datanode发起推送时已有的nano
    private boolean sync;//消息接收与文件下载同步

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private FileMsgPushReqDto reqDto;
    private FileMsgPushRspDto rspDto;
    private long nano;//fts-client响应时生成的nano
    private String seq;
    private String flowNo;
    private long msgId;

    public FileMsgPushCli(String toUid, String ip, int port, String sysname, String serverFileName, String clientFileName) {
        this.toUid = toUid;
        this.ip = ip;
        this.port = port;
        this.sysname = sysname;
        this.serverFileName = serverFileName;
        this.clientFileName = clientFileName;
    }

    public FileMsgPushRspDto push() throws FtpException {
        try {
            connect();
            initReq();
            doPush();
        } catch (FtpException e) {
            log.debug("nano:{}#flowNo:{}#推送失败#errCode:{},errMsg:{}", nano, flowNo, e.getCode(), e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            throw new FtpIOException(flowNo, nano, e);
        } catch (Exception e) {
            throw new FtpException(e, flowNo, nano);
        } finally {
            IOUtils.closeQuietly(socket);
        }
        return rspDto;
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
        reqDto = new FileMsgPushReqDto();
        reqDto.setToUid(toUid);
        reqDto.setTranCode(tranCode);
        reqDto.setSysname(sysname);
        reqDto.setServerFileName(serverFileName);
        reqDto.setClientFileName(clientFileName);
        reqDto.setPrenano(prenano);
        reqDto.setSync(sync);
        reqDto.setFlowNo(flowNo);
        reqDto.setMsgId(msgId);
    }

    private void doPush() throws IOException, FtpException {
        DtoStreamChunkHelper.writeAndFlushDto(out, reqDto);
        rspDto = readDtoAndCheck(socket, in, FileMsgPushRspDto.class);
        log.debug("nano:{}#flowNo:{}#文件消息推送结果:{}#prenano:{}", nano, flowNo, rspDto.isSucc(), prenano);
        if (!rspDto.isAuth()) {
            throw new FtpException(rspDto.getErrCode(), rspDto.getNano(), rspDto.getErrMsg());
        }
    }

    //getter setter


    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getServerFileName() {
        return serverFileName;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public long getPrenano() {
        return prenano;
    }

    public void setPrenano(long prenano) {
        this.prenano = prenano;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }
}
