/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.servicenode.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.servicenode.dao.FtServiceNodeDao;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 节点管理Service
 *
 * @author liwang
 * @version 2016-01-11
 */
@Service
@Transactional(readOnly = true)
public class FtServiceNodeService extends CrudService<FtServiceNodeDao, FtServiceNode> {

    public FtServiceNode get(String id) {
        return super.get(id);
    }

    //20160714通过name获取node对象,要求name是唯一的
//      public FtServiceNode getByName(String name){return super.getByName(name);}


    public List<FtServiceNode> findList(FtServiceNode ftServiceNode) {
        return super.findList(ftServiceNode);
    }

    public Page<FtServiceNode> findPage(Page<FtServiceNode> page, FtServiceNode ftServiceNode) {
        return super.findPage(page, ftServiceNode);
    }

	/*public Page<FtServiceNode> findAllPage(Page<FtServiceNode> page, FtServiceNode ftServiceNode) {
        return super.findAllPage(page, ftServiceNode);
	}*/


    @Transactional(readOnly = false)
    public void save(FtServiceNode ftServiceNode) {
        super.save(ftServiceNode);
    }

    @Transactional(readOnly = false)
    public void delete(FtServiceNode ftServiceNode) {
        super.delete(ftServiceNode);
    }


}