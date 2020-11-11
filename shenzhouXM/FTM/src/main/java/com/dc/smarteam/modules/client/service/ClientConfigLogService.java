package com.dc.smarteam.modules.client.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.aspectCfg.cfgOperate.ICfgService;
import com.dc.smarteam.common.service.LongCrudService;
import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.modules.client.dao.ClientConfigLogDao;
import com.dc.smarteam.modules.client.dao.FtOperationLogDao;
import com.dc.smarteam.modules.client.entity.ClientConfigLog;
import com.dc.smarteam.modules.client.entity.FtOperationLog;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangzbb on 2016/8/3.
 */
@Service
@Transactional
public class ClientConfigLogService extends LongCrudService<ClientConfigLogDao, ClientConfigLog>{

    @Transactional
    public void insert(ClientConfigLog clientConfigLog){
        dao.insert(clientConfigLog);
    }

}
