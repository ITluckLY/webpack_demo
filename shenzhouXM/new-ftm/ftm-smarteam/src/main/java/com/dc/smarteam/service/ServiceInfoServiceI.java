package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;

import java.util.List;

/**
 * @Author: gaoyang
 * @Date: 2020/10/23  16:26
 * @Description:
 */

public interface ServiceInfoServiceI {
    List<FtServiceInfo> getFtServiceInfoList();

    ServiceModel loadModel();

}
