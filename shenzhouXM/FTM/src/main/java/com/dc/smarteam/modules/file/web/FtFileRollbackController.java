package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.persistence.Page;
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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/12/2.
 */
@Controller
@RequestMapping(value = "${adminPath}/file/ftFileRollback")
public class FtFileRollbackController extends BaseController {
    @Autowired
    private FtFileUploadLogService ftFileUploadLogService;
    @Autowired
    private FtFileRollbackLogService ftFileRollbackService;

    @RequiresPermissions("file:ftFileRollback:view")
    @RequestMapping(value = {"rollback", ""})
    public String rollback(FtFileSend ftFileSend, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
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
        model.addAttribute("sendNodeList", sendNodeList);
        return "modules/file/ftFileRollback";
    }

    @RequiresPermissions("file:ftFileRollback:view")
    @RequestMapping(value = "bakFile")
    @ResponseBody
    public List<FtFileUploadLog> bakFile(String dataNodeName) {
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
        return bakFileList;
    }

    @RequiresPermissions("file:ftFileRollback:edit")
    @RequestMapping(value = "send")
    public String send(FtFileSend ftFileSend, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        String dataNodeName = ftFileSend.getDataNodeName();
        if (dataNodeName == null || "0".equals(dataNodeName)) {
            addMessage(redirectAttributes, "未选择数据节点");
            return "redirect:" + Global.getAdminPath() + "/file/ftFileRollback/rollback";
        }
        String bakFileName = ftFileSend.getBakFileName();
        if (bakFileName == null || "null".equalsIgnoreCase(bakFileName)) {
            addMessage(redirectAttributes, "未选择备份文件");
            return "redirect:" + Global.getAdminPath() + "/file/ftFileRollback/rollback";
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
        addMessage(redirectAttributes, "版本回滚中，请稍后查看回滚历史记录");
        new Thread(ftRollbackThread).start();
        /*TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(str, ftServiceNode, String.class, true);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            addMessage(redirectAttributes, data);
        } else {
            String data = resultDto.getMessage();
            if (data == null) {
                addMessage(redirectAttributes, "未选中数据节点");
            } else {
                addMessage(redirectAttributes, data);
            }
        }*/
        return "redirect:" + Global.getAdminPath() + "/file/ftFileRollback/rollback";
    }

    @RequiresPermissions("file:ftFileRollback:view")
    @RequestMapping(value = "listLog")
    public String listLog(FtFileRollbackLog ftFileRollbackLog, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            Page<FtFileRollbackLog> page = ftFileRollbackService.findPage(new Page<FtFileRollbackLog>(request, response), ftFileRollbackLog);
            ftFileRollbackLog.setPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        return "modules/file/ftFileRollbackListLog";
    }
}
