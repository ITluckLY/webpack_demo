package com.dc.smarteam.modules.serviceinfo.service;

import com.dc.smarteam.modules.serviceinfo.entity.TradeScenario;

import java.util.List;

/**
 * @Author: gaoyang
 * @Date: 2020/10/26  8:56
 * @Description:
 */

public interface TradeScenarioService {
    List<TradeScenario> findListFail(TradeScenario tradeScenario);

    List<TradeScenario> findUploadFail(TradeScenario tradeScenario);

    List<TradeScenario> findListSuss(TradeScenario tradeScenario);
}
