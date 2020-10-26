package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.FileSync;

import java.util.List;

/**
 * Created by Administrator on 2019/9/4.
 */
@Mapper
public interface FileSyncDao extends CrudDao<FileSync> {
    List<String> findTranCodeList(FileSync fileSync);
    List<String> findNodeNameList(FileSync fileSync);

}