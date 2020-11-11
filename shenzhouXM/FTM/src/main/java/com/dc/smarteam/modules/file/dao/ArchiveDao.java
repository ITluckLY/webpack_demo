package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.file.entity.Archive;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorLog;


import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/9/4.
 */
@MyBatisDao
public interface ArchiveDao extends CrudDao<Archive> {
    List<String> findUserNameList(Archive archive);
    List<String> findTranCodeList(Archive archive);
}
