package com.dc.smarteam.modules.gen.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.gen.entity.GenScheme;

/**
 * 生成方案DAO接口
 *
 * @author ThinkGem
 * @version 2013-10-15
 */
@Mapper
public interface GenSchemeDao extends CrudDao<GenScheme> {

}
