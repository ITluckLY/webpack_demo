/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.serviceinfo.entity.TradeScenario;

import java.util.List;

/**
 * 用户管理DAO接口
 *
 * @author liwjx
 * @version 2017-09-07
 */
@Mapper
public interface TradeScenarioDao extends CrudDao<TradeScenario> {

    List<TradeScenario> findListSuss(TradeScenario tradeScenario);

    List<TradeScenario> findListFail(TradeScenario tradeScenario);

    List<TradeScenario> findUploadFail(TradeScenario tradeScenario);
}