package com.dc.smarteam.modules.file.service.impl;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.BizFileDownloadLogMapper;
import com.dc.smarteam.modules.file.entity.BizFileDownloadLog;
import com.dc.smarteam.modules.file.service.BizFileDownloadLogServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Service("BizFileDownloadLogServiceImpl")
@Transactional(readOnly = true)
public class BizFileDownloadLogServiceImpl extends CrudService<BizFileDownloadLogMapper, BizFileDownloadLog> implements BizFileDownloadLogServiceI {

    @Override
    public List<BizFileDownloadLog> findList(BizFileDownloadLog bizFileDownloadLog) {
        return super.findList(bizFileDownloadLog);
    }


    public List<String> findSystemNameList() {
        return dao.findSystemNameList(new BizFileDownloadLog());
    }

    public List<String> findNodeNameList() {
        return dao.findNodeNameList(new BizFileDownloadLog());
    }

    public Long findListByTime2(Map map) {
        return dao.findListByTime2(map);
    }

    public List<Long> findListByTime(Map map) {
        return dao.findListByTime(map);
    }

    public List<Long> findListByTranCodeAndTime(Map map) {
        return dao.findListByTranCodeAndTime(map);
    }

    public List<Long> findListBySysAndTime(Map map) {
        return dao.findListBySysAndTime(map);
    }

    public List<Long> findListBySysTime(Map map) {
        return dao.findListBySysTime(map);
    }
    public Long findListByTimeBySucc(Map map) {
        return dao.findListByTimeBySucc(map);
    }

    public List<BizFileDownloadLog> findListByTranCodeAndFileName(Map map) {
        return dao.findListByTranCodeAndFileName(map);
    }

    public BizFileDownloadLog findsussDownloadLogByunameAndFlow(BizFileDownloadLog bizFileDownloadLog) {
        return dao.findsussDownloadLogByunameAndFlow(bizFileDownloadLog);
    }
    public List<BizFileDownloadLog> findsussDownloadLogByunameAndFileName(BizFileDownloadLog bizFileDownloadLog) {
        return dao.findsussDownloadLogByunameAndFileName(bizFileDownloadLog);
    }

    public List<BizFileDownloadLog> findDownListByFail(Map<String, Object> map) {
        return dao.findListByTimeByFail(map);
    }

    public long finddownloadsussByflowNoAndUname(Map<String, Object> map) {
        return  dao.finddownloadsussByflowNoAndUname(map);
    }
}
