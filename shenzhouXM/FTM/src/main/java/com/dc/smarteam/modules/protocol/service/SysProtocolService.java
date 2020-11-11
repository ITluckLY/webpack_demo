/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.protocol.service;

import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.protocol.dao.SysProtocolDao;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通讯协议管理Service
 *
 * @author kern
 * @version 2015-12-24
 */
@Service
@Transactional(readOnly = true)
public class SysProtocolService extends CrudService<SysProtocolDao, SysProtocol> {

    private SysProtocolDao sysProtocolDao;

    public SysProtocol get(String id) {
        return super.get(id);
    }

    public List<SysProtocol> findList(SysProtocol sysProtocol) {
        return super.findList(sysProtocol);
    }

    public Page<SysProtocol> findPage(Page<SysProtocol> page, SysProtocol sysProtocol) {
        return super.findPage(page, sysProtocol);
    }

    @Transactional(readOnly = false)
    public void save(SysProtocol sysProtocol) {
        super.save(sysProtocol);
    }

    @Transactional(readOnly = false)
    public void delete(SysProtocol sysProtocol) {
        super.delete(sysProtocol);
    }

}