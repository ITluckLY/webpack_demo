/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.filebroadcast.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.filebroadcast.dao.FtFileBroadcastDao;
import com.dc.smarteam.modules.filebroadcast.entity.FtFileBroadcast;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件广播Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class FtFileBroadcastService extends CrudService<FtFileBroadcastDao, FtFileBroadcast> {

    public FtFileBroadcast get(String id) {
        return super.get(id);
    }

    public List<FtFileBroadcast> findList(FtFileBroadcast ftFileBroadcast) {
        return super.findList(ftFileBroadcast);
    }

    public Page<FtFileBroadcast> findPage(Page<FtFileBroadcast> page, FtFileBroadcast ftFileBroadcast) {
        return super.findPage(page, ftFileBroadcast);
    }

    @Transactional(readOnly = false)
    public void save(FtFileBroadcast ftFileBroadcast) {
        super.save(ftFileBroadcast);
    }

    @Transactional(readOnly = false)
    public void delete(FtFileBroadcast ftFileBroadcast) {
        super.delete(ftFileBroadcast);
    }

}