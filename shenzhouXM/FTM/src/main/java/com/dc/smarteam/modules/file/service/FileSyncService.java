package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.FileSyncDao;
import com.dc.smarteam.modules.file.entity.FileSync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/9/4.
 */
@Service
@Transactional(readOnly = true)
public class FileSyncService extends CrudService<FileSyncDao, FileSync> {

    @Autowired
    private FileSyncDao fileSyncDao;
    @Override
    public List<FileSync> findList(FileSync fileSync) {
        return super.findList(fileSync);
    }

    public List<String> findNodeNameList() {
        return fileSyncDao.findNodeNameList(new FileSync());
    }

    public List<String> findTranCodeList() {
        return fileSyncDao.findTranCodeList(new FileSync());
    }
}
