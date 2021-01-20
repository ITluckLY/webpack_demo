package com.dcfs.esb.ftp.server.tool;

import com.dcfs.esb.ftp.common.cons.EncodingCons;
import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.CfgDocServiceFace;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


public class XMLDealTool {
    private static final Logger log = LoggerFactory.getLogger(XMLDealTool.class);

    private XMLDealTool() {
    }

    public static Document readXmlFile(String filePath) {
        File file = new File(filePath);
        return readXml(file);
    }

    public static Document readXml(File xmlFile) {
        SAXReader reader = new SAXReader();
        InputStreamReader fr = null;
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            fr = new InputStreamReader(fis, EncodingCons.CFG_CONTENT_CHARSET);
            return reader.read(fr);
        } catch (DocumentException e) {
            log.error("XML文件读取出错", e);
            throw new NestedRuntimeException("XML文件读取出错", e);
        } catch (FileNotFoundException e) {
            log.error("XML文件不存在", e);
            throw new NestedRuntimeException("XML文件不存在", e);
        } catch (IOException e) {
            log.error("XML文件IO异常", e);
            throw new NestedRuntimeException("XML文件IO异常", e);
        } finally {
            IOUtils.closeQuietly(fr);
        }
    }

    public static Document readXml(String xml) {
        Document doc = null;
        SAXReader reader = new SAXReader();
        try {
            doc = reader.read(new StringReader(xml));
        } catch (DocumentException e) {
            log.error("XML文本读取出错", e);
            throw new NestedRuntimeException("XML文本读取出错", e);
        }
        return doc;
    }

    public static Document withoutTimestamp(Document doc) {
        Element root = doc.getRootElement();
        Attribute timestamp = root.attribute("timestamp");
        if (timestamp != null) root.remove(timestamp);
        return doc;
    }

    public static Element getRoot(Document doc) {
        return doc.getRootElement();
    }

    //	查找当前节点下所有子节点
    public static List getChildNodes(Element element) {
        return element.elements();
    }

    //	当前节点下添加子节点
    public static Element addChild(String name, Element element) {
        return element.addElement(name);
    }

    //	添加属性
    public static void addProperty(String proName, String proContent, Element element) {
        element.addAttribute(proName, proContent);
    }

    //	更新节点值
    public static void updateNode(String content, Element element) {
        element.setText(content);
    }

    //	写入XML
    public static String xmlWriter(Document doc, String configFilepath) {
        ResultDto<Void> resultDto = xmlWriter2(doc, configFilepath);
        return ResultDtoTool.toJson(resultDto);
    }

    public static ResultDto<Void> xmlWriter2(Document doc, String configFilepath) {//NOSONAR
        NodeType nodeType = Cfg.getNodeType();
        try {
            if (NodeType.NAMENODE.equals(nodeType)) {
                String fileName = FilenameUtils.getName(configFilepath);
                if (fileName.equals(Cfg.SYS_CFG)) return writerXml2(doc, new File(configFilepath));
                if (fileName.equals(Cfg.COMPONENTS_CFG)) return writerDB(doc, Cfg.COMPONENTS_CFG);
                if (fileName.equals(Cfg.CRONTAB_CFG)) return writerDB(doc, Cfg.CRONTAB_CFG);
                if (fileName.equals(Cfg.LB_CFG)) return writerDB(doc, Cfg.LB_CFG);
                if (fileName.equals(Cfg.FILE_CFG)) return writerDB(doc, Cfg.FILE_CFG);
                if (fileName.equals(Cfg.FILE_CLEAN_CFG)) return writerDB(doc, Cfg.FILE_CLEAN_CFG);
                if (fileName.equals(Cfg.FLOW_CFG)) return writerDB(doc, Cfg.FLOW_CFG);
                if (fileName.equals(Cfg.PSFLOW_CFG)) return writerDB(doc, Cfg.PSFLOW_CFG); /*// 校验流程*/
                if (fileName.equals(Cfg.NODES_CFG)) return writerDB(doc, Cfg.NODES_CFG);
                if (fileName.equals(Cfg.ROUTE_CFG)) return writerDB(doc, Cfg.ROUTE_CFG);
                if (fileName.equals(Cfg.RULE_CFG)) return writerDB(doc, Cfg.RULE_CFG);
                if (fileName.equals(Cfg.SERVICES_INFO_CFG)) return writerDB(doc, Cfg.SERVICES_INFO_CFG);
                if (fileName.equals(Cfg.SYSTEM_CFG)) return writerDB(doc, Cfg.SYSTEM_CFG);
                if (fileName.equals(Cfg.USER_CFG)) return writerDB(doc, Cfg.USER_CFG);
                if (fileName.equals(Cfg.FILE_RENAME_CFG)) return writerDB(doc, Cfg.FILE_RENAME_CFG);
                if (fileName.equals(Cfg.VSYS_MAP_CFG)) return writerDB(doc, Cfg.VSYS_MAP_CFG);
                if (fileName.equals(Cfg.CLIENT_STATUS_CFG)) return writerDB(doc, Cfg.CLIENT_STATUS_CFG);
                if (fileName.equals(Cfg.NETTY_CFG)) return writerDB(doc, Cfg.NETTY_CFG);
                if (fileName.equals(Cfg.KEY_CFG)) return writerDB(doc, Cfg.KEY_CFG);

            } else {
                return writerXml2(doc, new File(configFilepath));
            }
        } catch (IOException e) {
            log.error("XML配置文件更新失败", e);//NOSONAR
            return ResultDtoTool.buildError("XML配置文件更新失败");
        }
        return ResultDtoTool.buildError("XML配置文件更新失败");
    }

    public static ResultDto<Void> writerXml2(Document doc, File configFile) {
        ResultDto<Void> resultDto = null;
        XMLWriter writer = null;
        OutputFormat format = createXmlFormat();
        format.setEncoding(EncodingCons.CFG_CONTENT_ENCODING);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            OutputStreamWriter or = new OutputStreamWriter(fos, EncodingCons.CFG_CONTENT_ENCODING);
            writer = new XMLWriter(or, format);
            writer.write(doc);
            writer.flush();
            resultDto = ResultDtoTool.buildSucceed("操作成功", null);
        } catch (IOException e) {
            log.error("XML配置文件写入出错", e);
            resultDto = ResultDtoTool.buildError("操作XML配置文件异常");
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                log.error("关闭XML配置文件出错", e);
            }
        }
        return resultDto;
    }

    public static boolean writerXml(Document doc, String configFilePath) {
        return writerXml(doc, new File(configFilePath));
    }

    public static boolean writerXml(Document doc, File configFile) {
        XMLWriter writer = null;
        OutputFormat format = createXmlFormat();
        format.setEncoding(EncodingCons.CFG_CONTENT_ENCODING);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            OutputStreamWriter or = new OutputStreamWriter(fos, EncodingCons.CFG_CONTENT_ENCODING);
            writer = new XMLWriter(or, format);
            writer.write(doc);
            writer.flush();
        } catch (IOException e) {
            log.error("XML配置文件写入出错", e);
            return false;
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                log.error("关闭XML配置文件出错", e);
            }
        }
        return true;
    }

    public static ResultDto<Void> writerDB(Document doc, String filename) throws IOException {
        CfgDocServiceFace cfgDocServiceFace = SpringContext.getInstance().getCfgDocServiceFace();
        String xml = format(doc);
        return cfgDocServiceFace.setCfgDoc(xml, filename, null);
    }

    //打印缓存中的XML
    public static ResultDto<String> printXML(String cfgPath) {
        String cfgName = FilenameUtils.getName(cfgPath);
        String str = null;
        try {
            Document doc = CachedCfgDoc.getInstance().load(cfgName);
            if (doc != null) str = doc.asXML();
        } catch (Exception e) {
            log.error("打印XML", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    //打印文件中的XML
    public static ResultDto<String> printXMLFile(String cfg) {
        File file = new File(cfg);
        String str = null;
        try {
            Document document = XMLDealTool.readXml(file);
            if (document != null) str = document.asXML();
        } catch (Exception e) {
            log.error("打印XML", e);
            return ResultDtoTool.buildError(e.getMessage());
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public static boolean remove(Element element) {
        return element.getParent().remove(element);
    }

    public static void remove(List<Element> elements) {
        for (Element element : elements) {
            remove(element);
        }
    }

    public static Document cloneDoc(Document doc) {
        return (Document) doc.clone();
    }

    public static OutputFormat createXmlFormat() {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(EncodingCons.CFG_CONTENT_ENCODING);
        format.setIndent(true);
        format.setIndent("    ");
        format.setNewlines(true);
        return format;
    }

    public static String format(Document doc) throws IOException {
        OutputFormat format = createXmlFormat();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter or = new OutputStreamWriter(bos, EncodingCons.CFG_CONTENT_CHARSET);
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(or, format);
            writer.write(doc);
            writer.flush();
        } finally {
            if (writer != null) writer.close();
        }
        return new String(bos.toByteArray(), EncodingCons.CFG_CONTENT_ENCODING);
    }
}
