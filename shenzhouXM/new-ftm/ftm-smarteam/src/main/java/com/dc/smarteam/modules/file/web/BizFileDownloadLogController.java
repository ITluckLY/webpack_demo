package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.file.entity.BizFile;
import com.dc.smarteam.modules.file.entity.BizFileDownloadLog;
import com.dc.smarteam.modules.file.service.impl.BizFileDownloadLogServiceImpl;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Controller
@RequestMapping(value = "${adminPath}/file/bizFileDownloadLog")
public class BizFileDownloadLogController {

    @Autowired
    private BizFileDownloadLogServiceImpl bizFileDownloadLogService;

    @ModelAttribute
    public BizFileDownloadLog get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return bizFileDownloadLogService.get(id);
        } else {
            return new BizFileDownloadLog();
        }
    }

    @RequiresPermissions("file:bizFileDownloadLog:view")
    @RequestMapping(value = {"list"})
    public String list(BizFile bizFile, HttpServletRequest request, HttpServletResponse response, Model model) {
        return null;
    }

}
