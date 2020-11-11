package com.dc.smarteam.util;

import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpErrCode;
import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 文件服务器消息头的XML转换工具
 */
public class FileXmlUtil {
//	public static SAXReader reader = new SAXReader();

    /**
     * 文件服务器通讯系统头转换，转换成HashMap
     *
     * @param xml XML数据
     * @return 转换成Hash表，
     * @throws FtpException
     */
    public static HashMap<String, String> parseXml(String xml) throws FtpException {

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
     * @throws FtpException
     */
    public static String createXML(HashMap<String, String> map) throws FtpException {
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
