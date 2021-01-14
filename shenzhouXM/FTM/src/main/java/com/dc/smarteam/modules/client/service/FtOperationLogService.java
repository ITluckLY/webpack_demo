package com.dc.smarteam.modules.client.service;

import com.dc.smarteam.common.service.LongCrudService;
import com.dc.smarteam.common.utils.CacheUtils;
import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.modules.client.dao.FtOperationLogDao;
import com.dc.smarteam.modules.client.entity.FtOperationLog;
import com.dc.smarteam.modules.sys.entity.OptTag;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuchuang on 2018/5/24.
 */
@Service
@Transactional
public class FtOperationLogService extends LongCrudService<FtOperationLogDao, FtOperationLog> {

    public List<FtOperationLog> findList(FtOperationLog ftOperationLog) {
        return dao.findList(ftOperationLog);
    }
    public void addTag(FtOperationLog ftOperationLog) {
         dao.addTag(ftOperationLog);
    }
    public List<FtOperationLog> findFileName() {
        return dao.findFileName();
    }
    public List<FtOperationLog> findtags() {
        return dao.findtags();
    }
    public FtOperationLog findById(String id){return dao.findById(id);}

    public final static Map<String,String> remarkMap = new HashMap<>();
    static {
        remarkMap.put("user.xml", "用户配置信息");
        remarkMap.put("system.xml", "系统配置信息");
        remarkMap.put("flow.xml", "流程配置信息");
        remarkMap.put("service_info.xml", "交易码配置信息");
        remarkMap.put("component.xml", "系统配置信息");
        remarkMap.put("route.xml", "路由配置信息");
        remarkMap.put("file_clean.xml", "文件清理配置信息");
        remarkMap.put("client_status.xml","客户端状态配置信息");
        remarkMap.put("netty.xml","客户流量信息");
        //...
    }

    private final static String ADD = "add";
    private final static String DELETE = "delete";
    private final static String UPDATE = "update";


    private static String getRemark(String cfgFileName){
        return remarkMap.get(cfgFileName)==null?"配置":remarkMap.get(cfgFileName);
    }

    @Transactional
    public void updateOperation(FtOperationLog ftOperationLog, String paramName, String cfgFileName) {
        ftOperationLog.setCfgFileName(cfgFileName);
        ftOperationLog.setParamUpdateType(UPDATE);
        ftOperationLog.setRemarks(getRemark(cfgFileName) + "修改:" + paramName);
        logger.info("cfg operate 插入更新记录 {}",ftOperationLog.toString());
        saveLog(ftOperationLog);
    }

    @Transactional
    public void addOperation(FtOperationLog ftOperationLog,String paramName,String cfgFileName){
        ftOperationLog.setCfgFileName(cfgFileName);
        ftOperationLog.setParamUpdateType(ADD);
        ftOperationLog.setRemarks(getRemark(cfgFileName)+"新增:"+paramName);
        logger.info("cfg operate 插入新增记录 {}",ftOperationLog.toString());
        saveLog(ftOperationLog);

    }

    @Transactional
    public void delOperation(FtOperationLog ftOperationLog,String paramName,String cfgFileName){
        ftOperationLog.setCfgFileName(cfgFileName);
        ftOperationLog.setParamUpdateType(DELETE);
        ftOperationLog.setRemarks(getRemark(cfgFileName)+"删除:"+paramName);
        logger.info("cfg operate 插入删除记录 {}",ftOperationLog.toString());
        saveLog(ftOperationLog);
    }

    public void saveLog(FtOperationLog ftOperationLog){
        ftOperationLog.setModifiedDate(new Date());
        ftOperationLog.setOpeId(UserUtils.getUser().getId());
        ftOperationLog.setOpeName(UserUtils.getUser().getLoginName());
        ftOperationLog.setId(IdGen.longUUID());
        //ftOperationLog.setRemark1(Tag.currTag);//添加标签
        OptTag optTag = (OptTag)CacheUtils.get("tag_type");
        ftOperationLog.setRemark1(optTag==null?"":optTag.getStyle());
        dao.insert(ftOperationLog);
    }

}
