/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.ipconfig.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.ipconfig.dao.FtUserIpDao;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * IP控制Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class FtUserIpService extends CrudService<FtUserIpDao, FtUserIp> {

    public FtUserIp get(String id) {
        return super.get(id);
    }

    public List<FtUserIp> findList(FtUserIp ftUserIp) {
        return super.findList(ftUserIp);
    }

    public Page<FtUserIp> findPage(Page<FtUserIp> page, FtUserIp ftUserIp) {
        return super.findPage(page, ftUserIp);
    }

    @Transactional(readOnly = false)
    public void save(FtUserIp ftUserIp) {
        super.save(ftUserIp);
    }

    @Transactional(readOnly = false)
    public void delete(FtUserIp ftUserIp) {
        super.delete(ftUserIp);
    }

}