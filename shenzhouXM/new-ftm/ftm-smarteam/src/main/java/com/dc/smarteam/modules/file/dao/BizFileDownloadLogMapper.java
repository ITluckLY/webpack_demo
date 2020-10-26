package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.BizFileDownloadLog;

import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Mapper
public interface BizFileDownloadLogMapper extends CrudDao<BizFileDownloadLog> {

    public List<String> findSystemNameList(BizFileDownloadLog bizFileDownloadLog);

    public List<String> findNodeNameList(BizFileDownloadLog bizFileDownloadLog);
    public Long findListByTime2(Map map);
    public List<Long> findListByTime(Map map);
    public List<Long> findListByTranCodeAndTime(Map map);

    public List<Long> findListBySysAndTime(Map map);
    public List<Long> findListBySysTime(Map map);

    public Long findListByTimeBySucc(Map map);

    public List<BizFileDownloadLog> findListByTimeByFail(Map map);

    public List<BizFileDownloadLog> findListByTranCodeAndFileName(Map map);

   public BizFileDownloadLog findsussDownloadLogByunameAndFlow(BizFileDownloadLog bizFileDownloadLog);

    public List<BizFileDownloadLog> findsussDownloadLogByunameAndFileName(BizFileDownloadLog bizFileDownloadLog);

    public long finddownloadsussByflowNoAndUname(Map map);
}
