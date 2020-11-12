package com.dcfs.esb.ftp.common.msg;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 文件服务器消息头的XML转换工具
 */
public class FileXmlUtil {
    protected FileXmlUtil() {
    }

    /**
     * 文件服务器通讯系统头转换，转换成HashMap
     *
     * @param xml XML数据
     * @return 转换成Hash表，
     * @throws FtpException
     */
    public static Map<String, String> parseXml(String xml) throws FtpException {

        HashMap<String, String> map = new HashMap<String, String>();
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new InputSource(new StringReader(xml)));
            Element root = doc.getRootElement();
            Element e = null;
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                e = (Element) i.next();
                if (e.isTextOnly()) {
                    map.put(e.getName(), e.getText());
                }
            }
            return map;
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.HEAD_XML_ERROR, e);
        }
    }

    /**
     * 文件服务器通讯系统头转换，转换成XML
     *
     * @param map Hash表数据
     * @return XML字符串
     */
    public static String createXML(Map<String, String> map) {
        String xml = null;
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("FileRoot");
        for (Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            root.addElement(entry.getKey()).addText(entry.getValue());
        }
        xml = doc.asXML();
        return xml;
    }
}
