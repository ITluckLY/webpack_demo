package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLine;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeAlarmLineService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * 系统设置-告警配置-告警名单
 * <p>
 * Created by huangzbb on 2017/11/2.
 */
@RestController
@RequestMapping(value = "${adminPath}/monitor/FtNodeMonitor")
public class AlarmLineController extends BaseController {

    @Resource(name = "FtNodeAlarmLineServiceImpl")
    private FtNodeAlarmLineService ftNodeAlarmLineService;

    @RequestMapping(value = "saveAlarmLineList", produces = "application/json;charset=UTF-8")
    public ResultDto saveAlarmLineList(FtNodeAlarmLine ftNodeAlarmLine) {
        String message = "";
        int change = ftNodeAlarmLineService.saveAlarmLineList(ftNodeAlarmLine);
        if(change>0){
            message = "保存成功";
        }
        return ResultDtoTool.buildSucceed(message,null);
    }

}
