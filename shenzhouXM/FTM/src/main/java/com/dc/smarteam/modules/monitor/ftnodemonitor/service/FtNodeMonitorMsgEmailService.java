/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeMonitorMsgEmailDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorMsgEmail;
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
public class FtNodeMonitorMsgEmailService extends CrudService<FtNodeMonitorMsgEmailDao, FtNodeMonitorMsgEmail> {

    @Autowired
    private FtNodeMonitorMsgEmailDao ftNodeMonitorMsgEmailDao;

    public FtNodeMonitorMsgEmail get(String id) {
        return super.get(id);
    }

//	@Transactional(readOnly = false)
//	public void save(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail) {
//		super.save(ftNodeMonitorMsgEmail);
//	}


}