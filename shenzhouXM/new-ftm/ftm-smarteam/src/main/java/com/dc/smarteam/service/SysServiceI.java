package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;

import java.util.List;

/**
 * @Author: gaoyang
 * @Date: 2020/10/26  10:02
 * @Description:
 */

public interface SysServiceI {
    SystemModel loadModel();

    ResultDto del(SysProtocol sysProtocol);

    ResultDto add(SysProtocol sysProtocol);

    ResultDto<List<SystemModel.System>> listAll();
}
