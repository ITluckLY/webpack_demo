package com.dc.smarteam.modules.client.dao;

import com.dc.smarteam.common.persistence.LongCrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.client.entity.FtOperationLog;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface FtOperationLogDao extends LongCrudDao<FtOperationLog> {
    public List<FtOperationLog> findList(FtOperationLog ftOperationLog);
    public List<FtOperationLog> findFileName();
    public List<FtOperationLog> findtags();
    public FtOperationLog findById(String id);
    public void addTag(FtOperationLog ftOperationLog);
}
