/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.web;

import com.dc.smarteam.cfgmodel.*;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.modules.serviceinfo.entity.BankSystem;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceFlowVo;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.*;
import com.dc.smarteam.service.impl.ServiceInfoServiceImpl;
import com.dc.smarteam.service.impl.SysServiceImpl;
import com.dc.smarteam.service.impl.UserServiceImpl;
import com.dc.smarteam.util.CollectionUtil;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务管理Controller

 */
@Controller
@RequestMapping(value = "${adminPath}/serviceinfo/ftServiceImports")
public class FtServiceImportsController extends BaseController {

    @Resource
    private ServiceInfoImportsService serviceImportsService;

    @Resource
    private ServiceInfoServiceImpl serviceInfoService;
    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;
    @Resource
    private SysServiceImpl sysService;

    @Resource
    private FlowService flowService;

    @Value("${localUploadFilePath}")
    private String localUploadFilePath;
    @Value("${localUploadFile}")
    private String localUploadFile;
    @Value("${localDownFilePath}")
    private String localDownFilePath;
    private static final String REDIRECT = "redirect:";

    @RequestMapping(value = {""})
    public Object show( HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return  ResultDtoTool.buildError("请先设置节点组！！！");
        }
        Map<String,Object> res= new HashMap<>();
        List<BankSystem> banksystemlist = userService.getBanksystemlist();
        res.put("BankSystemList", banksystemlist);
        return ResultDtoTool.buildSucceed(res);
    }

    @RequestMapping(value = "addOneServiceFlow")
    public Object showAddPage(HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return  ResultDtoTool.buildError("请先设置节点组！！！");
        }
        Map<String,Object> res= new HashMap<>();
        ResultDto<List<ServiceModel.Service>> resultDto = serviceInfoService.listAll();
        ResultDto<List<UserModel.UserInfo>> userInfoDto = userService.listAll();
        ResultDto<List<SystemModel.System>> systemDto = sysService.listAll();

        List<String> tranCodeList = new ArrayList<>();
        for (ServiceModel.Service service : resultDto.getData()) {
            if (service.getSysname().equals(ftServiceNode.getSystemName())) {
                tranCodeList.add(service.getTrancode());
            }
        }
        res.put("tranCodeList", tranCodeList);

        ResultDto<List<FlowModel.Flow>> resultDto2 = flowService.selBySysname(ftServiceNode.getSystemName());
        List<FtFlow> ftFlowList = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto2)) {
            List<FlowModel.Flow> flows = resultDto2.getData();
            for (FlowModel.Flow flow : flows) {
                FtFlow ftFlow = new FtFlow();
                CfgModelConverter.convertTo(flow, ftFlow);
                ftFlowList.add(ftFlow);
            }
        }
        res.put("ftFlowList", ftFlowList);

        List<String> providerList = null;
        List<String> consumerList = null;

        try {
            providerList = CollectionUtil.freeTurnList(userInfoDto.getData(), "getUid", "getUid");
            consumerList = CollectionUtil.freeTurnList(systemDto.getData(), "getUsername");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        res.put("providerList", providerList);
        res.put("consumerList", consumerList);
        return  ResultDtoTool.buildSucceed(res);
    }

    @RequestMapping(value = {"add"})
    public Object addOneServiceFlow(FtServiceFlowVo ftServiceFlowVo, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logger.info("新增交易：{}", ftServiceFlowVo.toString());
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String nodesystemname = ftServiceNode.getSystemName();
        if (null == nodesystemname || StringUtils.equals(nodesystemname, "")) {
            //addMessage(redirectAttributes, "系统名称不能为空！！！");
            return  ResultDtoTool.buildError("系统名称不能为空！！！");
        }
        String mess="";
        try {
            ResultDto resultDto = serviceImportsService.addServiceFlow(ftServiceFlowVo, nodesystemname);
            if (ResultDtoTool.isSuccess(resultDto)) {
                logger.info("添加结束，成功");
                mess= "服务添加成功";
            } else {
                logger.error("添加结束，失败");
                mess=resultDto.getMessage();
            }
        } catch (Exception e) {
            logger.error("添加交易码服务流程失败:{}", e.getMessage());
            //addMessage(redirectAttributes, "添加交易码服务流程失败:"+e.getMessage());
            return  ResultDtoTool.buildError("添加交易码服务流程失败:"+e.getMessage());
        }

        return ResultDtoTool.buildSucceed(mess);
    }

    @RequestMapping(value = {"save"})
    public Object save(BankSystem bankSystem, @RequestParam("uploadFileName") MultipartFile file,  HttpServletRequest request) {
        String tempName = String.valueOf(System.currentTimeMillis());
        String originalFilename = file.getOriginalFilename();


        int idx = originalFilename.lastIndexOf('.');
        String extention = originalFilename.substring(idx + 1);
        String fileTemp = originalFilename.substring(0, idx);
        if (extention.isEmpty() || (!extention.equalsIgnoreCase("csv"))) {
            //addMessage(redirectAttributes, "文件上传不成功，注：只能上传.csv文件！");
            return ResultDtoTool.buildError("文件上传不成功，注：只能上传.csv文件！");
        }

        StringBuilder sb = new StringBuilder(100).append(fileTemp).append(tempName).append(".").append(extention);
        String path = localUploadFilePath;
        String fileName = sb.toString();
        File targetFile = new File(path, fileName);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            logger.error("文件传输失败", e);
        }

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String nodesystemname = ftServiceNode.getSystemName();
        if (null == nodesystemname || StringUtils.equals(nodesystemname, "")) {
            //addMessage(redirectAttributes, "系统名称不能为空！！！");
            return  ResultDtoTool.buildError("系统名称不能为空！！！");
        }
        ResultDto resultDto = serviceImportsService.serviceImports(targetFile, nodesystemname, bankSystem.getBankname());
        String messages = "";
        if (ResultDtoTool.isSuccess(resultDto)) {
            messages =  "服务导入成功";
        } else {
            //addMessage(redirectAttributes, resultDto.getMessage());
           // return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceImports";
            return  ResultDtoTool.buildError(resultDto.getMessage());
        }
        return  ResultDtoTool.buildSucceed(messages);
    }

    @RequestMapping(value = {"download"})
    public Object download(@RequestParam("filename") String filename) {
        logger.debug("进入下载页面:{}{}{}", Thread.currentThread().getContextClassLoader().getResource("").getPath(), File.separator, filename);
        File file = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath() + "codes_model_bak.csv");
        ResponseEntity responseEntity = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            //下载显示的文件名，解决中文名称乱码问题
            String downloadFielName = new String(filename.getBytes("UTF-8"), "iso-8859-1");
            //通知浏览器以attachment（下载方式）打开图片
            headers.setContentDispositionFormData("attachment", downloadFielName);
            //application/octet-stream ： 二进制流数据（最常见的文件下载）。
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            if (!file.exists()) {
                String downerror = File.separator + filename + "文件不存在";
                logger.debug(downerror);
                return null;
            }
            String outputFileName = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "codes_model_bom.csv";
            File outputFile = com.dc.smarteam.common.utils.FileUtils.convertUtf8WithBom(file, outputFileName);
            responseEntity = new ResponseEntity<>(FileUtils.readFileToString(outputFile, "UTF8"),
                    headers, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("", e);
        }
        return  ResultDtoTool.buildSucceed(responseEntity);

    }

    @RequestMapping(value = {"editOneServiceFlow"})
    public Object showEditPage(HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        Map<String,Object> result =new HashMap<>();
        ResultDto<List<ServiceModel.Service>> resultDto = serviceInfoService.listAll();
        ResultDto<List<UserModel.UserInfo>> userInfoDto = userService.listAll();
        ResultDto<List<SystemModel.System>> systemDto = sysService.listAll();
        List<String> tranCodeList = new ArrayList<>();
        for (ServiceModel.Service service : resultDto.getData()) {
            if (service.getSysname().equals(ftServiceNode.getSystemName())) {
                tranCodeList.add(service.getTrancode());
            }
        }
        result.put("tranCodeList", tranCodeList);
        ResultDto<List<FlowModel.Flow>> resultDto2 = flowService.selBySysname(ftServiceNode.getSystemName());
        List<FtFlow> ftFlowList = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto2)) {
            List<FlowModel.Flow> flows = resultDto2.getData();
            for (FlowModel.Flow flow : flows) {
                FtFlow ftFlow = new FtFlow();
                CfgModelConverter.convertTo(flow, ftFlow);
                ftFlowList.add(ftFlow);
            }
        }
        result.put("ftFlowList", ftFlowList);
        List<String> providerList = null;
        List<String> consumerList = null;

        try {
            providerList = CollectionUtil.freeTurnList(userInfoDto.getData(), "getUid", "getUid");
            consumerList = CollectionUtil.freeTurnList(systemDto.getData(), "getUsername");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        result.put("providerList", providerList);
        result.put("consumerList", consumerList);
        return ResultDtoTool.buildSucceed(result);
    }


    @RequestMapping(value = {"edit"})
    public Object updateOneServiceFlow(FtServiceFlowVo ftServiceFlowVo, HttpServletRequest request) {

        logger.info("修改交易：{}", ftServiceFlowVo.toString());

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String nodesystemname = ftServiceNode.getSystemName();
        if (null == nodesystemname || StringUtils.equals(nodesystemname, "")) {
            //addMessage(redirectAttributes, "系统名称不能为空！！！");
            return  ResultDtoTool.buildError("系统名称不能为空！！！");
        }

        String mess="";
        try {
            ResultDto resultDto = serviceImportsService.addServiceFlow(ftServiceFlowVo, nodesystemname);
            if (ResultDtoTool.isSuccess(resultDto)) {
                logger.info("修改结束，成功");
                mess= "服务修改成功";
            } else {
                logger.error("修改结束，失败");
                mess=  resultDto.getMessage();
            }
        } catch (Exception e) {
            logger.error("修改交易码服务流程失败:{}", e.getMessage());
            return  ResultDtoTool.buildError("修改交易码服务流程失败:{}"+ e.getMessage());
        }

        return ResultDtoTool.buildError(mess);
    }

    @ResponseBody
    @RequestMapping(value = {"getService"})
    public ServiceModel.Service getService(String tranCode) {
        ResultDto<List<ServiceModel.Service>> resultDto = serviceInfoService.listAll();
        for (ServiceModel.Service service : resultDto.getData()) {
            if (service.getTrancode().equals(tranCode)) {
                return service;
            }
        }
        return new ServiceModel.Service();
    }
}