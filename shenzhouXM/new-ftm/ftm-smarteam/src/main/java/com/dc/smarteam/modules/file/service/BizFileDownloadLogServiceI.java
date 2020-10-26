package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.file.entity.BizFileDownloadLog;

import java.util.List;
import java.util.Map;

/**
 * @Author: gaoyang
 * @Date: 2020/10/23  16:53
 * @Description:
 */

public interface BizFileDownloadLogServiceI {
    List<BizFileDownloadLog> findDownListByFail(Map<String, Object> map);

    BizFileDownloadLog get(BizFileDownloadLog bizFileDownloadLog);

    long finddownloadsussByflowNoAndUname(Map<String, Object> map2);

    Long findListByTimeBySucc(Map<String, Object> map);

    Page<BizFileDownloadLog> findPage(Page<BizFileDownloadLog> bizFileDownloadLogPage, BizFileDownloadLog bizFileDownloadLog);
}
