package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.modules.file.entity.FtFileUploadLog;

import java.util.List;

public interface FtFileUploadLogService {

    FtFileUploadLog get(FtFileUploadLog entity);

    FtFileUploadLog get(String id);

    List<FtFileUploadLog> findAll(FtFileUploadLog ftFileUploadLog);

    void save(FtFileUploadLog ftFileUploadLog);

    int update(FtFileUploadLog ftFileUploadLog);

    int getFtFileRollbackLogTotal();
}
