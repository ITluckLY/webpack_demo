package com.dc.smarteam.modules.file.service.impl;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.BizFileUploadLogMapper;
import com.dc.smarteam.modules.file.entity.BizFileUploadLog;
import com.dc.smarteam.modules.file.service.BizFileUploadLogServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Service("BizFileUploadLogServiceImpl")
@Transactional(readOnly = true)
public class BizFileUploadLogServiceImpl extends CrudService<BizFileUploadLogMapper, BizFileUploadLog> implements BizFileUploadLogServiceI {
    @Override
    public List<BizFileUploadLog> findList(BizFileUploadLog bizFileUploadLog) {
        return super.findList(bizFileUploadLog);
    }

    public List<String> findSystemNameList() {
        return dao.findSystemNameList(new BizFileUploadLog());
    }

    public List<String> findNodeNameList() {
        return dao.findNodeNameList(new BizFileUploadLog());
    }
    public List<String> findUNameList() {
        return dao.findUNameList(new BizFileUploadLog());
    }
    public List<String> findSourceList(Map map) {
        return dao.findSourceList(map);
    }
    public List<String> findTransCodeList() {
        return dao.findTransCodeList(new BizFileUploadLog());
    }
    public List<String> findRouteList() {
        return dao.findRouteList(new BizFileUploadLog());
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
    public List<Map<String,Long>> findListByTimeBySys(Map map) {
        return dao.findListByTimeBySys(map);
    }
    public List<Map<String,Long>> findListByTimeBySysName(Map map) {
        return dao.findListByTimeBySysName(map);
    }
    public List<Map<String,Long>> findListByTimeByClient(Map map) {
        return dao.findListByTimeByClient(map);
    }
    public List<Map<String,Long>> findListByTimeByTranCode(Map map) {
        return dao.findListByTimeByTranCode(map);
    }
    public Long findListByTimeBySucc(Map map) {
        return dao.findListByTimeBySucc(map);
    }

    public List<BizFileUploadLog> findListByTimeByFail(Map map){return dao.findListByTimeByFail(map);}

    public List<BizFileUploadLog> findListByTranCodeAndFileName(Map map){return  dao.findListByTranCodeAndFileName(map);}

    public List<Map> findListByTranCode(Map map){return  dao.findListByTranCode(map);}

    public List<Map> getDetail(Map map){return  dao.getDetail(map);}

    public long findUpsussByflowNoAndUname(Map map) {
        return dao.findUpsussByflowNoAndUname(map);}
}
