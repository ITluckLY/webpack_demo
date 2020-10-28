package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;

import java.util.List;

/**
 * @Author: gaoyang
 * @Date: 2020/10/23  16:26
 * @Description:
 */

public interface ServiceInfoServiceI {
    List<FtServiceInfo> getFtServiceInfoList();

    ServiceModel loadModel();

    ResultDto<List<ServiceModel.Service>> listAll();

    ResultDto<ServiceModel.Service> selByTrancodeAndSysname(FtServiceInfo ftServiceInfo);

    ResultDto<String> savePutAuth(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity);

    ResultDto<String> add(FtServiceInfo ftServiceInfo);

    ResultDto<String> update(FtServiceInfo ftServiceInfo);

    ResultDto<String> delPutAuth(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity);

    ResultDto<String> saveGetAuth(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity);

    ResultDto<String> delGetAuth(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity);

    ResultDto<String> del(FtServiceInfo ftServiceInfo);

    ResultDto<String> serviceInfoExport();

}
