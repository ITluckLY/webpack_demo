package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodeManager;
import com.dcfs.esb.ftp.server.invoke.node.NodesManager;
import com.dcfs.esb.ftp.spring.outservice.IFileTargetNodeService;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.NullDefTool;
import com.dcfs.esb.ftp.utils.ObjectsTool;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esc.ftp.datanode.nework.namecli.NameServerClient;
import com.dcfs.esc.ftp.svr.comm.model.FilePathValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 从Namenode上查询文件位于哪些节点
 * Created by huangzbb on 2017/7/22.
 */
public class FileTargetNodeByNnService implements IFileTargetNodeService {

    /**
     * 文件目标节点 ip:port  返回-1:表示没有可用节点，null、0:表示按遍历方式 1:表示继续使用本连接
     * 没有可用节点也让API遍历，保证在有延时情况下文件也能下载到
     *
     * @param filePath
     * @return ip:port of target dataNode
     */
    @Override
    public String findTargetNodeAddrByFilePath(String filePath) throws IOException {//NOSONAR
        String sysName = NodeManager.getInstance().getSysName();
        String filePathValueList = NameServerClient.getInstance().get(sysName, filePath);
        if (filePathValueList == null) return "0";//-1
        JsonArray jsonArray = GsonUtil.toJsonArray(filePathValueList);
        List<String> nodeList = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            Gson gson = new Gson();
            FilePathValue filePathValue = gson.fromJson(jsonElement, FilePathValue.class);
            //文件版本号小于1不正常，无效
            if (NullDefTool.defNull(filePathValue.getFileVersion(), 0L) < 1) continue;
            String nodeName = filePathValue.getNodeName();
            nodeList.add(nodeName);
        }
        //选择数据节点，本节点优先
        String currNodeName = FtpConfig.getInstance().getNodeName();
        if (nodeList.contains(currNodeName)) return "1";
        //选择存活的数据节点
        boolean useLegalNode = FtpConfig.getInstance().isUseLegalNode();
        ZkService zkService = ZkService.getInstance();
        Map<String, JsonObject> dataNodeMap = useLegalNode ? zkService.getLegalDataNodeMap() : zkService.getDataNodeMap();
        String nodeName = null;
        String ipPort = null;
        JsonObject jsonObject = null;

        while (!nodeList.isEmpty()) {
            int index = RandomUtils.nextInt(0, nodeList.size());
            String tmpNodeName = nodeList.get(index);
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
            nodeList.remove(index);
        }
        if (nodeName == null) return "0";//-1
        //获取目标节点ip与服务端口 TODO 返回多个
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
