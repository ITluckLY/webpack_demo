package com.dc.smarteam.modules.client.version;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientVersionCli extends SvrBaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(ClientVersionCli.class);

    private String ip;
    private int port;

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private ClientVersionReqDto reqDto;
    private ClientVersionRspDto rspDto;
    private long nano;
    private String seq;//NOSONAR

    public ClientVersionCli(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String cliVersion(String config) {
        String version = null;
        JSONObject obj = null;
        try {
            connect();
            initReq();
            String cliCfg = doPush();
            obj = JSONObject.fromObject(cliCfg);
            version = obj.getString(config);
        } catch (Exception e) {
            log.debug(e.getMessage());
            return "未获取到客户端信息";
        }
        return version;

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
        reqDto = new ClientVersionReqDto();

    }

    private String doPush() throws IOException, FtpException {
        DtoStreamChunkHelper.writeAndFlushDto(out, reqDto);
        rspDto = readDtoAndCheck(socket, in, ClientVersionRspDto.class);
       return rspDto.getJsonMsg();

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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public ClientVersionReqDto getReqDto() {
        return reqDto;
    }

    public void setReqDto(ClientVersionReqDto reqDto) {
        this.reqDto = reqDto;
    }

    public ClientVersionRspDto getRspDto() {
        return rspDto;
    }

    public void setRspDto(ClientVersionRspDto rspDto) {
        this.rspDto = rspDto;
    }

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
