package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.file.entity.BizFileUploadLog;

import java.util.List;
import java.util.Map;

/**
 * @Author: gaoyang
 * @Date: 2020/10/23  16:23
 * @Description:
 */

public interface BizFileUploadLogServiceI {
    List<BizFileUploadLog> findListByTimeByFail(Map<String, Object> map);

    Long findListByTimeBySucc(Map<String, Object> map);

    long findUpsussByflowNoAndUname(Map<String, Object> map1);

    Page<BizFileUploadLog> findPage(Page<BizFileUploadLog> bizFileUploadLogPage, BizFileUploadLog bizFileUploadLog);

    BizFileUploadLog get(BizFileUploadLog bizFileUploadLog);

    List<Long> findListBySysTime(Map<String, Object> map2);

    List<Long> findListBySysAndTime(Map<String, Object> map2);

    List<Long> findListByTranCodeAndTime(Map<String, Object> map2);

    List<Long> findListByTime(Map<String, Object> map2);

    List<Map<String, Long>> findListByTimeBySysName(Map<String, Object> map2);

    List<Map<String, Long>> findListByTimeBySys(Map<String, Object> map2);

    List<Map<String, Long>> findListByTimeByClient(Map<String, Object> map2);

    List<Map<String, Long>> findListByTimeByTranCode(Map<String, Object> map2);

    List<Map> findListByTranCode(Map<String, Object> map2);

    List<Map> getDetail(Map<String, Object> map);
}
