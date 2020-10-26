package com.dc.smarteam.modules.sys.service.impl;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.common.utils.CacheUtils;
import com.dc.smarteam.modules.sys.dao.DictMapper;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.modules.sys.service.DictService;
import com.dc.smarteam.modules.sys.utils.DictUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("DictServiceImpl")
@Transactional(readOnly = true)
public class DictServiceImpl extends CrudService<DictMapper, Dict> implements DictService {

    /**
     * 查询字段类型列表
     *
     * @return
     */
    public List<String> findTypeList() {
        return dao.findTypeList(new Dict());
    }

    @Transactional(readOnly = false)
    public void save(Dict dict) {
        super.save(dict);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
    }

    @Transactional(readOnly = false)
    public void delete(Dict dict) {
        super.delete(dict);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
    }

}
