package com.dcfs.esc.ftp.datanode.nework.namecli;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.server.model.FileSaveRecord;
import com.dcfs.esc.ftp.comm.constant.SysConst;
import com.dcfs.esc.ftp.comm.dto.InitDto;
import com.dcfs.esc.ftp.comm.helper.DtoStreamChunkHelper;
import com.dcfs.esc.ftp.svr.comm.dto.PutFilePathReqDto;
import com.dcfs.esc.ftp.svr.comm.dto.PutFilePathRspDto;
import com.dcfs.esc.ftp.svr.comm.network.SvrBaseDtoSock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;

/**
 * 更新目录服务器
 * Created by huangzbb on 2017/7/16.
 */
public class NameServerPut extends SvrBaseDtoSock {
    private static final Logger log = LoggerFactory.getLogger(NameServerPut.class);

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private PutFilePathReqDto putReqDto;
    private PutFilePathRspDto putRspDto;
    private int timeOutInterval = SysConst.DEF_CONNECT_TIME_OUT_INTERVAL;//间隔时间
    private int timeOutRetryCount = SysConst.DEF_CONNECT_TIME_OUT_RETRY_COUNT;//重试次数
    private long nano;
    private String seq;//密码交互 //NOSONAR
    private String tags;
    private Node namenode;
    private FileSaveRecord record;

    public NameServerPut(FileSaveRecord record, Node namenode) {
        this.namenode = namenode;
        this.record = record;
    }

    public boolean doPut() throws FtpException, IOException {
        try {
            connect();
            initReq();
            putFileInfo();
        } catch (FtpException e) {
            log.error("nano:{}#查询目录服务器失败#errCode:{},errMsg:{}", nano, e.getCode(), e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.info("nano:{}#查询目录服务器失败#IO异常#ip:port:{}:{}", nano, namenode.getIp(), namenode.getFtpServPort());
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

    private void connect(int timeout) throws FtpException, IOException {//NOSONAR
        // 连接超时
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(namenode.getIp(), namenode.getFtpServPort());
        socket.connect(socketAddress, timeout);
    }

    private void initReq() {
        putReqDto = new PutFilePathReqDto();
        putReqDto.setTags(tags);
        putReqDto.setNodeName(record.getNodeName());
        putReqDto.setSystemName(record.getSystemName());
        putReqDto.setFilePath(record.getFilePath());
        putReqDto.setRequestFilePath(record.getRequestFilePath());
        putReqDto.setFileName(record.getFileName());
        putReqDto.setFileSize(record.getFileSize());
        putReqDto.setFileExt(record.getFileExt());
        putReqDto.setFileMd5(record.getFileMd5());
        putReqDto.setFileVersion(record.getFileVersion());
        String uploadStartTime = (new SimpleDateFormat("yyyyMMddHHmmss")).format(record.getUploadStartTime());
        putReqDto.setUploadStartTime(uploadStartTime);
        String uploadEndTime = (new SimpleDateFormat("yyyyMMddHHmmss")).format(record.getUploadEndTime());
        putReqDto.setUploadEndTime(uploadEndTime);
    }

    private void putFileInfo() throws FtpException, IOException {
        DtoStreamChunkHelper.writeAndFlushDto(out, putReqDto);
        putRspDto = readDtoAndCheck(socket, in, PutFilePathRspDto.class);
        log.debug("nano:{}#文件信息推送至目录服务器结果:{}", nano, putRspDto.isSucc());
        if (!putRspDto.isAuth()) {
            throw new FtpException(putRspDto.getErrCode(), putRspDto.getNano(), putRspDto.getErrMsg());
        }
    }

    public PutFilePathRspDto getPutRspDto() {
        return putRspDto;
    }

}
