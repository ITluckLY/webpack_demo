package com.dcfs.esb.ftp.namenode.helper;

import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import org.dom4j.Document;

/**
 * Created by mocg on 2016/11/30.
 */
public class EmptyCfgXmlHelper {
    private EmptyCfgXmlHelper() {
    }

    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final String EMPTY_XML = XML_HEAD + "<root></root>";//NOSONAR

    private static final String COMPONENTS = XML_HEAD +
            "<services timestamp=\"1477472443897\">\n" +
            "\n" +
            "    <service name=\"IsolStateCheckService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.IsolStateCheckService</implement>\n" +
            "        <params/>\n" +//NOSONAR
            "    </service>\n" +//NOSONAR
            "\n" +
            "    <service name=\"ApiVersionCheckService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.ApiVersionCheckService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "\n" +
            "    <service name=\"IPCheckService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.IPCheckService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "\n" +
            "    <service name=\"PwdAuthWithSeqService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.PwdAuthWithSeqService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "\n" +
            "    <service name=\"PushDataNodeListService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.PushDataNodeListService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "\n" +
            "    <service name=\"InitService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.InitService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "\n" +
            "    <service name=\"FileRenameService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.FileRenameService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "\n" +
            "    <service name=\"ResourceCtrlService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.ResourceCtrlService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "\n" +
            "    <service name=\"FileQueryService\" describe=\"\">\n" +
            "        <implement>com.dcfs.esb.ftp.impls.service.FileQueryService</implement>\n" +
            "        <params/>\n" +
            "    </service>\n" +
            "</services>";
    private static final String CRONTAB = XML_HEAD + "<schedules></schedules>";
    private static final String FILE = XML_HEAD + "<FileRoot></FileRoot>";
    private static final String FILE_CLEAN = XML_HEAD + "<root></root>";
    private static final String FILE_RENAME = XML_HEAD + "<root></root>";
    private static final String FLOW = XML_HEAD +
            "<flows>\n" +
            "    <flow name=\"DefStandardFlow\"\n" +
            "          components=\"IsolStateCheckService,IPCheckService,PwdAuthWithSeqService,PushDataNodeListService,InitService,FileRenameService,ResourceCtrlService,FileQueryService\"\n" +
            "          describe=\"默认流程\">*</flow>\n" +
            "    <flow name=\"FullCheckFlow\"\n" +
            "          components=\"IsolStateCheckService,ApiVersionCheckService,IPCheckService,PwdAuthWithSeqService,PushDataNodeListService,InitService,FileRenameService,ResourceCtrlService,FileQueryService\"\n" +
            "          describe=\"全部流程，包含API版本校验\">*</flow>\n" +
            "</flows>";
    private static final String NODES = XML_HEAD + "<nodes></nodes>";
    private static final String ROUTE = XML_HEAD + "<rules></rules>";
    private static final String RULE = XML_HEAD + "<RuleRoot></RuleRoot>";
    private static final String SERVICES_INFO = XML_HEAD + "<services></services>";
    private static final String SYSTEM = XML_HEAD + "<systems></systems>";
    private static final String USER = XML_HEAD + "<UserRoot></UserRoot>";

    public static Document getEmptyDoc(String fileName) {
        fileName = fileName.toLowerCase();//NOSONAR
        String xml = null;
        if ("components.xml".equals(fileName)) xml = COMPONENTS;
        else if ("crontab.xml".equals(fileName)) xml = CRONTAB;
        else if ("file.xml".equals(fileName)) xml = FILE;
        else if ("file_clean.xml".equals(fileName)) xml = FILE_CLEAN;
        else if ("file_rename.xml".equals(fileName)) xml = FILE_RENAME;
        else if ("flow.xml".equals(fileName)) xml = FLOW;
        else if ("nodes.xml".equals(fileName)) xml = NODES;
        else if ("route.xml".equals(fileName)) xml = ROUTE;
        else if ("rule.xml".equals(fileName)) xml = RULE;
        else if ("services_info.xml".equals(fileName)) xml = SERVICES_INFO;
        else if ("system.xml".equals(fileName)) xml = SYSTEM;
        else if ("user.xml".equals(fileName)) xml = USER;
        if (xml == null) xml = EMPTY_XML;
        return XMLDealTool.readXml(xml);
    }
}
