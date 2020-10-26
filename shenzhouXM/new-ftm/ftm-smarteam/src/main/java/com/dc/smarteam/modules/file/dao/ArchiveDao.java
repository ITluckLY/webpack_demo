package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.Archive;

import java.util.List;

/**
 * Created by Administrator on 2019/9/4.
 */
@Mapper
public interface ArchiveDao extends CrudDao<Archive> {
    List<String> findUserNameList(Archive archive);
    List<String> findTranCodeList(Archive archive);
}
