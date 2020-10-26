package com.dc.smarteam.modules.ftinterface.web;


import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.ftinterface.enity.FtInterface;
import com.dc.smarteam.modules.ftinterface.service.FtInterfaceService;
import com.dc.smarteam.tool.MessDealTool;
import com.dc.smarteam.tool.ResultDtoTools;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.Socket;

/**
 * 对外服务Controller
 *
 * Created by zhenghe on 2019/12/3.
 */
@Controller
@RequestMapping(value = "ftinterface")
public class ServiceController {

    // 操作目标 userid passid
    public static final String USER_PASSWD = "user";
    // 操作目标 userid serviceid
    public static final String USER_SERVICE = "service";
    // 操作目标  RSA
    public static final String RSA = "rsa";
    // 操作目标 获取全量服务信息
    public static final String SERVICE_FILE = "servicefile";
    // 操作目标 获取全量服务信息
    public static final String MSG = "msg";
    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);
    private Socket socket;
    @Autowired
    private FtInterfaceService ftInterfaceService;

    @ResponseBody
    @RequestMapping(value = "service", method = RequestMethod.POST)
    public String service(@RequestBody String msg) throws IOException {
        log.info(msg);
        JSONObject jsonObject = JSONObject.fromObject(msg);
        FtInterface ftInterface = (FtInterface)JSONObject.toBean(jsonObject,FtInterface.class);
        ResultDto resultDto = null;


        if (ftInterface.getTarget().equals(USER_PASSWD)) {

            log.info(USER_PASSWD+"开始执行");
            resultDto = ftInterfaceService.user(ftInterface);

        } else if (ftInterface.getTarget().equals(USER_SERVICE)) {

            log.info(USER_SERVICE+"开始执行");

             resultDto = ftInterfaceService.service(ftInterface);

        } else if (ftInterface.getTarget().equals(RSA)) {

            log.info(RSA+"开始执行");
             resultDto = ftInterfaceService.rsa(ftInterface);

        } else if (ftInterface.getTarget().equals(SERVICE_FILE)) {

            log.info(SERVICE_FILE+"开始执行");

             resultDto = ftInterfaceService.serviceInfo(ftInterface);

        } else if (ftInterface.getTarget().equals(MSG)){

            log.info(MSG+"开始执行");

            resultDto = ftInterfaceService.msg(ftInterface);
        } else {
            MessDealTool.sendBackMes(ResultDtoTools.buildError("不能识别报文信息"), socket);
        }

        return ResultDtoTools.toJson(resultDto);

    }

}





