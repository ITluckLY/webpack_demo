package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.modules.file.entity.FtFileRollbackLog;

import java.util.List;

public interface FtFileRollbackLogService {

    public List<FtFileRollbackLog> getFtFileRollbackLogList(int pageNo, int pageSize);

    public int getFtFileRollbackLogTotal();

    void save(FtFileRollbackLog ftFileRollbackLog);
}
