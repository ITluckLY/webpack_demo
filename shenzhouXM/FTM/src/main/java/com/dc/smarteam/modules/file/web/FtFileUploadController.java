/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.file.entity.FtFileSend;
import com.dc.smarteam.modules.file.entity.FtFileUpload;
import com.dc.smarteam.modules.file.entity.FtFileUploadLog;
import com.dc.smarteam.modules.file.service.FtFileUploadLogService;
import com.dc.smarteam.modules.file.service.FtFileUploadService;
import com.dc.smarteam.modules.monitor.putfiletomonitor.client.FtpPutThread;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgType;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.google.gson.JsonObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 文件管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/file/ftFileUpload")
public class FtFileUploadController extends BaseController {


    @Autowired
    private FtFileUploadService ftFileUploadService;
    @Autowired
    private FtFileUploadLogService ftFileUploadLogService;
    @Value("${localUploadFilePath}")
    private String localUploadFilePath;
    @Value("${localUploadFile}")
    private String localUploadFile;

    @RequiresPermissions("file:ftFileUpload:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtFileUpload ftFileUpload, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        try {
            Page<FtFileUpload> page = ftFileUploadService.findPage(new Page<FtFileUpload>(request, response), ftFileUpload);
            ftFileUpload.setPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        return "modules/file/ftFileUploadList";
    }

    @RequiresPermissions("file:ftFileUpload:view")
    @RequestMapping(value = "listOne")
    public String listOne(FtFileUpload ftFileUpload, HttpServletRequest request, HttpServletResponse response, Model model) {

        FtFileUpload ftFileUploadTemp = ftFileUploadService.get(ftFileUpload);
        model.addAttribute("ftFileUploadTemp", ftFileUploadTemp);
        return "modules/file/ftFileUploadListOne";
    }


    @RequiresPermissions("file:ftFileUpload:view")
    @RequestMapping(value = "form")
    public String form(FtFileUpload ftFileUpload, Model model, HttpServletRequest request) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        ftFileUpload.setSystemName(ftServiceNode.getSystemName());
        ftFileUpload.setNodeName(ftServiceNode.getName());
        model.addAttribute("ftFileUpload", ftFileUpload);
        return "modules/file/ftFileUploadForm";
    }

    @RequiresPermissions("file:ftFileUpload:edit")
    @RequestMapping(value = "save")
    public String save(FtFileUpload ftFileUpload, @RequestParam("uploadFileName") MultipartFile file, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String loginUsername = (String) request.getSession().getAttribute("loginUsername");
        String tempName = String.valueOf(System.currentTimeMillis());
        String originalFilename = file.getOriginalFilename();

        int idx = originalFilename.lastIndexOf('.');
        String extention = originalFilename.substring(idx + 1);
        String fileTemp = originalFilename.substring(0, idx);

        if (extention.isEmpty() || !extention.equalsIgnoreCase("zip")) {
            addMessage(redirectAttributes, "文件上传不成功，注：只能上传ZIP压缩格式文件！");
            return "redirect:" + Global.getAdminPath() + "/file/ftFileUpload/?repage";
        }
        ftFileUpload.setFileType(extention);
        ftFileUpload.setFileName(fileTemp);
        ftFileUpload.setId(tempName);
        ftFileUpload.setRenameFileName(fileTemp + tempName);
        ftFileUpload.setCreateDate(new Date());
        ftFileUpload.setUploadUser(loginUsername);
        StringBuilder sb = new StringBuilder(100).append(fileTemp)
                .append(tempName).append(".").append(extention);
        ftFileUpload.setUploadPath(localUploadFilePath + "/" + sb.toString());
        String path = localUploadFilePath;
        String fileName = sb.toString();
        File targetFile = new File(path, fileName);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            logger.error("", e);
        }
        ftFileUploadService.save(ftFileUpload);
        addMessage(redirectAttributes, "保存文件管理成功");
        return "redirect:" + Global.getAdminPath() + "/file/ftFileUpload/?repage";
    }

    @RequiresPermissions("file:ftFileUpload:edit")
    @RequestMapping(value = "delOne")
    public String delOne(FtFileUpload ftFileUpload, RedirectAttributes redirectAttributes) {

        ftFileUploadService.delOne(ftFileUpload.getId());
        String uploadPath = ftFileUpload.getUploadPath();
        if (uploadPath == null) uploadPath = "";
        File file = new File(uploadPath);
        boolean delete = file.delete();
        if (delete) {
            addMessage(redirectAttributes, "删除文件管理成功");
        } else {
            addMessage(redirectAttributes, "无此文件，请重新确认再进行删除操作！");
        }
        return "redirect:" + Global.getAdminPath() + "/file/ftFileUpload/?repage";
    }

    // 发送到monitor
    @RequiresPermissions("file:ftFileUpload:edit")
    @RequestMapping("sendLink")
    public String sendLink(FtFileUpload ftFileUpload, RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {
        List<FtFileSend> sendNodeList = new ArrayList<FtFileSend>();
        Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
        for (String mnIpPort : monitorNodeMap.keySet()) {
            String[] split = mnIpPort.split(":");
            JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
            String dataNodeName = jsonObject.get("dnName").getAsString();
            FtFileSend ftFileSend = new FtFileSend();
            ftFileSend.setDataNodeName(dataNodeName);
            ftFileSend.setMonitorNodeIp(split[0]);
            ftFileSend.setMonitorNodePort(Integer.parseInt(split[1]));
            sendNodeList.add(ftFileSend);
        }
        model.addAttribute("sendNodeList", sendNodeList);
        return "modules/file/ftFileUploadSendLink";
    }

    @RequiresPermissions("file:ftFileUpload:edit")
    @RequestMapping(value = "send")
    public String send(FtFileUpload ftFileUpload, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        FtpPutThread ftpPutThread = null;
        FtFileUpload ftFileUploadTemp = ftFileUploadService.get(ftFileUpload.getId());
        if (null != ftServiceNode) {
            String nodeName = ftFileUpload.getSendNodeName();
            if (nodeName.isEmpty()) {
                addMessage(redirectAttributes, "数据节点不能为空");
                return "redirect:" + Global.getAdminPath() + "/file/ftFileUpload/?repage";
            }
            String updateType = ftFileUpload.getUpdateType();
            String ip = null;
            int port = 0;
            Map<String, JsonObject> monitorNodeMap = ZkService.getInstance().getMonitorNodeMap();
            for (String mnIpPort : monitorNodeMap.keySet()) {
                JsonObject jsonObject = monitorNodeMap.get(mnIpPort);
                String dataNodeName = jsonObject.get("dnName").getAsString();
                if (dataNodeName.equals(nodeName)) {
                    String[] split = mnIpPort.split(":");
                    ip = split[0];
                    port = Integer.parseInt(split[1]);
                    break;
                }
            }
            ftFileUploadTemp.setSendNodeName(nodeName);
            ftFileUploadTemp.setMonitorNodeIp(ip);
            ftFileUploadTemp.setMonitorForDataNodePort(port);
            ftFileUploadTemp.setUpdateType(updateType);
            ftpPutThread = new FtpPutThread(ftFileUploadTemp.getUploadPath(), "//dn.zip", ftFileUploadTemp.getUpdateType(), ip, port,
                    ftFileUploadTemp.getId(), ftFileUploadService, ftFileUploadLogService);
            ftFileUploadTemp.setRealFileName(null);
            ftFileUploadTemp.setRetCode(FileMsgType.SUCC);
            ftFileUploadTemp.setUpdateDate(new Date());
            ftFileUploadTemp.setRetMsg("版本发布中");
            ftFileUploadService.update(ftFileUploadTemp);

            addMessage(redirectAttributes, "版本发布中，请稍后点击详情查看结果");
            new Thread(ftpPutThread).start();
        }
        return "redirect:" + Global.getAdminPath() + "/file/ftFileUpload/?repage";
    }


    @RequiresPermissions("file:ftFileUpload:view")
    @RequestMapping(value = "listLog")
    public String listLog(FtFileUploadLog ftFileUploadLog, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            Page<FtFileUploadLog> page = ftFileUploadLogService.findPage(new Page<FtFileUploadLog>(request, response), ftFileUploadLog);
            ftFileUploadLog.setPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        return "modules/file/ftFileUploadListLog";
    }

    @RequiresPermissions("file:ftFileUpload:view")
    @RequestMapping(value = "listOneLog")
    public String listOneLog(FtFileUploadLog ftFileUploadLog, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        FtFileUploadLog ftFileUploadLogTemp = null;
        try {
            ftFileUploadLogTemp = ftFileUploadLogService.get(ftFileUploadLog);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询信息失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        model.addAttribute("ftFileUploadLogTemp", ftFileUploadLogTemp);
        return "modules/file/ftFileUploadListOneLog";
    }

}