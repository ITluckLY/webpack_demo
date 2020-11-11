package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.file.entity.BizFile;
import com.dc.smarteam.modules.file.service.BizFileService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Controller
@RequestMapping(value = "${adminPath}/file/bizFile")
public class BizFileController {
    @Autowired
    private BizFileService bizFileService;

    @ModelAttribute
    public BizFile get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return bizFileService.get(id);
        } else {
            return new BizFile();
        }
    }

    @RequiresPermissions("file:bizFile:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizFile bizFile, HttpServletRequest request, HttpServletResponse response, Model model) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        List<String> nodeNameList = bizFileService.findNodeNameList();
        model.addAttribute("nodeNameList", nodeNameList);

        Page<BizFile> page = bizFileService.findPage(new Page<BizFile>(request, response), bizFile);
        bizFile.setPage(page);
//        List<BizFile> list = bizFileService.findList(bizFile);
        model.addAttribute("page", page);
        return "modules/file/bizFileList";
    }

    @RequiresPermissions("file:bizFile:view")
    @RequestMapping(value = "form")
    public String form(BizFile bizFile, Model model) {
        model.addAttribute("bizFile", bizFile);
        return "modules/file/bizFileForm";
    }

}
