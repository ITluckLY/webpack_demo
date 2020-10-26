package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.client.entity.FtOperationLog;

import java.util.List;

@Mapper
public interface FtOperationLogDao extends LongCrudDao<FtOperationLog> {
    public List<FtOperationLog> findList(FtOperationLog ftOperationLog);
    public List<FtOperationLog> findFileName();
    public List<FtOperationLog> findtags();
    public FtOperationLog findById(String id);
    public void addTag(FtOperationLog ftOperationLog);
}
