package com.dc.smarteam.modules.cfgfile.service;

import com.dc.smarteam.cfgmodel.BaseModel;
import com.dc.smarteam.common.service.LongCrudService;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.helper.EmptyCfgXmlHelper;
import com.dc.smarteam.helper.IDHelper;
import com.dc.smarteam.modules.cfgfile.dao.CfgFileDao;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;
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

/**
 * Created by mocg on 2017/3/15.
 */
@Service
@Transactional
public class CfgFileService extends LongCrudService<CfgFileDao, CfgFile> {

    @Resource
    private NodeCfgSyncService nodeCfgSyncService;

    @Transactional
    public void save(CfgFile cfgFile) {
        cfgFile.setUpdateBy(UserUtils.getUser());
        Date now = new Date();
        cfgFile.setUpdateDate(now);
        cfgFile.setFileSize((long) cfgFile.getContent().getBytes().length);

        if (IDHelper.isEmpty(cfgFile.getId())) {
            cfgFile.preInsert(); // 插入数据更新 用户等信息
            if (cfgFile.getCreateDate() == null) cfgFile.setCreateDate(now);
            dao.insert(cfgFile);
        } else {
            cfgFile.preUpdate();
            dao.update(cfgFile); // 更新操作。
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

    /**
     *   此处可以正常获取到 流程节点组件的数据
     * @param sysname
     * @param fileName
     * @param hasTimestamp
     * @return
     * @throws IOException
     */
    public String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        String cfgXml = null;
        CfgFile queryCfgFile = new CfgFile();
        queryCfgFile.setNodeType(NodeType.NAMENODE.name());
        queryCfgFile.setSystem("UNDEFINED");
        queryCfgFile.setFileName(fileName);
        CfgFile cfgFile = findOne(queryCfgFile);
        if (cfgFile != null){
            cfgXml = cfgFile.getContent(); // 获取流程组件数据
        }

        if (cfgXml == null) {
            // 此处为数据为空的时候时候会走默认的 组件
            cfgXml = EmptyCfgXmlHelper.getEmptyXml(fileName);
        }
        return nodeCfgSyncService.generateSyncCfgXml(sysname, fileName, cfgXml, hasTimestamp); // 此处的cfgXml 文件是正常的 页面传过来的正常数据
    }

    public <T extends BaseModel> T loadModel4Name(String cfgFileName, Class<? extends T> tclass) {

        CfgFile cfgFile = new CfgFile();
        cfgFile.setFileName(cfgFileName);
        cfgFile.setNodeType(NodeType.NAMENODE.name());
        cfgFile.setSystem("UNDEFINED");
        // 根据cfgFile 对象的内容查找数据 ，有则返回 1 或者 1+ 没有就是0
        List<CfgFile> list = findList(cfgFile);
        String xml;
        /* 这一步生产xml 文件时 */
        if (list.size() == 0) xml = EmptyCfgXmlHelper.getEmptyXml(cfgFileName);
        else xml = CharsetUtil.convertFromUTF8(list.get(0).getContent()); /* 读取list 中的数据，并设置字符集 */
        Document doc = XMLDealTool.readXml(xml);
        T model = XmlBeanUtil.toEntity(XMLDealTool.getRootXml(doc), tclass);
        model.init();
        return model;
    }

    /*
        保存进xml 文件内
     */
    public <T extends BaseModel> void saveModel4Name(String cfgFileName, T model) {
        logger.info(String.valueOf(model));
        String xml = XmlBeanUtil.toXml(model);
        CfgFile cfgFile = new CfgFile(); //文件属性类
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
        /*baocun flow.xml*/
        target.setContent(CharsetUtil.convertToUTF8(xml));
        save(target);
    }
}
