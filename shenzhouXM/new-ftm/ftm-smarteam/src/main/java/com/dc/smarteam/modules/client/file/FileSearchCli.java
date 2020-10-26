package com.dc.smarteam.modules.client.file;

import com.dc.smarteam.modules.client.version.SvrBaseDtoSock;
import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileSearchCli extends SvrBaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(FileSearchCli.class);

    private String ip;
    private int port;

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private FileSearchReqDto reqDto;
    private FileSearchRspDto rspDto;
    private long nano;
    private String seq;//NOSONAR

    public FileSearchCli(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String cliFile(String key,String filePath,String type) throws FtpException {
        try {
            connect();
            initReq(key,filePath);
            String fileJson = doPush();
            if(fileJson == null){
                return null;
            }
            String fileList = JSONObject.fromObject(fileJson).getString(type);
            return fileList;
        } catch (FtpException e) {
            log.debug("nano:{}#客户端文件获取出错#errCode:{},errMsg:{}", nano, e.getCode(), e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, nano, e);
        } finally {
            IOUtils.closeQuietly(socket);
        }
    }

    private void connect() throws IOException, FtpException {
        socket = new Socket(ip, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();
        InitDto initDto = readDtoAndCheck(socket, in, InitDto.class);
        nano = initDto.getNano();
        seq = initDto.getSeq();
    }

    /**
     * select 查询目录
     * selByKey 查询树
     *
     * 1.filePath为空查询key所指定的目录DIR_KEY_SEND
     *
     */
    private void initReq(String filePath) {
        reqDto = new FileSearchReqDto();
        reqDto.setOperateType("select");
        reqDto.setFilePath(filePath);
    }
    private void initReq(String key,String filePath) {
        reqDto = new FileSearchReqDto();
        reqDto.setOperateType(key);
        reqDto.setFilePath(filePath);
    }
    private String doPush() throws IOException, FtpException {
        DtoStreamChunkHelper.writeAndFlushDto(out, reqDto);
        rspDto = readDtoAndCheck(socket, in, FileSearchRspDto.class);
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

    public FileSearchReqDto getReqDto() {
        return reqDto;
    }

    public void setReqDto(FileSearchReqDto reqDto) {
        this.reqDto = reqDto;
    }

    public FileSearchRspDto getRspDto() {
        return rspDto;
    }

    public void setRspDto(FileSearchRspDto rspDto) {
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
