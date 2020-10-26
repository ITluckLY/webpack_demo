/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.flow.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.flow.dao.RuleDao;
import com.dc.smarteam.modules.flow.entity.Rule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 流程管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class RuleService extends CrudService<RuleDao, Rule> {

}