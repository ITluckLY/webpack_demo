/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.user.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.serviceinfo.entity.BankSystem;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.UserImportsService;
import com.dc.smarteam.service.UserService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * 用户批量导入Controller
 *
 * @author hudja
 * @version 2017-09-11
 */
@Controller
@RequestMapping(value = "${adminPath}/user/ftUserImports")
public class FtUserImportsController extends BaseController {
    @Resource
    private UserImportsService userImportsService;
    @Resource
    private UserService userService;
    @Value("${localUploadFilePath}")
    private String localUploadFilePath;
    @Value("${localUploadFile}")
    private String localUploadFile;

    public static final String REDIRECT = "redirect:";

    @RequiresPermissions("user0:ftUserImports:view")
    @RequestMapping(value = {""})
    public String show(BankSystem bankSystem, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        List<BankSystem> banksystemlist = userService.getBanksystemlist();
        model.addAttribute("BankSystemList", banksystemlist);
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        return "modules/user/ftUserImports";
    }

    @RequiresPermissions("user0:ftUserImports:edit")
    @RequestMapping(value = {"save"})
    public String save(BankSystem bankSystem, @RequestParam("uploadFileName") MultipartFile file, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String nodesystemname = ftServiceNode.getSystemName();
        String tempName = String.valueOf(System.currentTimeMillis());
        String originalFilename = file.getOriginalFilename();

        int idx = originalFilename.lastIndexOf('.');
        String extention = originalFilename.substring(idx + 1);
        String fileTemp = originalFilename.substring(0, idx);

        if (extention.isEmpty() || (!extention.equalsIgnoreCase("csv"))) {
            addMessage(redirectAttributes, "文件上传不成功，注：只能上传.csv文件！");
            return REDIRECT + Global.getAdminPath() + "/user/ftUserImports"; //NOSONAR
        }

        StringBuilder sb = new StringBuilder(100).append(fileTemp)
                .append(tempName).append(".").append(extention);
        String path = localUploadFilePath;
        String fileName = sb.toString();
        File targetFile = new File(path, fileName);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            addMessage(redirectAttributes, "文件上传失败");
            return REDIRECT + Global.getAdminPath() + "/user/ftUserImports";
        }
        ResultDto resultDto = userImportsService.userImports(targetFile, nodesystemname, bankSystem.getBankname());
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "用户导入成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
            return REDIRECT + Global.getAdminPath() + "/user/ftUserImports";
        }
        return REDIRECT + Global.getAdminPath() + "/user/ftUser/?repage";
    }

    @RequiresPermissions("user0:ftUserImports:view")
    @RequestMapping(value = {"download"})
    public ResponseEntity<String> download(HttpServletRequest request,
                                           @RequestParam("filename") String filename) {
        logger.debug("进入下载页面:{}users_model.csv", Thread.currentThread().getContextClassLoader().getResource("").getPath());
        File file = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath() + "users_model.csv");
        ResponseEntity responseEntity = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            //下载显示的文件名，解决中文名称乱码问题
            String downloadFielName = new String(filename.getBytes("UTF-8"), "iso-8859-1");
            //通知浏览器以attachment（下载方式）打开文档
            headers.setContentDispositionFormData("attachment", downloadFielName);
            //application/octet-stream ： 二进制流数据（最常见的文件下载）。
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            if (!file.exists()) {
                String downerror = File.separator + filename + "文件不存在";
                logger.debug(downerror);
                return null;
            }
            String outputFileName = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "users_model_bom.csv";
            File outputFile = com.dc.smarteam.common.utils.FileUtils.convertUtf8WithBom(file, outputFileName);
            responseEntity = new ResponseEntity<>(FileUtils.readFileToString(outputFile, "UTF8"),
                    headers, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("", e);
        }
        return responseEntity;
    }

}