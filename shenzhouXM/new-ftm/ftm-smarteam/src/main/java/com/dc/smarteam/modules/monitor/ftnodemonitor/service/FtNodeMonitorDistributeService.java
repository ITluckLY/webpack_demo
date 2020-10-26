package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorDistribute;

/**
 * @Author: gaoyang
 * @Date: 2020/10/23  18:33
 * @Description:
 */

public interface FtNodeMonitorDistributeService {
    Page<FtNodeMonitorDistribute> findPage(Page<FtNodeMonitorDistribute> ftNodeMonitorDistributePage, FtNodeMonitorDistribute ftNodeMonitorDistribute);

    FtNodeMonitorDistribute get(FtNodeMonitorDistribute ftNodeMonitorDistribute);

}
