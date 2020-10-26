package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.file.entity.BizFileUploadLog;
import com.dc.smarteam.modules.file.service.impl.BizFileUploadLogServiceImpl;
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
@RequestMapping(value = "${adminPath}/file/bizFileUploadLog")
public class BizFileUploadLogController {
    @Autowired
    private BizFileUploadLogServiceImpl bizFileUploadLogService;

    @ModelAttribute
    public BizFileUploadLog get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return bizFileUploadLogService.get(id);
        } else {
            return new BizFileUploadLog();
        }
    }

    @RequiresPermissions("file:bizFileUploadLog:view")
    @RequestMapping(value = {"list"})
    public String list(BizFileUploadLog bizFileUploadLog, HttpServletRequest request, HttpServletResponse response, Model model) {
        return null;
    }


}
