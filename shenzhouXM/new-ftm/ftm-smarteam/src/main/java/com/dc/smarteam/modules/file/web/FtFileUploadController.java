/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.json.ResultDtoTool;
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
import com.dc.smarteam.util.PublicRepResultTool;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 文件管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Slf4j
@RestController
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


  /**
   * 查询 版本列表数据
   *
   * @param ftFileUpload
   * @param request
   * @return
   */
  @GetMapping(value = "/list")
  public Object list(FtFileUpload ftFileUpload, HttpServletRequest request) {
    List<FtFileUpload> list;
    int total = 0;
    Map<String, Object> resultMap = new HashMap<>();
    try {
      int pageNo = Integer.valueOf(request.getParameter("pageNo"));
      int pageSize = Integer.valueOf(request.getParameter("pageSize"));

      list = ftFileUploadService.getFtFileUploadList(pageNo, pageSize);
      total = ftFileUploadService.getFtFileUploadTotal();
      resultMap.put("list", list);
      resultMap.put("total", total);
      if(log.isInfoEnabled()){
        log.info("查询的版本列表: " + resultMap);
      }
    } catch (Exception e) {
      return "查询版本单失败！详情：" + e.getMessage();
    }
    return  ResultDtoTool.buildSucceed(resultMap);
  }

  /**
   * 查询数据详情
   *
   * @param ftFileUpload
   * @return
   */
  @GetMapping(value = "/listOne")
  public Object listOne(FtFileUpload ftFileUpload) {

       /* FtFileUpload ftFileUploadTemp = ftFileUploadService.get(ftFileUpload);
        model.addAttribute("ftFileUploadTemp", ftFileUploadTemp);
        return "modules/file/ftFileUploadListOne";*/
    try {
      if (ftFileUpload.getId() != null) {
        ftFileUpload = ftFileUploadService.getFtFileUploadById(ftFileUpload.getId());
      }
    } catch (Exception e) {
      if (log.isDebugEnabled()) {
        log.debug("查询的版本单信息: " + ftFileUpload);
      }
    }
    return   ResultDtoTool.buildSucceed(ftFileUpload);
  }

  /**
   *   文件上传界面：*** 我觉得可以前端操作页面控制***
   * @param
   * @param ftFileUpload
   * @param request
   * @return
   */
  @GetMapping(value = "/form")
  public Object form(FtFileUpload ftFileUpload, HttpServletRequest request) {

    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftFileUpload.setSystemName(ftServiceNode.getSystemName());
    ftFileUpload.setNodeName(ftServiceNode.getName());

    return ResultDtoTool.buildSucceed(ftFileUpload);
  }

  /**
   * 保存上传数据。
   *
   * @param ftFileUpload
   * @param file
   * @param
   * @param request
   * @return
   */
  @PostMapping(value = "/save")
  public Object save(FtFileUpload ftFileUpload, @RequestParam("uploadFileName") MultipartFile file,  HttpServletRequest request) {
    String loginUsername = (String) request.getSession().getAttribute("loginUsername");
    String tempName = String.valueOf(System.currentTimeMillis());
    String originalFilename = file.getOriginalFilename();

    int idx = originalFilename.lastIndexOf('.');
    String extention = originalFilename.substring(idx + 1);
    String fileTemp = originalFilename.substring(0, idx);

    if (extention.isEmpty() || !extention.equalsIgnoreCase("zip")) {
      String message = "文件上传不成功，注：只能上传ZIP压缩格式文件！";
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

    return ResultDtoTool.buildSucceed("保存文件管理成功");
  }


  /**
   * 列表数据删除
   *
   * @param ftFileUpload
   * @param
   * @return
   */
  @PostMapping(value = "/delOne")
  public Object delOne(FtFileUpload ftFileUpload) {

    ftFileUploadService.delOne(ftFileUpload.getId());
    String uploadPath = ftFileUpload.getUploadPath();
    if (uploadPath == null) uploadPath = "";
    File file = new File(uploadPath);
    boolean delete = file.delete();
    if (delete) {
      return  ResultDtoTool.buildSucceed("删除文件管理成功");
    } else {
      return   ResultDtoTool.buildError("无此文件，请重新确认再进行删除操作！");
    }

  }

  /**
   *    获取数据节点下拉数据
   * @param ftFileUpload
   * @param
   * @param request
   * @param
   * @return
   */
  @GetMapping(value = "/sendLink")
  public Object sendLink(FtFileUpload ftFileUpload, HttpServletRequest request) {
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
    return  ResultDtoTool.buildSucceed(sendNodeList);
  }

  /**
   *   列表数据 发送
   * @param ftFileUpload
   * @param request
   * @return
   */
  @PostMapping(value = "/send")
  public Object send(FtFileUpload ftFileUpload, HttpServletRequest request) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    FtpPutThread ftpPutThread = null;
    FtFileUpload ftFileUploadTemp = ftFileUploadService.get(ftFileUpload.getId());
    if (null != ftServiceNode) {
      String nodeName = ftFileUpload.getSendNodeName();
      if (nodeName.isEmpty()) {
       return  ResultDtoTool.buildSucceed("数据节点不能为空");
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
      //addMessage(redirectAttributes, "版本发布中，请稍后点击详情查看结果");
      new Thread(ftpPutThread).start();
    }
    return   ResultDtoTool.buildSucceed("版本发布中，请稍后点击详情查看结果");
  }


  /**
   *  版本发布历史记录：
   *    列表数据
   * @param ftFileUploadLog
   * @return
   */
  @GetMapping(value = "/listLog")
  public Object listLog(FtFileUploadLog ftFileUploadLog,  HttpServletRequest request) {
   /* try {
      Page<FtFileUploadLog> page = ftFileUploadLogService.findPage(new Page<FtFileUploadLog>(request, response), ftFileUploadLog);
      ftFileUploadLog.setPage(page);
      model.addAttribute("page", page);
    } catch (Exception e) {
      addMessage(redirectAttributes, "查询信息失败！详情：" + e.getMessage());
      return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
    }
    return "modules/file/ftFileUploadListLog";*/

    List<FtFileUpload> listLog;
    int total = 0;
    Map<String, Object> resultMapLog = new HashMap<>();
    try {
      int pageNo = Integer.valueOf(request.getParameter("pageNo"));
      int pageSize = Integer.valueOf(request.getParameter("pageSize"));
      listLog = ftFileUploadService.getFtFileUploadList(pageNo, pageSize);
      total = ftFileUploadService.getFtFileUploadTotal();
      resultMapLog.put("list", listLog);
      resultMapLog.put("total", total);
      if (log.isDebugEnabled()) {
        log.debug("查询的版本发布历史记录列表: " + resultMapLog);
      }
    } catch (Exception e) {
      String message = "查询的版本发布历史记录列表失败！详情：" + e.getMessage();
      return  ResultDtoTool.buildError(message);
    }
    return ResultDtoTool.buildSucceed(resultMapLog);


  }

  @GetMapping(value = "/listOneLog")
  public Object listOneLog(FtFileUploadLog ftFileUploadLog) {

    FtFileUploadLog ftFileUploadLogTemp = null;
    try {
      ftFileUploadLogTemp = ftFileUploadLogService.get(ftFileUploadLog);
    } catch (Exception e) {
      return  ResultDtoTool.buildError("查询信息失败！详情："+ e.getMessage());
    }
    return ResultDtoTool.buildSucceed(ftFileUploadLogTemp);
  }

}
