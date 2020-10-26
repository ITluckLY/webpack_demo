package com.dc.smarteam.modules.sys.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.sys.entity.OptTag;

/**
 * Created by xuchuang on 2018/6/26.
 */
@Mapper
public interface OptTagDao extends CrudDao<OptTag> {
    OptTag select2(OptTag optTag);
}
