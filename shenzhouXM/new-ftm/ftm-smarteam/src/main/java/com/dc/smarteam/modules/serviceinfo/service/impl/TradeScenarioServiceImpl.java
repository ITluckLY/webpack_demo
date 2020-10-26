/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.service.impl;


import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.serviceinfo.dao.TradeScenarioMapper;
import com.dc.smarteam.modules.serviceinfo.entity.TradeScenario;
import com.dc.smarteam.modules.serviceinfo.service.TradeScenarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户管理Service
 *
 * @author liwjx
 * @version 2017-09-07
 */
@Service("TradeScenarioServiceImpl")
@Transactional(readOnly = true)
public class TradeScenarioServiceImpl extends CrudService<TradeScenarioMapper, TradeScenario> implements TradeScenarioService {

    public TradeScenario getTradeScenarioBytoandFrom(List<TradeScenario> tradeScenarios, TradeScenario tradeScenario) {
        if (null == tradeScenarios) {
            return null;
        }
        for (int i = 0; i < tradeScenarios.size(); i++) {
            if (StringUtils.equals(tradeScenario.getFromUid(), tradeScenario.getToUid())) {
                return tradeScenarios.get(i);
            }
        }
        return null;
    }

    public List<TradeScenario> findListSuss(TradeScenario tradeScenario) {
        return dao.findListSuss(tradeScenario);
    }

    public List<TradeScenario> findListFail(TradeScenario tradeScenario) {
        return dao.findListFail(tradeScenario);
    }

    public List<TradeScenario> findUploadFail(TradeScenario tradeScenario) {
        return dao.findUploadFail(tradeScenario);
    }
}