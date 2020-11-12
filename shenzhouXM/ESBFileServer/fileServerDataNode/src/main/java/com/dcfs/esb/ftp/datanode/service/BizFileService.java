package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.adapter.TCPAdapter;
import com.dcfs.esb.ftp.common.model.BizFileMsg;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.servcomm.helper.RequestMsgHelper;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.BusDeal;
import com.dcfs.esb.ftp.server.invoke.bizfile.BizFileDeal;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangzbb on 2016/10/20.
 */
public class BizFileService {
    private static final Logger log = LoggerFactory.getLogger(BizFileService.class);
    private static BizFileService ourInstance = new BizFileService();
    private static final int TIME_OUT = 3000;
    private static final String REQUEST_FILE_PATH = "requestFilePath";

    public static BizFileService getInstance() {
        return ourInstance;
    }

    public ResultDto<String> selByRequestFilePathDto(String requestFilePath) throws IOException {
        ResultDto<String> resultDto;
        String ipPort = ZkService.getInstance().getMasterNameNodeIpPort();
        if (ipPort == null) {
            resultDto = ResultDtoTool.buildError("没有masterNameNode");
            return resultDto;
        }
        String[] split = ipPort.split(":");
        String ip = split[0];
        int port = Integer.parseInt(split[1]);
        TCPAdapter tcpAdapter = new TCPAdapter(ip, port, TIME_OUT);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(REQUEST_FILE_PATH, requestFilePath);
        String msg = RequestMsgHelper.generate(BusDeal.BIZFILE, BizFileDeal.SEL_BY_REQUEST_FILE_PATH, dataMap);
        resultDto = tcpAdapter.invoke(msg, String.class);
        return resultDto;
    }

    public BizFileMsg selByRequestFilePathObj(String requestFilePath) throws IOException {
        //String ipPort = ZkService.getInstance().getMasterNameNodeIpPort();//NOSONAR
        //随机选取一个nameNode节点，获取文件信息
        boolean useLegalNode = FtpConfig.getInstance().isUseLegalNode();
        ZkService zkService = ZkService.getInstance();
        //文件目录服务节点
        Map<String, JsonObject> fileListNodeMap = useLegalNode ? zkService.getLegalLogNodeMap() : zkService.getLogNodeMap();
        if (fileListNodeMap.isEmpty()) return null;
        String ipPort = null;
        int index = RandomUtils.nextInt(0, fileListNodeMap.size());
        int currIndex = 0;
        for (Map.Entry<String, JsonObject> entry : fileListNodeMap.entrySet()) {
            if (index == currIndex++) {
                ipPort = entry.getKey();
                break;
            }
        }
        if (ipPort == null) {
            return null;
        }
        String[] split = ipPort.split(":");
        String ip = split[0];
        int port = Integer.parseInt(split[1]);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(REQUEST_FILE_PATH, requestFilePath);
        String msg = RequestMsgHelper.generate(BusDeal.BIZFILE, BizFileDeal.SEL_BY_REQUEST_FILE_PATH, dataMap);
        TCPAdapter tcpAdapter = new TCPAdapter(ip, port, TIME_OUT);
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            log.debug("从文件目录中获取目标节点:{}", data);
            return GsonUtil.fromJson(data, BizFileMsg.class);
        } else {
            log.warn("从文件目录中获取目标节点失败#{}", resultDto.getMessage());
            return null;
        }
    }

    public ResultDto<String> selByRequestFileLocal(String ip, int port, String requestFilePath) throws IOException {
        TCPAdapter tcpAdapter = new TCPAdapter(ip, port, TIME_OUT);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(REQUEST_FILE_PATH, requestFilePath);
        String msg = RequestMsgHelper.generate(BusDeal.BIZFILE, BizFileDeal.SEL_BY_REQUEST_FILE_PATH, dataMap);
        return tcpAdapter.invoke(msg, String.class);
    }
}
