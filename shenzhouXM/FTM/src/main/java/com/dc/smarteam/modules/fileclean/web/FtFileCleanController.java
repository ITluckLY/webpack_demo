/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.fileclean.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileCleanModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.CfgZkService;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;
import com.dc.smarteam.modules.fileclean.service.FtFileCleanService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.FileCleanService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件清理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/fileclean/ftFileClean")
public class FtFileCleanController extends BaseController {

    @Resource
    private FileCleanService fileCleanService;
    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private CfgZkService cfgZkService;

    @RequiresPermissions("fileclean:ftFileClean:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtFileClean ftFileClean, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) throws Exception {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ResultDto<List<FileCleanModel.FileClean>> resultDto = fileCleanService.listAll();
        List<FtFileClean> list = new ArrayList<FtFileClean>();
        List<FtFileClean> list2 = new ArrayList<FtFileClean>();
        List<FtFileClean> listTemp = new ArrayList<FtFileClean>();

        if (ResultDtoTool.isSuccess(resultDto)) {
            List<FileCleanModel.FileClean> filecleans = resultDto.getData();
            for (FileCleanModel.FileClean fileClean : filecleans) {
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                list.add(ftFileClean2);
            }

            for (FtFileClean ffc : list) {
                if (ffc.getSystem() != null && ffc.getSystem().equalsIgnoreCase(ftServiceNode.getSystemName())) {
                    listTemp.add(ffc);
                }
            }
            boolean targetDir = false;
            boolean state = false;
            if (ftFileClean.getTargetDir() != null && !"".equals(ftFileClean.getTargetDir())) {
                targetDir = true;
            }
            if (ftFileClean.getState() != null && !"".equals(ftFileClean.getState())) {
                state = true;
            }
            if (targetDir || state) {
                for (int i = 0; i < listTemp.size(); i++) {
                    FtFileClean ffc = listTemp.get(i);
                    if (targetDir && !ffc.getTargetDir().toLowerCase().contains(ftFileClean.getTargetDir().toLowerCase())) {
                        continue;
                    }
                    if (state && !ffc.getState().toLowerCase().contains(ftFileClean.getState().toLowerCase())) {
                        continue;
                    }
                    list2.add(listTemp.get(i));
                }
            } else {
                list2 = listTemp;
            }
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        PageHelper.getInstance().getPage(ftFileClean.getClass(), request, response, model, list2);
        return "modules/fileclean/ftFileCleanList";
    }

    @RequiresPermissions("fileclean:ftFileClean:view")
    @RequestMapping(value = "form")
    public String form(HttpServletRequest request, FtFileClean ftFileClean, RedirectAttributes redirectAttributes, Model model) {
        if (!(ftFileClean.getId() == null || ftFileClean.getId().equals(""))) {
            ResultDto<FileCleanModel.FileClean> resultDto = fileCleanService.selByID(ftFileClean);
            List<FtFileClean> list = new ArrayList<FtFileClean>();
            if (ResultDtoTool.isSuccess(resultDto)) {
                FileCleanModel.FileClean fileClean = resultDto.getData();
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                list.add(ftFileClean2);

                for (FtFileClean ffc : list) {
                    if (ffc.getId().equals(ftFileClean.getId())) {
                        ftFileClean = ffc;
                    }
                }
            } else {
                String data = resultDto.getMessage();
                addMessage(redirectAttributes, data);
                return "redirect:" + Global.getAdminPath() + "/fileclean/ftFileClean/?repage";
            }
        }
        model.addAttribute("ftFileClean", ftFileClean);
        return "modules/fileclean/ftFileCleanForm";
    }

    @RequiresPermissions("fileclean:ftFileClean:edit")
    @RequestMapping(value = "save")
    public String save(HttpServletRequest request, FtFileClean ftFileClean, Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        ResultDto<String> resultDto = null;
        if (StringUtils.isNoneEmpty(ftFileClean.getId())) {
            ResultDto<FileCleanModel.FileClean> resultDto1 = fileCleanService.selByID(ftFileClean);
            if (ResultDtoTool.isSuccess(resultDto1)) {
                FileCleanModel.FileClean fileClean = resultDto1.getData();
                ftFileClean.setState(fileClean.getState());
            }
            resultDto = fileCleanService.update(ftFileClean);
        } else {
            ftFileClean.setId(UUID.randomUUID().toString());
            ftFileClean.setState(FtFileClean.WAITING);
            resultDto = fileCleanService.add(ftFileClean, ftServiceNode);
        }

        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存文件清理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/fileclean/ftFileClean/?repage";
    }

    @RequiresPermissions("fileclean:ftFileClean:edit")
    @RequestMapping(value = "delete")
    public String delete(HttpServletRequest request, FtFileClean ftFileClean, RedirectAttributes redirectAttributes) {
        ResultDto<String> resultDto = fileCleanService.del(ftFileClean);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除文件清理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/fileclean/ftFileClean/?repage";
    }

    @RequiresPermissions("fileclean:ftFileClean:edit")
    @RequestMapping(value = "startFileClean")
    public String startFileClean(FtFileClean ftFileClean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (!(ftFileClean.getId() == null || ftFileClean.getId().equals(""))) {
            ResultDto<FileCleanModel.FileClean> resultDto = fileCleanService.selByID(ftFileClean);
            if (ResultDtoTool.isSuccess(resultDto)) {
                FileCleanModel.FileClean fileClean = resultDto.getData();
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                ftFileClean2.setState("1");
                ResultDto<String> resultDto2 = fileCleanService.update(ftFileClean2);
                if (ResultDtoTool.isSuccess(resultDto2)) {
                    String fileName = "file_clean.xml";
                    String sysname = ftServiceNode.getSystemName();
                    String content = getCurrCfgContent(sysname, fileName, true);
                    cfgZkService.write(sysname, fileName, content.trim());
                    addMessage(redirectAttributes, "同步文件清理成功");
                } else {
                    addMessage(redirectAttributes, "同步文件清理失败");
                }
            }
        }
        return "redirect:" + Global.getAdminPath() + "/fileclean/ftFileClean/?repage";
    }

    @RequiresPermissions("fileclean:ftFileClean:edit")
    @RequestMapping(value = "stopFileClean")
    public String stopFileClean(FtFileClean ftFileClean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        //---------后台缺少处理流程-----------
        if (!(ftFileClean.getId() == null || ftFileClean.getId().equals(""))) {
            ResultDto<FileCleanModel.FileClean> resultDto = fileCleanService.selByID(ftFileClean);
            if (ResultDtoTool.isSuccess(resultDto)) {
                FileCleanModel.FileClean fileClean = resultDto.getData();
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                ftFileClean2.setState("0");
                ResultDto<String> resultDto2 = fileCleanService.update(ftFileClean2);
                if (ResultDtoTool.isSuccess(resultDto2)) {
                    String fileName = "file_clean.xml";
                    String sysname = ftServiceNode.getSystemName();
                    String content = getCurrCfgContent(sysname, fileName, true);
                    cfgZkService.write(sysname, fileName, content.trim());
                    addMessage(redirectAttributes, "同步文件清理成功");
                } else {
                    addMessage(redirectAttributes, "同步文件清理失败");
                }
            }
        }
        return "redirect:" + Global.getAdminPath() + "/fileclean/ftFileClean/?repage";
    }


    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().fileClean(new FtFileClean(), "print");//生成查询报文
        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/fileclean/ftFileCleanOtherConf";
    }

    @RequiresPermissions("fileclean:ftFileClean:view")
    @RequestMapping(value = "confComp")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("file_clean", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().fileClean(new FtFileClean(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/fileclean/ftFileCleanConfComp";
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }

    private String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        return cfgFileService.getCurrCfgContent(sysname, fileName, hasTimestamp);
    }
}