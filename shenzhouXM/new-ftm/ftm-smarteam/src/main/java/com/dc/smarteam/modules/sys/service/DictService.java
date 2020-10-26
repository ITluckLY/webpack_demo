package com.dc.smarteam.modules.sys.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.sys.entity.Dict;
import java.util.List;

public interface DictService {

    List<String> findTypeList();

    void save(Dict dict);

    void delete(Dict dict);

    Dict get(String id);

    Page<Dict> findPage(Page<Dict> page, Dict entity);

    List<Dict> findList(Dict entity);

}
