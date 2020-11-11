package com.dc.smarteam.tool;

import net.sf.json.JSON;
import net.sf.json.JSONNull;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.Element;

public class XMLtoJSON {

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

    public static void main(String[] args) {
//		String json = getXMLtoJSON("asdf");
//		Document d = null;
        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<configuration status=\"OFF\">\n" +
                "    <appenders>\n" +
                "        <Console name=\"Console\" target=\"SYSTEM_OUT\">\n" +
                "            <PatternLayout pattern=\"%d{HH:mm:ss.SSS} [%t] %-5level %class{36} %L %M - %msg%n\"/>\n" +
                "        </Console>\n" +
                "        <RollingFile name=\"rollingFile\" fileName=\"log/dcfs.log\"\n" +
                "                     filePattern=\"log/$${date:yyyy-MM}/dcfs-%d{MM-dd-yyyy}-%i.log.gz\">\n" +
                "            <PatternLayout pattern=\"%d{yyyy.MM.dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n\"/>\n" +
                "            <Policies>\n" +
                "                <TimeBasedTriggeringPolicy interval=\"1\" modulate=\"true\"/>\n" +
                "                <SizeBasedTriggeringPolicy size=\"100 MB\"/>\n" +
                "            </Policies>\n" +
                "        </RollingFile>\n" +
                "        <RollingFile name=\"rollingFile_all\" fileName=\"log/all.log\"\n" +
                "                     filePattern=\"log/$${date:yyyy-MM}/all-%d{MM-dd-yyyy}-%i.log.gz\">\n" +
                "            <PatternLayout pattern=\"%d{yyyy.MM.dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n\"/>\n" +
                "            <Policies>\n" +
                "                <TimeBasedTriggeringPolicy interval=\"1\" modulate=\"true\"/>\n" +
                "                <SizeBasedTriggeringPolicy size=\"100 MB\"/>\n" +
                "            </Policies>\n" +
                "        </RollingFile>\n" +
                "    </appenders>\n" +
                "    <loggers>\n" +
                "        <logger name=\"com.dcfs\" level=\"info\" additivity=\"true\">\n" +
                "            <appender-ref ref=\"rollingFile\"/>\n" +
                "        </logger>\n" +
                "        <logger name=\"org.springframework\" level=\"error\" additivity=\"true\">\n" +
                "            <!--<appender-ref ref=\"Console\" />-->\n" +
                "        </logger>\n" +
                "        <logger name=\"org.apache\" level=\"error\" additivity=\"true\">\n" +
                "            <!--<appender-ref ref=\"Console\" />-->\n" +
                "        </logger>\n" +
                "        <logger name=\"com.dcfs.esc.ftp.clisvr.crontab.job\" level=\"info\" additivity=\"true\">\n" +
                "        </logger>\n" +
                "        <logger name=\"com.dcfs.esc.ftp.clisvr.service.NetworkSpeedComputeService\" level=\"info\" additivity=\"true\">\n" +
                "        </logger>\n" +
                "        <root level=\"info\">\n" +
                "            <appender-ref ref=\"Console\"/>\n" +
                "            <appender-ref ref=\"rollingFile_all\"/>\n" +
                "        </root>\n" +
                "    </loggers>\n" +
                "</configuration>\n";
        System.out.println(getJSONFromXMLStr(str));

//		System.out.println(json);
    }
}
