package com.dcfs.esb.ftp.namenode.service;

import com.dcfs.esb.ftp.common.cons.EncodingCons;
import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.namenode.helper.EmptyCfgXmlHelper;
import com.dcfs.esb.ftp.namenode.spring.NameSpringContext;
import com.dcfs.esb.ftp.namenode.spring.core.entity.biz.CfgFile;
import com.dcfs.esb.ftp.namenode.spring.core.repository.CfgFileRepository;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CfgDocServiceFace;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.utils.EmptyUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;

import java.util.List;

/**
 * Created by huangzbb on 2016/9/2.
 */
public class NodeCfgDocService implements CfgDocServiceFace {
    private static NodeCfgDocService ourInstance = new NodeCfgDocService();

    public static NodeCfgDocService getInstance() {
        return ourInstance;
    }


    public Document getCfgDoc(String filename, String system) {
        Document doc;
        if (system == null) {
            system = "UNDEFINED";//NOSONAR
        }
        CfgFileRepository cfgFileRepository = NameSpringContext.getInstance().getCfgFileRepository();
        List<CfgFile> cfgFileList = cfgFileRepository.findByFilenameAndSystemAndNodetype(filename, system, NodeType.NAMENODE.name());
        CfgFile cfgFile;
        if (EmptyUtils.isNotEmpty(cfgFileList)) {
            cfgFile = cfgFileList.get(0);
            //byte[] bytes = cfgFile.getContent();//NOSONAR
            String str = cfgFile.getContent();
            doc = XMLDealTool.readXml(str);
            XMLDealTool.withoutTimestamp(doc);
            return doc;
        }
        return EmptyCfgXmlHelper.getEmptyDoc(filename);
    }

    public ResultDto<Void> setCfgDoc(String docXml, String filename, String system) {
        if (docXml == null) return ResultDtoTool.buildError("写入配置文件不能为空");

        if (system == null) system = "UNDEFINED";//NOSONAR
        byte[] bytes = docXml.getBytes(EncodingCons.CFG_CONTENT_IN_DB_CHARSET);
        int length = bytes.length;
        String nodetype = NodeType.NAMENODE.name();
        String filetype = FilenameUtils.getExtension(filename);
        CfgFileRepository cfgFileRepository = NameSpringContext.getInstance().getCfgFileRepository();
        List<CfgFile> cfgFileList = cfgFileRepository.findByFilenameAndSystemAndNodetype(filename, system, nodetype);
        CfgFile cfgFile;
        if (EmptyUtils.isNotEmpty(cfgFileList)) {
            cfgFile = cfgFileList.get(0);
        } else {
            cfgFile = new CfgFile();
        }
        cfgFile.setContent(docXml);
        cfgFile.setFilesize(length);
        cfgFile.setFiletype(filetype);
        cfgFile.setSystem(system);
        cfgFile.setFilename(filename);
        cfgFile.setNodetype(nodetype);
        cfgFileRepository.save(cfgFile);
        return ResultDtoTool.buildSucceed(null);
    }
}
