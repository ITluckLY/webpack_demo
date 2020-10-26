package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.BizFileFlowNoLogDao;
import com.dc.smarteam.modules.file.entity.BizFileFlowNoLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Service
@Transactional(readOnly = true)
public class BizFileFlowNoLogService extends CrudService<BizFileFlowNoLogDao, BizFileFlowNoLog> {

}
