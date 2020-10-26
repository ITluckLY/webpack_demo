package com.dc.smarteam.modules.monitor.fttransformonitor.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.monitor.fttransformonitor.entity.FtTransforMonitor;

import java.util.List;

/**
 * Created by lvchuan on 2016/8/1.
 */
@MyBatisDao
public interface FtTransforMonitorDao extends CrudDao<FtTransforMonitor> {
    List<FtTransforMonitor> findTransforLog(FtTransforMonitor ftTransforMonitor);

    List<FtTransforMonitor> findTransforstatistic(FtTransforMonitor ftTransforMonitor);

    List<FtTransforMonitor> findTransfordetail(FtTransforMonitor ftTransforMonitor);

    List<FtTransforMonitor> findDownloadNumberPerHour(FtTransforMonitor ftTransforMonitor);

    List<FtTransforMonitor> findUploadNumberPerHour(FtTransforMonitor ftTransforMonitor);

    List<FtTransforMonitor> findUploadFailPerHour(FtTransforMonitor ftTransforMonitor);

    List<FtTransforMonitor> findDownloadFailPerHour(FtTransforMonitor ftTransforMonitor);

    List<FtTransforMonitor> findDownload();

    List<FtTransforMonitor> findUpload();

    long findUploadTotal(String nodeName);

    long findUploadTotalForSucc(String nodeName);

    long findUploadTotalForFail(String nodeName);

    long findDownloadTotal(String nodeName);

    long findDownloadTotalForSucc(String nodeName);

    long findDownloadTotalForFail(String nodeName);
}

