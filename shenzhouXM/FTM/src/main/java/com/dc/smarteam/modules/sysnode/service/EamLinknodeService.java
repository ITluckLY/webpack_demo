/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysnode.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.sysnode.dao.EamLinknodeDao;
import com.dc.smarteam.modules.sysnode.entity.EamLinknode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统关联节点Service
 *
 * @author yangqjb
 * @version 2015-12-28
 */
@Service
@Transactional(readOnly = true)
public class EamLinknodeService extends CrudService<EamLinknodeDao, EamLinknode> {

    public EamLinknode get(String id) {
        return super.get(id);
    }

    public List<EamLinknode> findList(EamLinknode eamLinknode) {
        return super.findList(eamLinknode);
    }

    public Page<EamLinknode> findPage(Page<EamLinknode> page, EamLinknode eamLinknode) {
        return super.findPage(page, eamLinknode);
    }

    @Transactional(readOnly = false)
    public void save(EamLinknode eamLinknode) {
        super.save(eamLinknode);
    }

    @Transactional(readOnly = false)
    public void delete(EamLinknode eamLinknode) {
        super.delete(eamLinknode);
    }

}