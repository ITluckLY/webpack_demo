package com.dcfs.esc.ftp.comm.network;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.error.FtpIOException;
import com.dcfs.esc.ftp.comm.chunk.ChunkConfig;
import com.dcfs.esc.ftp.comm.dto.BaseBizDto;
import com.dcfs.esc.ftp.comm.dto.BaseBusiDto;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by mocg on 2017/7/24.
 */
public abstract class SimpleDtoSock<S extends BaseDto, T extends BaseDto> extends BaseDtoSock {
    private String ip;
    private int port;
    private S reqDto;
    private T rspDto;
    private Class<T> rspDtoClass;
    private ChunkConfig chunkConfig;

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    protected long nano;
    protected String seq;

    public SimpleDtoSock(String ip, int port, S reqDto, Class<T> rspDtoClass) {
        this(ip, port, reqDto, rspDtoClass, new ChunkConfig());
    }

    public SimpleDtoSock(String ip, int port, S reqDto, Class<T> rspDtoClass, ChunkConfig chunkConfig) {
        this.ip = ip;
        this.port = port;
        this.reqDto = reqDto;
        this.rspDtoClass = rspDtoClass;
        this.chunkConfig = chunkConfig;
    }

    private void connect() throws IOException, FtpException {
        socket = new Socket(ip, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();
        InitDto initDto = readDtoAndCheck(socket, in, InitDto.class);
        nano = initDto.getNano();
        seq = initDto.getSeq();
        reqDto.setNano(nano);
    }

    /**
     * 初始化reqDto对象
     */
    protected abstract void initReq();

    private void doReqesut() throws FtpException, IOException {
        DtoStreamChunkHelper.writeAndFlushDto(out, reqDto, chunkConfig);
        rspDto = readDtoAndCheck(socket, in, rspDtoClass);
        if (rspDto instanceof BaseBusiDto) {
            BaseBusiDto busiDto = (BaseBusiDto) rspDto;
            String errCode = busiDto.getErrCode();
            if (errCode != null && errCode.length() > 0) throw new FtpException(errCode, busiDto.getNano(), busiDto.getErrMsg());
        } else if (rspDto instanceof BaseBizDto) {
            BaseBizDto bizDto = (BaseBizDto) rspDto;
            String errCode = bizDto.getErrCode();
            if (errCode != null && errCode.length() > 0) throw new FtpException(errCode, bizDto.getNano(), bizDto.getErrMsg());
        }
    }

    public T execute() throws FtpException {
        try {
            connect();
            initReq();
            doReqesut();
        } catch (IOException e) {
            throw new FtpIOException(nano, e);
        } finally {
            IOUtils.closeQuietly(socket);
        }
        return rspDto;
    }

}
