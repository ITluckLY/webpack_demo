package com.dcfs.esc.ftp.datanode.nework.namecli;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.datanode.service.ZkService;
import com.dcfs.esb.ftp.server.model.FileDeleteRecord;
import com.dcfs.esb.ftp.server.model.FileSaveRecord;
import com.dcfs.esc.ftp.svr.comm.dto.GetFilePathRspDto;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mocg on 2017/6/17.
 */
public class NameServerClient {
    private static final Logger log = LoggerFactory.getLogger(NameServerClient.class);

    private NameServerClient() {
    }

    public static NameServerClient getInstance() {
        return new NameServerClient();
    }

    private Node fetchNamenode() {
        //TODO 暂时由master节点统一处理
        Node namenode = new Node();
        String masterNameNodeIpPort = ZkService.getInstance().getMasterNameNodeIpPort();
        String[] split = masterNameNodeIpPort.split(":");
        String namenodeIP = split[0];
        Map<String, JsonObject> nameNodeMap = ZkService.getInstance().getNameNodeMap();
        JsonObject jsonObject = nameNodeMap.get(masterNameNodeIpPort);
        String ftpServPort = jsonObject.get("ftpServ").getAsString();
        namenode.setIp(namenodeIP);
        namenode.setFtpServPort(Integer.valueOf(ftpServPort));
        return namenode;
    }

    /**
     * 上传结束后，记录文件信息
     * EFS_FILE_SAVE1 EFS_FILE_SAVE0
     */
    public void put(FileSaveRecord record) throws IOException {
        if (record == null) return;
        // 分发失败的不入库
        if (record.getState() == -1) return;
        // 文件版本号异常的不入库
        if (record.getFileVersion() < 1L) return;
        Node namenode = fetchNamenode();
        NameServerPut nameServerPut = new NameServerPut(record, namenode);
        try {
            nameServerPut.doPut();
        } catch (FtpException e) {
            log.trace("NameServer保存文件信息出错", e);
        }
    }

    /**
     * 文件下载之前，查询文件信息
     */
    public String get(String systemName, String filePath) {
        Node namenode = fetchNamenode();
        NameServerGet nameServerGet = new NameServerGet(namenode, systemName, filePath);
        String filePathValueList = null;
        try {
            boolean getListSucc = nameServerGet.doGet();
            if (!getListSucc) return null;
            GetFilePathRspDto rspDto = nameServerGet.getGetRspDto();
            filePathValueList = rspDto.getFilePathValueList();
        } catch (FtpException e) {
            log.trace("NameServer查询文件信息出错", e);
        } catch (IOException e) {
            log.trace("NameServer查询文件信息出错", e);
        }
        return filePathValueList;
    }

    /**
     * EFS_FILE_DEL
     */
    public void del(FileDeleteRecord record) {
        Node namenode = fetchNamenode();
        NameServerDel nameServerDel = new NameServerDel(record, namenode);
        try {
            nameServerDel.doDel();
        } catch (IOException e) {
            log.trace("NameServer删除文件记录出错", e);
        } catch (FtpException e) {
            log.trace("NameServer删除文件记录出错", e);
        }
    }

}
