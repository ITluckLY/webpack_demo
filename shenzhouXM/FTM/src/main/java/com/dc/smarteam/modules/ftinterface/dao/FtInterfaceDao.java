package com.dc.smarteam.modules.ftinterface.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;

import com.dc.smarteam.modules.ftinterface.enity.FtFile;

import java.util.List;

/**
 * Created by Administrator on 2019/12/4.
 */

@MyBatisDao
public interface FtInterfaceDao extends LongCrudDao<FtFile> {

    public List<FtFile> findFileList(FtFile ftFile);

}