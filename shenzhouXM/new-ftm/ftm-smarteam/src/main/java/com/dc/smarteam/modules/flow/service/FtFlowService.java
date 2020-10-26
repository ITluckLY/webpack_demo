/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.flow.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.flow.dao.FtFlowDao;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 流程管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class FtFlowService extends CrudService<FtFlowDao, FtFlow> {

    public FtFlow get(String id) {
        return super.get(id);
    }

    public List<FtFlow> findList(FtFlow ftFlow) {
        return super.findList(ftFlow);
    }

    public Page<FtFlow> findPage(Page<FtFlow> page, FtFlow ftFlow) {
        return super.findPage(page, ftFlow);
    }

    @Transactional(readOnly = false)
    public void save(FtFlow ftFlow) {
        super.save(ftFlow);
    }

    @Transactional(readOnly = false)
    public void delete(FtFlow ftFlow) {
        super.delete(ftFlow);
    }

}