package com.dc.smarteam.modules.demo.controller;


import com.dc.smarteam.modules.demo.entity.FtSysInfos;
import com.dc.smarteam.modules.demo.service.DemoService;
import com.dc.smarteam.modules.demo.service.impl.DemoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource(name = "DemoServiceImpl")
    private DemoService demoService;

    @RequestMapping("/reqDemo/{name}")
    public FtSysInfos reqDemo(@PathVariable("name") String name){
        return demoService.getFtSysInfoByName(name);
    }
}
