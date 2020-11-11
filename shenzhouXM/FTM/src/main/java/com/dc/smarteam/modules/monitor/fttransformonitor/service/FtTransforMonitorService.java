package com.dc.smarteam.modules.monitor.fttransformonitor.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.monitor.fttransformonitor.dao.FtTransforMonitorDao;
import com.dc.smarteam.modules.monitor.fttransformonitor.entity.FtTransforMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lvchuan on 2016/8/1.
 */
@Service
@Transactional(readOnly = true)
public class FtTransforMonitorService extends CrudService<FtTransforMonitorDao, FtTransforMonitor> {
    @Autowired
    private FtTransforMonitorDao ftTransforMonitorDao;

    public Page<FtTransforMonitor> findPage(Page<FtTransforMonitor> page, FtTransforMonitor ftTransforMonitor) {
        return super.findPage(page, ftTransforMonitor);
    }

    public List<FtTransforMonitor> findTransforLog(FtTransforMonitor ftTransforMonitor) {
        return ftTransforMonitorDao.findTransforLog(ftTransforMonitor);
    }

    public List<FtTransforMonitor> findTransforstatistic(FtTransforMonitor ftTransforMonitor) {
        return ftTransforMonitorDao.findTransforstatistic(ftTransforMonitor);
    }

    public List<FtTransforMonitor> findTransfordetail(FtTransforMonitor ftTransforMonitor) {
        return ftTransforMonitorDao.findTransfordetail(ftTransforMonitor);
    }

    public List<FtTransforMonitor> findDownloadNumberPerHour(FtTransforMonitor ftTransforMonitor) {
        return ftTransforMonitorDao.findDownloadNumberPerHour(ftTransforMonitor);
    }

    public List<FtTransforMonitor> findUploadNumberPerHour(FtTransforMonitor ftTransforMonitor) {
        return ftTransforMonitorDao.findUploadNumberPerHour(ftTransforMonitor);
    }

    public List<FtTransforMonitor> findUploadFailPerHour(FtTransforMonitor ftTransforMonitor) {
        return ftTransforMonitorDao.findUploadFailPerHour(ftTransforMonitor);
    }

    public List<FtTransforMonitor> findDownloadFailPerHour(FtTransforMonitor ftTransforMonitor) {
        return ftTransforMonitorDao.findDownloadFailPerHour(ftTransforMonitor);
    }

    public List<FtTransforMonitor> findDownload() {
        return ftTransforMonitorDao.findDownload();
    }

    public List<FtTransforMonitor> findUpload() {
        return ftTransforMonitorDao.findUpload();
    }

    public long findUploadTotal(String nodeName) {
        return ftTransforMonitorDao.findUploadTotal(nodeName);
    }

    public long findUploadTotalForSucc(String nodeName) {
        return ftTransforMonitorDao.findUploadTotalForSucc(nodeName);
    }

    public long findUploadTotalForFail(String nodeName) {
        return ftTransforMonitorDao.findUploadTotalForFail(nodeName);
    }

    public long findDownloadTotal(String nodeName) {
        return ftTransforMonitorDao.findDownloadTotal(nodeName);
    }

    public long findDownloadTotalForSucc(String nodeName) {
        return ftTransforMonitorDao.findDownloadTotalForSucc(nodeName);
    }

    public long findDownloadTotalForFail(String nodeName) {
        return ftTransforMonitorDao.findDownloadTotalForFail(nodeName);
    }
}
