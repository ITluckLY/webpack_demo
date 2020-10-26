package com.dc.smarteam.modules.nodeparam.service.impl;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.modules.nodeparam.dao.FtNodeParamDao;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;
import com.dc.smarteam.modules.nodeparam.service.FtNodeParamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 节点参数Service
 *
 * @author liwang
 * @version 2016-01-11
 */
@Service("FtNodeParamServiceImpl")
@Transactional(readOnly = true)
public class FtNodeParamServiceImpl extends CrudService<FtNodeParamDao, FtNodeParam> implements FtNodeParamService {
    public FtNodeParam get(String id) {
        return super.get(id);
    }

    public List<FtNodeParam> findList(FtNodeParam ftNodeParam) {
        return super.findList(ftNodeParam);
    }

    public Page<FtNodeParam> findPage(Page<FtNodeParam> page, FtNodeParam ftNodeParam) {
        return super.findPage(page, ftNodeParam);
    }

    @Transactional(readOnly = false)
    public void save(FtNodeParam ftNodeParam) {
        super.save(ftNodeParam);
    }

    @Transactional(readOnly = false)
    public void delete(FtNodeParam ftNodeParam) {
        super.delete(ftNodeParam);
    }
}
