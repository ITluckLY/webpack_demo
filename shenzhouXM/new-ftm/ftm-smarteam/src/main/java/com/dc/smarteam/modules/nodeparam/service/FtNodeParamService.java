package com.dc.smarteam.modules.nodeparam.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FtNodeParamService {

    FtNodeParam get(String id);

    List<FtNodeParam> findList(FtNodeParam ftNodeParam);

    Page<FtNodeParam> findPage(Page<FtNodeParam> page, FtNodeParam ftNodeParam);

    void save(FtNodeParam ftNodeParam);

    void delete(FtNodeParam ftNodeParam);
}
