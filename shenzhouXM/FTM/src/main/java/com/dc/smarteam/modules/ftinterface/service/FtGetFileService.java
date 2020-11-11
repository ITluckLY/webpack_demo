package com.dc.smarteam.modules.ftinterface.service;

import com.dc.smarteam.common.service.LongCrudService;
import com.dc.smarteam.modules.ftinterface.dao.FtInterfaceDao;
import com.dc.smarteam.modules.ftinterface.enity.FtFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2019/12/13.
 */
@Service
@Transactional(readOnly = true)
public class FtGetFileService extends LongCrudService<FtInterfaceDao,FtFile> {

    public List<FtFile> findFileList(FtFile ftFile) {
        return dao.findFileList(ftFile);
    }
}

