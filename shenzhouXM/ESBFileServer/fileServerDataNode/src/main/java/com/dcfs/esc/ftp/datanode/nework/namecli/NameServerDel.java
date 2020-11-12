package com.dcfs.esc.ftp.datanode.nework.namecli;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.server.model.FileDeleteRecord;
import com.dcfs.esc.ftp.comm.constant.SysConst;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import com.dcfs.esc.ftp.svr.comm.dto.RemoveFilePathReqDto;
import com.dcfs.esc.ftp.svr.comm.dto.RemoveFilePathRspDto;
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
 * 目录服务-删除操作
 * Created by huangzbb on 2017/7/16.
 */
public class NameServerDel extends SvrBaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(NameServerDel.class);

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private RemoveFilePathReqDto removeReqDto;
    private int timeOutInterval = SysConst.DEF_CONNECT_TIME_OUT_INTERVAL;//间隔时间 //NOSONAR
    private int timeOutRetryCount = SysConst.DEF_CONNECT_TIME_OUT_RETRY_COUNT;//重试次数 //NOSONAR
    private long nano;
    private String seq;//密码交互 //NOSONAR
    private String tags;
    private Node namenode;
    private FileDeleteRecord record;

    public NameServerDel(FileDeleteRecord record, Node namenode) {
        this.namenode = namenode;
        this.record = record;
    }

    public boolean doDel() throws FtpException, IOException {
        try {
            connect();
            initReq();
            delFileInfo();
        } catch (FtpException e) {
            log.error("nano:{}#目录服务删除记录失败#errCode:{},errMsg:{}", nano, e.getCode(), e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.info("nano:{}#目录服务删除记录失败#IO异常#ip:port:{}:{}", nano, namenode.getIp(), namenode.getFtpServPort());
            throw e;
        } finally {
            if (socket != null) socket.close();
        }
        return true;
    }

    private void connect() throws FtpException, IOException {
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
        removeReqDto = new RemoveFilePathReqDto();
        removeReqDto.setTags(tags);
        removeReqDto.setNodeName(record.getNodeName());
        removeReqDto.setSystemName(record.getSysname());
        removeReqDto.setFilePath(record.getFilePath());
    }

    private void delFileInfo() throws FtpException, IOException {
        DtoStreamChunkHelper.writeAndFlushDto(out, removeReqDto);
        RemoveFilePathRspDto removeRspDto = readDtoAndCheck(socket, in, RemoveFilePathRspDto.class);
        log.debug("nano:{}#目录服务删除文件记录结果:{}", nano, removeRspDto.isSucc());
        if (!removeRspDto.isAuth()) {
            throw new FtpException(removeRspDto.getErrCode(), removeRspDto.getNano(), removeRspDto.getErrMsg());
        }
    }

}
