package com.dc.smarteam.modules.client.service;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ClientStatusModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.client.entity.FtClientStatus;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.ClientStatusService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuchuang on 2018/6/13.
 */
public class FtClientStatusService {

    @Resource
    private ClientStatusService clientStatusService;

}
