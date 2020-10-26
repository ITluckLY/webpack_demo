package com.dc.smarteam.modules.sysinfo.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.VsysmapModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sysinfo.entity.Vsysmap;
import com.dc.smarteam.service.VsysmapService;
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

/**
 * 系统映射Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/sysInfo/vsysmap")
public class VsysmapController extends BaseController {

    @Resource
    private VsysmapService vsysmapService;

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = {"list", ""})
    public String list(Vsysmap vsysmap, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        ResultDto<List<VsysmapModel.Map>> dto = vsysmapService.listAll();
        List<Vsysmap> vsysmapList = new ArrayList<>();
        if (ResultDtoTool.isSuccess(dto)) {
            List<VsysmapModel.Map> maps = dto.getData();
            for (VsysmapModel.Map map : maps) {
                Vsysmap vsysmap2 = new Vsysmap();
                CfgModelConverter.convertTo(map, vsysmap2);
                vsysmapList.add(vsysmap2);
            }
        } else {
            String data = dto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        PageHelper.getInstance().getPage(vsysmap.getClass(), request, response, model, vsysmapList);
        return "modules/vsysmap/list";
    }

    @RequestMapping(value = "addPage")
    public String addPage(Vsysmap vsysmap, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("vsysmap", vsysmap);
        return "modules/vsysmap/add";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "form")
    public String form(Vsysmap vsysmap, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        model.addAttribute("vsysmap", vsysmap);
        return "modules/vsysmap/form";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "save")
    public String save(Vsysmap vsysmap, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, vsysmap)) {
            return form(vsysmap, model, request, redirectAttributes);
        }
        ResultDto<String> dto = vsysmapService.add(vsysmap);
        if (ResultDtoTool.isSuccess(dto)) {
            addMessage(redirectAttributes, "保存成功");
        } else {
            addMessage(redirectAttributes, dto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/sysInfo/vsysmap/?repage";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:edit")
    @RequestMapping(value = "delete")
    public String delete(Vsysmap vsysmap, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> dto = vsysmapService.del(vsysmap);
        if (ResultDtoTool.isSuccess(dto)) {
            addMessage(redirectAttributes, "删除成功");
        } else {
            addMessage(redirectAttributes, dto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/sysInfo/vsysmap/?repage";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = {"confComp"})
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("vsysmap", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().vsysmap(new Vsysmap(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/vsysmap/confComp";
    }

    @RequiresPermissions("sysInfo:ftSysInfo:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }


}