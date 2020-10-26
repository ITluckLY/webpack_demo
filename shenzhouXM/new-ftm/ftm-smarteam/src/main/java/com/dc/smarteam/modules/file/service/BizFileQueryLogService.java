package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.BizFileQueryLogDao;
import com.dc.smarteam.modules.file.entity.BizFileDownloadLog;
import com.dc.smarteam.modules.file.entity.BizFileQueryLog;
import com.dc.smarteam.modules.file.service.impl.BizFileDownloadLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Service
@Transactional(readOnly = true)
public class BizFileQueryLogService extends CrudService<BizFileQueryLogDao, BizFileQueryLog> {
    @Autowired
    private BizFileDownloadLogServiceImpl bizFileDownloadLogService;

    @Override
    public List<BizFileQueryLog> findList(BizFileQueryLog bizFileQueryLog) {
        return super.findList(bizFileQueryLog);
    }

    public List<String> findSystemNameList() {
        return dao.findSystemNameList(new BizFileQueryLog());
    }

    public List<String> findNodeNameList() {
        return dao.findNodeNameList(new BizFileQueryLog());
    }

    public List<Long> findUploadListByRouteAndTime(Map map) {
        return dao.findUploadListByRouteAndTime(map);
    }
    public List<Long> findDownloadListByRouteAndTime(Map map) {
        return dao.findDownloadListByRouteAndTime(map);
    }
    public Long findListByTime(Map map) {
        return dao.findListByTime(map);
    }

    public Long findListByTimeBySucc(Map map) {
        return dao.findListByTimeBySucc(map);
    }

    public boolean isFilterByunameAndflow(BizFileQueryLog bizFileQueryLog) {
        if (bizFileQueryLog == null || null == bizFileQueryLog.getDownuname() || null == bizFileQueryLog.getFlowNo()) {
            return false;
        }
        BizFileDownloadLog bizFileDownloadLog = new BizFileDownloadLog();
        bizFileDownloadLog.setUname(bizFileQueryLog.getDownuname());
        bizFileDownloadLog.setFlowNo(bizFileQueryLog.getFlowNo());
        bizFileDownloadLog.setFileName(bizFileQueryLog.getFileName());
        bizFileDownloadLog.setEndDate(bizFileQueryLog.getEndDate());
        if (null != (bizFileDownloadLogService.findsussDownloadLogByunameAndFlow(bizFileDownloadLog))) {
            return true;
        }
        return false;
    }
    public boolean isFilterByunameAndfileName(BizFileQueryLog bizFileQueryLog) {
        if (bizFileQueryLog == null || null == bizFileQueryLog.getDownuname() || null == bizFileQueryLog.getFileName()) {
            return false;
        }
        BizFileDownloadLog bizFileDownloadLog = new BizFileDownloadLog();
        bizFileDownloadLog.setUname(bizFileQueryLog.getDownuname());
        bizFileDownloadLog.setFlowNo(bizFileQueryLog.getFlowNo());
        bizFileDownloadLog.setFileName(bizFileQueryLog.getFileName());
        bizFileDownloadLog.setBeginDate(bizFileQueryLog.getBeginDate());
        bizFileDownloadLog.setEndDate(bizFileQueryLog.getEndDate());
        if (null != (bizFileDownloadLogService.findsussDownloadLogByunameAndFileName(bizFileDownloadLog))) {
            return true;
        }
        return false;
    }

}
