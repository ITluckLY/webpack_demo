package com.dc.smarteam.modules.sys.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.sys.entity.OptTag;

/**
 * Created by xuchuang on 2018/6/26.
 */
@MyBatisDao
public interface OptTagDao extends CrudDao<OptTag> {
    OptTag select2(OptTag optTag);
}
