package com.dcfs.esc.ftp.namenode.executor;

import com.dcfs.esb.ftp.namenode.spring.core.repository.NodeMonitorRepository;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.namenode.context.ChannelContext;
import com.dcfs.esc.ftp.namenode.redis.RedisService;
import com.dcfs.esc.ftp.namenode.spring.ServiceBeans;
import com.dcfs.esc.ftp.svr.comm.dto.*;
import com.dcfs.esc.ftp.svr.comm.model.FilePathValue;
import com.dcfs.esc.ftp.svr.comm.model.NodeNameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mocg on 2017/6/16.
 */
public class FilePathExecutor {
    private static final Logger log = LoggerFactory.getLogger(FilePathExecutor.class);
    private NodeMonitorRepository nodeMonitorRepository = ServiceBeans.getNodeMonitorRepository();
    private Map<String, Long> nodeFreeSpaceMap = new HashMap<>();
    private static final int timeInterval = 1000 * 60 * 10;// 查询时间间隔
    private long nodeStorageTimestamp = 0L;

    // 上传结束后，记录redis
    public PutFilePathRspDto invoke(ChannelContext channelContext, PutFilePathReqDto reqDto) {
        final long nano = channelContext.getNano();
        final PutFilePathRspDto rspDto = new PutFilePathRspDto();
        if (log.isDebugEnabled()) {
            log.debug("nano:{}#接收到文件推送消息#{}", nano, GsonUtil.toJson(reqDto));
        }
        String filePath = reqDto.getRequestFilePath();
        String systemName = reqDto.getSystemName();
        String nodeName = reqDto.getNodeName();
        Long fileVersion = reqDto.getFileVersion();
        Long fileSize = reqDto.getFileSize();

        FilePathValue filePathValue = new FilePathValue();
        filePathValue.setNodeName(nodeName);
        filePathValue.setFileVersion(fileVersion);
        filePathValue.setFileSize(fileSize);
        String filePathValueStr = GsonUtil.toJson(filePathValue);

        NodeNameValue nodeNameValue = new NodeNameValue();
        nodeNameValue.setFilePath(filePath);
        nodeNameValue.setFileVersion(fileVersion);
        nodeNameValue.setFileSize(fileSize);
        nodeNameValue.setTime((new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()));
        String nodeNameValueStr = GsonUtil.toJson(nodeNameValue);

        boolean b = RedisService.getInstance().addRecord(systemName, filePath, filePathValueStr, nodeName, nodeNameValueStr, nano);
        log.debug("nano:{}#新增记录到目录服务器结果#{}", nano, b);
        if (b) {
            rspDto.setAuth(true);
            rspDto.setSucc(true);
        }
        return rspDto;
    }

    // 下载之前，查询redis
    public GetFilePathRspDto invoke(ChannelContext channelContext, GetFilePathReqDto reqDto) {
        final long nano = channelContext.getNano();
        final GetFilePathRspDto rspDto = new GetFilePathRspDto();
        if (log.isDebugEnabled()) {
            log.debug("nano:{}#接收到文件获取消息#{}", nano, GsonUtil.toJson(reqDto));
        }
        String systemName = reqDto.getSystemName();
        String filePath = reqDto.getFilePath();
        /*// 定期获取各节点磁盘剩余空间
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - nodeStorageTimestamp > timeInterval) {
            nodeStorageTimestamp = timeMillis;
            List<Node> nodeList = NodesManager.getInstance().getNodesBySystem(systemName);
            for (Node node : nodeList) {
                List<NodeMonitor> nodeMonitorList = nodeMonitorRepository.findByNode(node.getName());
                if (nodeMonitorList.size() != 0) {
                    String storage = nodeMonitorList.get(0).getStorage();
                    if (storage == null) continue;
                    String[] split = storage.split("/");
                    Long usedSpace = Long.parseLong(split[0]);
                    Long totalSpace = Long.parseLong(split[1]);
                    nodeFreeSpaceMap.put(node.getName(), totalSpace - usedSpace);
                }
            }
        }*/
        Set<String> filePathValueSet = RedisService.getInstance().getFilePathValue(systemName, filePath, nano);
        List<FilePathValue> filePathValueList = new ArrayList<>();
        for (String filePathValueStr : filePathValueSet) {
            FilePathValue filePathValue = GsonUtil.fromJson(filePathValueStr, FilePathValue.class);
            filePathValueList.add(filePathValue);
        }
        rspDto.setAuth(true);
        rspDto.setSucc(true);
        rspDto.setFilePathValueList(GsonUtil.toJson(filePathValueList));
        return rspDto;
    }

    public RemoveFilePathRspDto invoke(ChannelContext channelContext, RemoveFilePathReqDto reqDto) {
        final long nano = channelContext.getNano();
        final RemoveFilePathRspDto rspDto = new RemoveFilePathRspDto();
        String systemName = reqDto.getSystemName();
        String filePath = reqDto.getFilePath();
        String nodeName = reqDto.getNodeName();
        boolean b = RedisService.getInstance().delRecord(systemName, filePath, nodeName, nano);
        if (b) {
            rspDto.setAuth(true);
            rspDto.setSucc(true);
        }
        return rspDto;
    }

}
