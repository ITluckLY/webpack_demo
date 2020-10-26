package com.dc.smarteam.modules.client.service;

import com.dc.smarteam.common.service.LongCrudService;
import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.helper.IDHelper;
import com.dc.smarteam.modules.client.dao.ClientCfgFileDao;
import com.dc.smarteam.modules.client.entity.ClientCfgFile;
import com.dc.smarteam.modules.client.entity.ClientConfigLog;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by mocg on 2017/3/15.
 */
@Service
@Transactional
public class ClientCfgFileService extends LongCrudService<ClientCfgFileDao, ClientCfgFile> {

    @Resource
    ClientConfigLogService clientConfigLogService;
    @Transactional
    public void save(ClientCfgFile cfgFile) {
        cfgFile.setUpdateBy(UserUtils.getUser());
        Date now = new Date();
        cfgFile.setUpdateDate(now);
        cfgFile.setFileSize((long) cfgFile.getContent().getBytes().length);
        if (IDHelper.isEmpty(cfgFile.getId())) {
            cfgFile.preInsert();
            if (cfgFile.getCreateDate() == null) cfgFile.setCreateDate(now);
            dao.insert(cfgFile);
        } else {
            cfgFile.preUpdate();
            dao.update(cfgFile);
        }
    }

    public ClientCfgFile findOne(ClientCfgFile cfgFile) {
        List<ClientCfgFile> list = findList(cfgFile);
        if (list.size() > 0) return list.get(0);
        return null;
    }

    @Transactional
    public int updateByNameAndNodeType(ClientCfgFile cfgFile) {
        return dao.updateByFileNameAndNodeType(cfgFile);
    }

    public String getCurrCfgContent(ClientSyn clientSyn, String fileName, String content) {
        String old = null;
        ClientCfgFile queryCfgFile = new ClientCfgFile();
        queryCfgFile.setNodeType("CLIENT");
        queryCfgFile.setSystem(clientSyn.getName());
        queryCfgFile.setIp(clientSyn.getIp());
        queryCfgFile.setPort(clientSyn.getPort());
        queryCfgFile.setFileName(fileName);
        queryCfgFile.setFileType(FilenameUtils.getExtension(fileName));
        ClientCfgFile cfgFile = findOne(queryCfgFile);
        if (cfgFile != null) {
            old = cfgFile.getContent();
            if(!old.equals(content)){
                cfgFile.setContent(content);
                save(cfgFile);
            }
        }else{
            queryCfgFile.setContent(content);
            save(queryCfgFile);
        }
        if(old == null) old = "";
        ClientConfigLog clientConfigLog = new ClientConfigLog();
        clientConfigLog.setCfgFileName(fileName);
        clientConfigLog.setAfterModifyValue(content);
        clientConfigLog.setBeforeModifyValue(old);
        clientConfigLog.setClientName(clientSyn.getName());
        clientConfigLog.setIp(clientSyn.getIp());
        clientConfigLog.setPort(clientSyn.getPort());
        clientConfigLog.setId(IdGen.longUUID());
        clientConfigLogService.insert(clientConfigLog);
        return old;
    }

    public boolean bakfiledata(ClientSyn clientSyn, String fileName, String content) {
        String cfgxml = getCurrCfgContent(clientSyn, fileName,content);
        if(! cfgxml.equals(content)){
            ClientCfgFile cfgFile = new ClientCfgFile();
            cfgFile.setSystem(clientSyn.getName());
            cfgFile.setIp(clientSyn.getIp());
            cfgFile.setPort(clientSyn.getPort());
            cfgFile.setFileName(fileName);
            cfgFile.setNodeType("bak");
            cfgFile.setFileType(FilenameUtils.getExtension(fileName));
            cfgFile.setContent(cfgxml);
            if (null == findOne(cfgFile)) save(cfgFile);
            cfgFile = findOne(cfgFile);
            cfgFile.setContent(cfgxml);
            return updateByNameAndNodeType(cfgFile) == 1;
        }
        return false;
    }

    public ClientCfgFile findByNameAndFile(ClientCfgFile clientCfgFile){
            return dao.findByNameAndFile(clientCfgFile);
    }
}
