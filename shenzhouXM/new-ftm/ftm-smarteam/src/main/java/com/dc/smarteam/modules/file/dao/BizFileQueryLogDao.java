/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.BizFileQueryLog;

import java.util.List;
import java.util.Map;

/**
 * 文件传输记录DAO接口
 *
 * @author hudja
 * @version 2016-01-12
 */
@Mapper
public interface BizFileQueryLogDao extends CrudDao<BizFileQueryLog> {
    public List<String> findSystemNameList(BizFileQueryLog bizFileQueryLog);

    public List<String> findNodeNameList(BizFileQueryLog bizFileQueryLog);

    public Long findListByTime(Map map);
    public List<Long> findUploadListByRouteAndTime(Map map);
    public List<Long> findDownloadListByRouteAndTime(Map map);

    public Long findListByTimeBySucc(Map map);

}