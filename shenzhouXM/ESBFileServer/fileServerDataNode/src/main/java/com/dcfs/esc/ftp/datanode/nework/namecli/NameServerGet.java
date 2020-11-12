package com.dcfs.esc.ftp.datanode.nework.namecli;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esc.ftp.comm.constant.SysConst;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import com.dcfs.esc.ftp.svr.comm.dto.GetFilePathReqDto;
import com.dcfs.esc.ftp.svr.comm.dto.GetFilePathRspDto;
import com.dcfs.esc.ftp.svr.comm.network.SvrBaseDtoSock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by huangzbb on 2017/7/16.
 */
public class NameServerGet extends SvrBaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(NameServerGet.class);

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private GetFilePathReqDto getReqDto;
    private GetFilePathRspDto getRspDto;

    private int timeOutInterval = SysConst.DEF_CONNECT_TIME_OUT_INTERVAL;//间隔时间
    private int timeOutRetryCount = SysConst.DEF_CONNECT_TIME_OUT_RETRY_COUNT;//重试次数
    private long nano;
    private String seq;//NOSONAR
    private String tags;
    private Node namenode;
    private String systemName;
    private String filePath;

    public NameServerGet(Node namenode, String systemName, String filePath) {
        this.namenode = namenode;
        this.systemName = systemName;
        this.filePath = filePath;
    }

    public boolean doGet() throws IOException, FtpException {
        try {
            connect();
            initReq();
            getFilePathValueList();
        } catch (FtpException e) {
            log.error("nano:{}#查询目录服务失败#errCode:{},errMsg:{}", nano, e.getCode(), e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.info("nano:{}#查询目录服务失败#IO异常#ip:port:{}:{}", nano, namenode.getIp(), namenode.getFtpServPort());
            throw e;
        } finally {
            if (socket != null) socket.close();
        }
        return true;
    }

    private void connect() throws IOException, FtpException {
        int currTimeOut = 0;
        for (int i = 1; i <= timeOutRetryCount; i++) {
            try {
                currTimeOut = i * timeOutInterval;
                connect(currTimeOut);
                break;
            } catch (IOException e) {
                log.debug("nano:{}#connect failed#addr:{}:{}#次数:{}, 超时时间:{}"
                        , nano, namenode.getIp(), namenode.getFtpServPort(), i, currTimeOut, e);
                if (i == timeOutRetryCount) throw e;
            }
        }
        in = socket.getInputStream();
        out = socket.getOutputStream();
        InitDto initDto = readDtoAndCheck(socket, in, InitDto.class);
        nano = initDto.getNano();
        seq = initDto.getSeq();
    }

    private void connect(int timeout) throws IOException {
        // 连接超时
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(namenode.getIp(), namenode.getFtpServPort());
        socket.connect(socketAddress, timeout);
    }

    private void initReq() {
        getReqDto = new GetFilePathReqDto();
        getReqDto.setTags(tags);
        getReqDto.setSystemName(systemName);
        getReqDto.setFilePath(filePath);
    }

    private void getFilePathValueList() throws IOException, FtpException {
        DtoStreamChunkHelper.writeAndFlushDto(out, getReqDto);
        getRspDto = readDtoAndCheck(socket, in, GetFilePathRspDto.class);
        log.info("nano:{}#返回的文件信息列表:{}", nano, getRspDto.getFilePathValueList());
        if (!getRspDto.isAuth()) {
            throw new FtpException(getRspDto.getErrCode(), getRspDto.getNano(), getRspDto.getErrMsg());
        }
    }

    public GetFilePathRspDto getGetRspDto() {
        return getRspDto;
    }
}
