/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.GsonUtil;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeMonitorPushDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorPush;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 节点监控Service
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Service
@Transactional(readOnly = true)
public class FtNodeMonitorPushService extends CrudService<FtNodeMonitorPushDao, FtNodeMonitorPush> {
    @Autowired
    private FtNodeMonitorPushDao ftNodeMonitorPushDao;

    // 操作目标：发送给客户端的文件消费信息
    public static final String FILE_MSG_2_CLIENT = "fileMsg2Client";
    // 操作类型：重新推送
    public static final String REPUSH = "repush";

    private static final String TO_UID = "toUid";
    private static final String FROM_UID = "fromUid";
    private static final String ROUTE_NAME = "routeName";
    private static final String TRAN_CODE = "tranCode";
    private static final String SYSNAME = "sysname";
    private static final String SERVER_FILE_NAME = "serverFileName";
    private static final String CLIENT_FILE_NAME = "clientFileName";
    private static final String PRENANO = "prenano";
    private static final String SYNC = "sync";
    private static final String MSG_ID = "msgId";
    private static final String FLOW_NO = "flowNo";

    public List<FtNodeMonitorPush> findPushList() {
        return super.findList(new FtNodeMonitorPush());
    }

    public String repush2Datenode(FtNodeMonitorPush ftNodeMonitorPush) {
        //拼接报文信息
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(TO_UID, ftNodeMonitorPush.getToUid());
        dataMap.put(FROM_UID, ftNodeMonitorPush.getFromUid());
        dataMap.put(ROUTE_NAME, ftNodeMonitorPush.getRouteName());
        dataMap.put(TRAN_CODE, ftNodeMonitorPush.getTranCode());
        dataMap.put(SYSNAME, ftNodeMonitorPush.getSysname());
        dataMap.put(SERVER_FILE_NAME, ftNodeMonitorPush.getServerFileName());
        dataMap.put(CLIENT_FILE_NAME, ftNodeMonitorPush.getClientFileName());
        dataMap.put(PRENANO, String.valueOf(ftNodeMonitorPush.getPrenano()));
        dataMap.put(SYNC, String.valueOf(ftNodeMonitorPush.isSync()));
        dataMap.put(MSG_ID, String.valueOf(ftNodeMonitorPush.getMsgId()));
        dataMap.put(FLOW_NO, String.valueOf(ftNodeMonitorPush.getFlowNo()));
        HashMap<String, Object> map = new HashMap<>();
        map.put("target", FILE_MSG_2_CLIENT);
        map.put("operateType", REPUSH);
        map.put("data", dataMap);
        String msg = GsonUtil.toJson(map);
        //随机选取一个datanode节点，推送文件信息
        ZkService zkService = ZkService.getInstance();
        Map<String, JsonObject> fileListNodeMap = zkService.getDataNodeMap();
        if (fileListNodeMap.isEmpty()) return "获取datanode失败";
        String ipPort = null;
        int index = RandomUtils.nextInt(0, fileListNodeMap.size());
        int currIndex = 0;
        for (Map.Entry<String, JsonObject> entry : fileListNodeMap.entrySet()) {
            if (index == currIndex++) {
                ipPort = entry.getKey();
                break;
            }
        }
        if (ipPort == null) return "datanode ipPort为空";
        String[] split = ipPort.split(":");
        String ip = split[0];
        String port = split[1];
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setIpAddress(ip);
        ftServiceNode.setCmdPort(port);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode, String.class);
        String result = "文件消息#" + GsonUtil.toJson(dataMap) + "重推成功";
        if (!ResultDtoTool.isSuccess(resultDto)) {
            result = "文件消息#" + GsonUtil.toJson(dataMap) + "重推失败,返回信息#" + resultDto.getMessage();
        }
        return result;
    }
}