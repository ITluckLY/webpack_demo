package com.dc.smarteam.modules.monitor.ftnodemonitor.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtTranCodeAlarmLine;

/**
 * Created by xuchuang on 2018/6/8.
 */
@MyBatisDao
public interface FtTranCodeAlarmLineDao extends CrudDao<FtTranCodeAlarmLine> {

    //    --> super:
//    int insert(FtTranCodeAlarmLine ftTranCodeAlarmLine);
//
//    int update(FtTranCodeAlarmLine ftTranCodeAlarmLine);
//
//    FtTranCodeAlarmLine get(FtTranCodeAlarmLine ftTranCodeAlarmLine);
//
    FtTranCodeAlarmLine get(String tranCode);

}
