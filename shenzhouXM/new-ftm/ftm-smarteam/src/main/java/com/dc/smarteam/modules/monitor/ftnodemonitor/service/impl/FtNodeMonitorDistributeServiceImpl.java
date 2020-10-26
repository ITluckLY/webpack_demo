/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service.impl;


import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeMonitorDistributeMapper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorDistribute;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorDistributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 节点监控Service
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Service("FtNodeMonitorDistributeServiceImpl")
@Transactional(readOnly = true)
public class FtNodeMonitorDistributeServiceImpl extends CrudService<FtNodeMonitorDistributeMapper, FtNodeMonitorDistribute> implements FtNodeMonitorDistributeService {

    @Autowired
    private FtNodeMonitorDistributeMapper ftNodeMonitorDistributeDao;

    public List<FtNodeMonitorDistribute> findDistributeList(){
        return super.findList(new FtNodeMonitorDistribute());
    }

}