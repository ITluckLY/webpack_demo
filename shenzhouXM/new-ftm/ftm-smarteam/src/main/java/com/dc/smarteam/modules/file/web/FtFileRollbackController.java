package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.modules.file.entity.FtFileRollbackLog;
import com.dc.smarteam.modules.file.entity.FtFileSend;
import com.dc.smarteam.modules.file.entity.FtFileUploadLog;
import com.dc.smarteam.modules.file.service.FtFileRollbackLogService;
import com.dc.smarteam.modules.file.service.FtFileUploadLogService;
import com.dc.smarteam.modules.monitor.putfiletomonitor.client.FtRollbackThread;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/file/ftFileRollback")
public class FtFileRollbackController extends BaseController {

    @Resource(name = "FtFileUploadLogServiceImpl")
    private FtFileUploadLogService ftFileUploadLogService;
    @Resource(name = "FtFileRollbackLogServiceImpl")
    private FtFileRollbackLogService ftFileRollbackService;

    @GetMapping(value = "/rollback")
    public Object rollback(FtFileSend ftFileSend) {
        List<FtFileSend> sendNodeList = new ArrayList<FtFileSend>();
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            if (monitorNodeMap.size() == 0) break;
            String[] split = mnIpPort.split(":");
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeName = jsonObject.get("dnName").getAsString();
            FtFileSend ftFileSendTemp = new FtFileSend();
            ftFileSendTemp.setDataNodeName(dataNodeName);
            ftFileSendTemp.setMonitorNodeIp(split[0]);
            ftFileSendTemp.setMonitorNodePort(Integer.parseInt(split[1]));
            sendNodeList.add(ftFileSendTemp);
        }
        return  ResultDtoTool.buildSucceed(sendNodeList);
    }

    @GetMapping(value = "/bakFile")
    public Object bakFile(String dataNodeName) {
        List<FtFileUploadLog> bakFileList = new ArrayList<>();
        FtFileUploadLog ftFileUploadLogTemp = new FtFileUploadLog();
        List<FtFileUploadLog> fileUploadLogList = ftFileUploadLogService.findAll(ftFileUploadLogTemp);
        for (FtFileUploadLog ftFileUploadLog : fileUploadLogList) {
            String bakFileName = ftFileUploadLog.getBakFileName();
            String sendNodeName = ftFileUploadLog.getSendNodeName();
            if (bakFileName != null && sendNodeName != null && sendNodeName.equals(dataNodeName)) {
                bakFileList.add(ftFileUploadLog);
            }
        }
        return  ResultDtoTool.buildSucceed(bakFileList);
    }

    @PostMapping(value = "/send")
    public Object send(FtFileSend ftFileSend, RedirectAttributes redirectAttributes) {

        String dataNodeName = ftFileSend.getDataNodeName();
        if (dataNodeName == null || "0".equals(dataNodeName)) {
            return ResultDtoTool.buildError("未选择数据节点");
        }
        String bakFileName = ftFileSend.getBakFileName();
        if (bakFileName == null || "null".equalsIgnoreCase(bakFileName)) {
            return  ResultDtoTool.buildError("未选择备份文件");
        }
        String monitorIp = null;
        String monitorPort = null;
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeNameTemp = jsonObject.get("dnName").getAsString();
            monitorPort = jsonObject.get("mnCmdPort").getAsString();
            if (dataNodeNameTemp.equals(dataNodeName)) {
                String[] split = mnIpPort.split(":");
                monitorIp = split[0];
                break;
            }
        }
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setIpAddress(monitorIp);
        ftServiceNode.setCmdPort(monitorPort);
        String str = MessageFactory.getInstance().dataNodeRollback(bakFileName, "rollback");
        FtRollbackThread ftRollbackThread = new FtRollbackThread(str, ftServiceNode, dataNodeName, bakFileName, ftFileRollbackService);
        new Thread(ftRollbackThread).start();
        return ResultDtoTool.buildSucceed("版本回滚中，请稍后查看回滚历史记录");
    }

    /**
     *   查询数据
     * @param ftFileRollbackLog
     * @param request
     * @return
     */
    @GetMapping(value ={ "/listLog",""})
    public Object listLog(FtFileRollbackLog ftFileRollbackLog,HttpServletRequest request) {
       /* try {
            Page<FtFileRollbackLog> page = ftFileRollbackService.findPage(new Page<FtFileRollbackLog>(request, response), ftFileRollbackLog);
            ftFileRollbackLog.setPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        return "modules/file/ftFileRollbackListLog";*/



        List<FtFileRollbackLog> list;
        int total = 0;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int pageNum = Integer.valueOf(request.getParameter("pageNum"));
            int pageSize = Integer.valueOf(request.getParameter("pageSize"));
            list = ftFileRollbackService.getFtFileRollbackLogList(pageNum, pageSize);
            total = ftFileRollbackService.getFtFileRollbackLogTotal();
            resultMap.put("list", list);
            resultMap.put("total", total);
            if (log.isDebugEnabled()) {
                log.debug("查询的版本回滚历史记录列表: " + resultMap);
            }
        } catch (Exception e) {

            return ResultDtoTool.buildError("查询信息失败！详情：" + e.getMessage());
        }
        return ResultDtoTool.buildSucceed("成功",resultMap);
    }
}
