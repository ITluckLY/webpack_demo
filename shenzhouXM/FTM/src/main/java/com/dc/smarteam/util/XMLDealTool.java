package com.dc.smarteam.util;

import com.dc.smarteam.cons.EncodingCons;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;


public class XMLDealTool {

    private static Logger log = Logger.getLogger(XMLDealTool.class);

    //	初始化xml配置文件,获取root
    public static Document XMLConnection(String targetPath) {
        File file = new File(targetPath);
        return readXml(file);
    }

    public static Document readXml(File xmlFile) {
        SAXReader reader = new SAXReader();
        InputStreamReader fr = null;
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            fr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            return reader.read(fr);
        } catch (DocumentException e) {
            log.error("XML文件读取出错", e);
        } catch (FileNotFoundException e) {
            log.error("XML文件不存在", e);
        } catch (IOException e) {
            log.error("XML文件IO异常", e);
        } finally {
            IOUtils.closeQuietly(fr);
        }
        return null;
    }

    public static Document readXml(String xml) {
        Document doc = null;
        SAXReader reader = new SAXReader();
        try {
            doc = reader.read(new StringReader(xml));
        } catch (DocumentException e) {
            log.error("XML文本读取出错", e);
            throw new RuntimeException("XML文本读取出错");
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

    public static String getRootXml(Document doc) {
        return doc.getRootElement().asXML();
    }

    //	查找当前节点下所有子节点
    public static List getChildNodes(Element element) {
        return element.elements();
    }

    //	按条件查找当前节点下的某个节点
    public static void getNode() {

    }

    //	当前节点下添加子节点
    public static Element addChild(String name, Element element) {
        return element.addElement(name);
    }

    //	删除节点
    public static void delNode() {

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
    public static String XMLWriter(Document doc, String configFilepath) {
        return writerXml(doc, new File(configFilepath));
    }

    public static String writerXml(Document doc, File configFile) {
        String str = null;
        XMLWriter writer = null;
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            OutputStreamWriter or =
                    new OutputStreamWriter(fos, "utf-8");
            writer = new XMLWriter(or, format);

            writer.write(doc);
            str = "{\"code\":\"0000\",\"message\":\"操作成功\"}";
        } catch (IOException e) {
            log.error("XML配置文件写入出错", e);
            str = "{\"code\":\"9999\",\"message\":\"操作XML配置文件异常\"}";
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                log.error("关闭XML配置文件出错", e);
            }
        }
        return str;
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
        } finally {
            if (writer != null) writer.close();
        }
        return new String(bos.toByteArray(), EncodingCons.CFG_CONTENT_ENCODING);
    }
}
