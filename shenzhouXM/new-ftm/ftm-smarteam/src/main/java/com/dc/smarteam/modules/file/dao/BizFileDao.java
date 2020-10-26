package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.BizFile;

import java.util.List;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Mapper
public interface BizFileDao extends CrudDao<BizFile> {
    public List<String> findSystemNameList(BizFile bizFile);

    public List<String> findClientUserNameList(BizFile bizFile);

    public List<String> findNodeNameList(BizFile bizFile);
}
