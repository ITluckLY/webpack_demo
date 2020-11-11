package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileRenameModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.file.entity.FtFileRename;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.FileRenameService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by huangzbb on 2016/8/12.
 */
@Controller
@RequestMapping(value = "${adminPath}/file/ftFileRename")
public class FileRenameController extends BaseController {

    @Resource
    private FileRenameService fileRenameService;

    @RequiresPermissions("file:ftFileRename:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtFileRename ftFileRename, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ResultDto<List<FileRenameModel.FileRename>> resultDto = fileRenameService.listAll();
        List<FtFileRename> list = new ArrayList<FtFileRename>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<FileRenameModel.FileRename> fileRenames = resultDto.getData();
            String type = ftFileRename.getType();
            for (FileRenameModel.FileRename fileRename : fileRenames) {
                if (!StringUtils.equalsIgnoreCase(fileRename.getSysname(), ftServiceNode.getSystemName())) continue;
                if (!StringUtils.isEmpty(type) && !StringUtils.equalsIgnoreCase(fileRename.getType(), type)) continue;
                FtFileRename ftFileRename2 = new FtFileRename();
                CfgModelConverter.convertTo(fileRename, ftFileRename2);

                list.add(ftFileRename2);
            }
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        PageHelper.getInstance().getPage(ftFileRename.getClass(), request, response, model, list);
        return "modules/file/ftFileRenameList";
    }

    @RequiresPermissions("file:ftFileRename:view")
    @RequestMapping(value = {"form"})
    public String form(FtFileRename ftFileRename, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.isNoneEmpty(ftFileRename.getId())) {
            ResultDto<FileRenameModel.FileRename> resultDto = fileRenameService.selbyIDorType(ftFileRename);
            if (ResultDtoTool.isSuccess(resultDto)) {
                FileRenameModel.FileRename fileRename = resultDto.getData();
                if (fileRename.getId().equals(ftFileRename.getId()))
                    CfgModelConverter.convertTo(fileRename, ftFileRename);
            } else {
                String data = resultDto.getMessage();
                addMessage(redirectAttributes, data);
                return "redirect:" + Global.getAdminPath() + "/file/ftFileRename/?repage";
            }
        }
        model.addAttribute("ftFileRename", ftFileRename);
        return "modules/file/ftFileRenameForm";
    }

    @RequiresPermissions("file:ftFileRename:edit")
    @RequestMapping(value = "save")
    public String save(HttpServletRequest request, FtFileRename ftFileRename, Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        ResultDto<String> resultDto;
        if (StringUtils.isEmpty(ftFileRename.getId())) {
            ftFileRename.setId(UUID.randomUUID().toString());
            resultDto = fileRenameService.add(ftFileRename, ftServiceNode);
        } else {
            resultDto = fileRenameService.update(ftFileRename);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存文件重命名成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/file/ftFileRename/list?type=" + ftFileRename.getType();

    }

    @RequiresPermissions("file:ftFileRename:edit")
    @RequestMapping(value = "delete")
    public String delete(HttpServletRequest request, FtFileRename ftFileRename, RedirectAttributes redirectAttributes) {
        ResultDto<String> resultDto = fileRenameService.del(ftFileRename);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除文件重命名成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/file/ftFileRename/list?type=" + ftFileRename.getType();
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().fileRename(new FtFileRename(), "print");//生成查询报文
        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/file/ftFileRenameOtherConf";
    }

    @RequiresPermissions("file:ftFileRename:view")
    @RequestMapping(value = {"confComp"})
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("file_rename", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().fileRename(new FtFileRename(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/file/ftFileRenameConfComp";
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }


}
