package com.dcfs.esb.ftp.server.tool;

import net.sf.json.JSON;
import net.sf.json.JSONNull;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.Element;

public class XMLtoJSON {
    private XMLtoJSON() {
    }

    public static String getJSONFromXMLStr(String xmlStr) {
        String xmlWithoutNotation = xmlStr.replaceAll("\\<\\!\\-\\-.*?\\-\\-\\>", "");
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setTypeHintsCompatibility(false);
        xmlSerializer.setTypeHintsEnabled(false);
        JSON json = xmlSerializer.read(xmlWithoutNotation);
        if (json instanceof JSONNull) return null;
        return json.toString();
    }

    public static String getJSONFromXMLEle(Element xmlEle) {
        String xmlStr = xmlEle.asXML();
        return getJSONFromXMLStr(xmlStr);
    }

    public static String getJSONFromXMLDoc(Document doc) {

        String xmlStr = doc.asXML();
        return getJSONFromXMLStr(xmlStr);
    }

    public static String wrapToArray(String objJsonStr) {
        if (objJsonStr != null && objJsonStr.startsWith("{")) return "[" + objJsonStr + "]";
        else return objJsonStr;
    }
}
