package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.ArchiveDao;
import com.dc.smarteam.modules.file.entity.Archive;
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
public class ArchiveService extends CrudService<ArchiveDao, Archive> {

    @Autowired
    private ArchiveDao archiveDao;
    @Override
    public List<Archive> findList(Archive archive) {
        return super.findList(archive);
    }

    public List<String> findUserNameList() {
        return archiveDao.findUserNameList(new Archive());
    }

    public List<String> findTranCodeList() {
        return archiveDao.findTranCodeList(new Archive());
    }


}
