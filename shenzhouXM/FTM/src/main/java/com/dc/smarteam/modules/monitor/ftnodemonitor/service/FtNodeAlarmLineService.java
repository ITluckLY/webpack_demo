/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeAlarmLineDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service
 *
 * @author lvchuan
 * @version 2016-06-26
 */
@Service
@Transactional(readOnly = true)
public class FtNodeAlarmLineService extends CrudService<FtNodeAlarmLineDao, FtNodeAlarmLine> {

    @Autowired
    private FtNodeAlarmLineDao ftNodeAlarmLineDao;

    public FtNodeAlarmLine get(String id) {
        return super.get(id);
    }

}