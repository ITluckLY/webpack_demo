package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.common.model.BizFileInfoMsg;
import com.dcfs.esb.ftp.common.model.BizFileMsg;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodesManager;
import com.dcfs.esb.ftp.spring.outservice.IFileTargetNodeService;
import com.dcfs.esb.ftp.utils.NullDefTool;
import com.dcfs.esb.ftp.utils.ObjectsTool;
import com.dcfs.esb.ftp.utils.StringTool;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 从Lognode上查询文件位于哪些节点
 * Created by mocg on 2016/11/7.
 */
public class FileTargetNodeByLnService implements IFileTargetNodeService {
    private static final Logger log = LoggerFactory.getLogger(FileTargetNodeByLnService.class);

    /**
     * 文件目标节点 ip:port  返回-1:表示没有可用节点，null、0:表示按遍历方式 1:表示继续使用本连接
     * 没有可用节点也让API遍历，保证在有延时情况下文件也能下载到
     *
     * @param filePath
     * @return ip:port of target dataNode
     */
    @Override
    public String findTargetNodeAddrByFilePath(String filePath) throws IOException {
        BizFileMsg bizFileMsg = BizFileService.getInstance().selByRequestFilePathObj(filePath);
        if (bizFileMsg == null) return "0";
        List<BizFileInfoMsg> fileInfos = bizFileMsg.getFileInfo();
        Iterator<BizFileInfoMsg> it = fileInfos.iterator();
        while (it.hasNext()) {
            //文件版本号小于1不正常，无效
            BizFileInfoMsg msg = it.next();
            if (NullDefTool.defNull(msg.getFileVersion(), 0L) < 1) {
                log.warn("文件版本号小于1#sysname:{},nodename:{},filepath:{}", msg.getSystemName(), msg.getNodeName(), bizFileMsg.getFilePath());
                it.remove();
            }
        }
        if (fileInfos.isEmpty()) return "0";//-1
        //选择数据节点，本节点优先，没有则随机选择一个存活的其他节点
        String currNodeName = FtpConfig.getInstance().getNodeName();
        for (BizFileInfoMsg info : fileInfos) {
            if (StringUtils.equals(info.getNodeName(), currNodeName)) return "1";
        }
        //选择存活的数据节点
        boolean useLegalNode = FtpConfig.getInstance().isUseLegalNode();
        ZkService zkService = ZkService.getInstance();
        Map<String, JsonObject> dataNodeMap = useLegalNode ? zkService.getLegalDataNodeMap() : zkService.getDataNodeMap();
        String nodeName = null;
        String ipPort = null;
        JsonObject jsonObject = null;
        while (!fileInfos.isEmpty()) {
            int index = RandomUtils.nextInt(0, fileInfos.size());
            String tmpNodeName = fileInfos.get(index).getNodeName();
            boolean outBreak = false;
            for (Map.Entry<String, JsonObject> entry : dataNodeMap.entrySet()) {
                if (ObjectsTool.equals(tmpNodeName, entry.getValue().get("nodeName").getAsString())) {
                    nodeName = tmpNodeName;
                    ipPort = entry.getKey();
                    jsonObject = entry.getValue();
                    outBreak = true;
                    break;
                }
            }
            if (outBreak) break;
            fileInfos.remove(index);
        }
        if (nodeName == null) return "0";//-1
        //获取目标节点ip与服务端口
        int ftpServPort = 0;
        if (jsonObject != null) {
            JsonElement ftpServEle = jsonObject.get("ftpServ");
            if (ftpServEle != null) ftpServPort = StringTool.toInt(ftpServEle.getAsString());
        }
        if (ftpServPort > 0) {
            return ipPort.substring(0, ipPort.indexOf(':') + 1) + ftpServPort;
        } else {
            Node node = NodesManager.getInstance().selNodeByName(nodeName);
            if (node == null) return "0";//-1
            if (node.getState() == 0) return "0";//节点不正常运行
            return node.getIp() + ":" + node.getFtpServPort();
        }
    }
}
