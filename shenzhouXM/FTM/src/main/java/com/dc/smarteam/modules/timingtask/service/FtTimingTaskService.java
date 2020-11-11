/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.timingtask.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.timingtask.dao.FtTimingTaskDao;
import com.dc.smarteam.modules.timingtask.entity.FtTimingTask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务Service
 *
 * @author liwang
 * @version 2016-01-11
 */
@Service
@Transactional(readOnly = true)
public class FtTimingTaskService extends CrudService<FtTimingTaskDao, FtTimingTask> {

    public FtTimingTask get(String id) {
        return super.get(id);
    }

    public List<FtTimingTask> findList(FtTimingTask ftTimingTask) {
        return super.findList(ftTimingTask);
    }

    public Page<FtTimingTask> findPage(Page<FtTimingTask> page, FtTimingTask ftTimingTask) {
        return super.findPage(page, ftTimingTask);
    }

    @Transactional(readOnly = false)
    public void save(FtTimingTask ftTimingTask) {
        super.save(ftTimingTask);
    }

    @Transactional(readOnly = false)
    public void delete(FtTimingTask ftTimingTask) {
        super.delete(ftTimingTask);
    }

}