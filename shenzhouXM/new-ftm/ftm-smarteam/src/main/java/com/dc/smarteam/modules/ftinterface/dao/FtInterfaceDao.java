package com.dc.smarteam.modules.ftinterface.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.ftinterface.enity.FtFiles;

import java.util.List;

/**
 * Created by Administrator on 2019/12/4.
 */

@Mapper
public interface FtInterfaceDao extends LongCrudDao<FtFiles> {

    public List<FtFiles> findFileList(FtFiles ftFile);

}