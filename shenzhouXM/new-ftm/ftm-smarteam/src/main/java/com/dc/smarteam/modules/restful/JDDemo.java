package com.dc.smarteam.modules.restful;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2020/9/29.
 */

@RestController
@RequestMapping("/JD")
public class JDDemo {

    @Value("${jdbc.type}")
    private String jdbcType;

    @GetMapping(value = "/queryGet",produces = "application/json;charset=UTF-8")
    public String queryGet(){
        return "123456";
    }
    @PostMapping(value = "/queryPost",produces = "application/json;charset=UTF-8")
    public String queryPost(@RequestBody String reqStr){
        return "654321";
    }

    @GetMapping(value = "/queryGet2",produces = "application/json;charset=UTF-8")
    public String queryGet2(){
        return "123456===>"+jdbcType;
    }
}

