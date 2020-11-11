package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.file.dao.BizFileDao;
import com.dc.smarteam.modules.file.entity.BizFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Service
@Transactional(readOnly = true)
public class BizFileService extends CrudService<BizFileDao, BizFile> {
    public List<BizFile> findList(BizFile bizFile) {
        return super.findList(bizFile);
    }

    public List<String> findSystemNameList() {
        return dao.findSystemNameList(new BizFile());
    }

    public List<String> findClientUserNameList() {
        return dao.findClientUserNameList(new BizFile());
    }

    public List<String> findNodeNameList() {
        return dao.findNodeNameList(new BizFile());
    }

}
