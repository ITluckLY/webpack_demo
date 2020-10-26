package com.dc.smarteam.modules.monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by vincentfxz on 16/1/13.
 */

@Controller
@RequestMapping(value = "${adminPath}/monitor")
public class FtMonitorController {
    @RequestMapping(value = {"index", ""})
    public String list() {
        return "modules/monitor/index";
    }

}
