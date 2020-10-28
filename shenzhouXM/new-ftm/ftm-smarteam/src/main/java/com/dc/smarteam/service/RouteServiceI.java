package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;

/**
 * @Author: gaoyang
 * @Date: 2020/10/26  9:51
 * @Description:
 */

public interface RouteServiceI {
    RouteModel loadModel();

    ResultDto<String> addRouteByGetAuth(GetAuthEntity getAuthEntity);

    ResultDto<String> del(FtServiceInfo ftServiceInfo);

    ResultDto<String> delByTranscode(FtRoute ftRoute);
}
