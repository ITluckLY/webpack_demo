package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtTranCodeAlarmLineDao;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtTranCodeAlarmLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xuchuang on 2018/6/8.
 */
@Service
@Transactional
public class FtTranCodeAlarmLineService  extends CrudService<FtTranCodeAlarmLineDao,FtTranCodeAlarmLine> {

    public List<FtTranCodeAlarmLine> findTranCodeAlarmLineList(FtTranCodeAlarmLine ftTranCodeAlarmLine){
        return this.dao.findList(ftTranCodeAlarmLine);
    }

    public Page<FtTranCodeAlarmLine> findPage(Page page, FtTranCodeAlarmLine ftTranCodeAlarmLine){
        return super.findPage(page,ftTranCodeAlarmLine);
    }

    @Transactional
    public int saveOneTranCodeAlarmLine(FtTranCodeAlarmLine ftTranCodeAlarmLine){
        if(this.dao.get(ftTranCodeAlarmLine.getTranCode())!=null){
            logger.info("update ftTranCodeAlarmLine");
            return this.dao.update(ftTranCodeAlarmLine);
        }
        logger.info("insert ftTranCodeAlarmLine");
        return this.dao.insert(ftTranCodeAlarmLine);
    }

    public int deleteTranCodeAlarmLine(FtTranCodeAlarmLine ftTranCodeAlarmLine){
        return this.dao.delete(ftTranCodeAlarmLine);
    }

    public FtTranCodeAlarmLine findTranCodeAlarmLine(String tranCode){
        return this.dao.get(tranCode);
    }

    public FtTranCodeAlarmLine find(FtTranCodeAlarmLine ftTranCodeAlarmLine){
        return this.dao.get(ftTranCodeAlarmLine);
    }

}
