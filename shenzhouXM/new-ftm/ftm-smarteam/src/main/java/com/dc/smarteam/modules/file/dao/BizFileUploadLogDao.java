package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.BizFileUploadLog;

import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Mapper
public interface BizFileUploadLogDao extends CrudDao<BizFileUploadLog> {
    public List<String> findSystemNameList(BizFileUploadLog bizFileUploadLog);

    public List<String> findNodeNameList(BizFileUploadLog bizFileUploadLog);
    public List<String> findUNameList(BizFileUploadLog bizFileUploadLog);
    public List<String> findSourceList(Map map);
    public List<String> findTransCodeList(BizFileUploadLog bizFileUploadLog);
    public List<String> findRouteList(BizFileUploadLog bizFileUploadLog);

    public Long findListByTime2(Map map);
    public List<Long> findListByTime(Map map);

    public List<Long> findListByTranCodeAndTime(Map map);

    public List<Long> findListBySysAndTime(Map map);
    public List<Long> findListBySysTime(Map map);

    public List<Map<String,Long>> findListByTimeBySys(Map map);
    public List<Map<String,Long>> findListByTimeBySysName(Map map);
    public List<Map<String,Long>> findListByTimeByClient(Map map);
    public List<Map<String,Long>> findListByTimeByTranCode(Map map);
    public Long findListByTimeBySucc(Map map);

    public List<BizFileUploadLog> findListByTimeByFail(Map map);

    public List<BizFileUploadLog> findListByTranCodeAndFileName(Map map);

    public List<Map> findListByTranCode(Map map);
    public List<Map> getDetail(Map map);

    public long findUpsussByflowNoAndUname(Map map);
}
