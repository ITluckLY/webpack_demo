package com.dc.smarteam.modules.cfgfile.service.impl;

import com.dc.smarteam.cfgmodel.BaseModel;
import com.dc.smarteam.common.service.LongCrudService;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.helper.EmptyCfgXmlHelper;
import com.dc.smarteam.helper.IDHelper;
import com.dc.smarteam.modules.cfgfile.dao.CfgFileMapper;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.cfgsync.service.NodeCfgSyncService;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import com.dc.smarteam.util.CharsetUtil;
import com.dc.smarteam.util.XMLDealTool;
import com.dc.smarteam.util.XmlBeanUtil;
import org.dom4j.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@Service("CfgFileServiceImpl")
@Transactional
public class CfgFileServiceImpl extends LongCrudService<CfgFileMapper, CfgFile> implements CfgFileService {

    @Resource
    private NodeCfgSyncService nodeCfgSyncService;

    @Transactional
    public void save(CfgFile cfgFile) {
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

    public CfgFile findOne(CfgFile cfgFile) {
        List<CfgFile> list = findList(cfgFile);
        if (list.size() > 0) return list.get(0);
        return null;
    }

    @Transactional
    public void updateByNameAndNodeType(CfgFile cfgFile) {
        dao.updateByFileNameAndNodeType(cfgFile);
    }

    public String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        String cfgXml = null;
        CfgFile queryCfgFile = new CfgFile();
        queryCfgFile.setNodeType(NodeType.NAMENODE.name());
        queryCfgFile.setSystem("UNDEFINED");
        queryCfgFile.setFileName(fileName);
        CfgFile cfgFile = findOne(queryCfgFile);
        if (cfgFile != null) cfgXml = cfgFile.getContent();
        if (cfgXml == null) cfgXml = EmptyCfgXmlHelper.getEmptyXml(fileName);
        return nodeCfgSyncService.generateSyncCfgXml(sysname, fileName, cfgXml, hasTimestamp);
    }

    public <T extends BaseModel> T loadModel4Name(String cfgFileName, Class<? extends T> tclass) {
        CfgFile cfgFile = new CfgFile();
        cfgFile.setFileName(cfgFileName);
        cfgFile.setNodeType(NodeType.NAMENODE.name());
        cfgFile.setSystem("UNDEFINED");
        List<CfgFile> list = findList(cfgFile);
        String xml;
        if (list.size() == 0) xml = EmptyCfgXmlHelper.getEmptyXml(cfgFileName);
        else xml = CharsetUtil.convertFromUTF8(list.get(0).getContent());
        Document doc = XMLDealTool.readXml(xml);
        T model = XmlBeanUtil.toEntity(XMLDealTool.getRootXml(doc), tclass);
        model.init();
        return model;
    }

    public <T extends BaseModel> void saveModel4Name(String cfgFileName, T model) {
        logger.info(String.valueOf(model));
        String xml = XmlBeanUtil.toXml(model);
        CfgFile cfgFile = new CfgFile();
        cfgFile.setFileName(cfgFileName);
        cfgFile.setNodeType(NodeType.NAMENODE.name());
        cfgFile.setSystem("UNDEFINED");
        List<CfgFile> list = findList(cfgFile);
        CfgFile target;
        if (list.size() == 0) {
            cfgFile.setId(0L);
            cfgFile.setSystem("UNDEFINED");
            cfgFile.setFileType("xml");
            target = cfgFile;
        } else {
            target = list.get(0);
        }
        target.setContent(CharsetUtil.convertToUTF8(xml));
        save(target);
    }
}
