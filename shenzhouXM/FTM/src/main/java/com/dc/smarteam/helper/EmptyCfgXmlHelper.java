package com.dc.smarteam.helper;

import com.dc.smarteam.util.XMLDealTool;
import org.dom4j.Document;

/**
 * 设置默认的xml属性
 * Created by mocg on 2016/11/30.
 */
public class EmptyCfgXmlHelper {
  private EmptyCfgXmlHelper() {
  }

  private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
  private static final String EMPTY_XML = XML_HEAD + "<root></root>";//NOSONAR

  private static final String COMPONENTS = XML_HEAD +
      "<services>\n" +
      "\n" +
      "    <service name=\"IsolStateCheckService\" describe=\"隔离校验组件：隔离节点不接受传输请求\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.IsolStateCheckService</implement>\n" +
      "        <params/>\n" +//NOSONAR
      "    </service>\n" +//NOSONAR
      "\n" +
      "    <service name=\"ApiVersionCheckService\" describe=\"Api版本校验组件：API版本不同不接受传输请求\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.ApiVersionCheckService</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"IPCheckService\" describe=\"IP地址校验组件：配合IP校验开关使用，非法IP不接受传输请求\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.IPCheckService</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"PwdAuthWithSeqService\" describe=\"用户名密码校验组件\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.PwdAuthWithSeqService</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"PushDataNodeListService\" describe=\"节点列表下发组件：数据节点校验nodeList.version，不一致时主动下发nodeList至API客户端\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.PushDataNodeListService</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"InitService\" describe=\"初始校验组件：包含文件大小超限校验、节点组不一致校验等\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.InitService</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"FileRenameService\" describe=\"文件重名命名组件：根据命名控制对文件进行重命名操作。对于指定目录下没有权限使用原文件名的，不接受传输请求\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.FileRenameService</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"ResourceCtrlService\" describe=\"资源控制组件：只支持有交易码的请求，根据交易码优先级进行资源控制\">\n" +
      "        <implement>com.dcfs.esb.ftp.impls.service.ResourceCtrlService</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"FileQueryService\" describe=\"文件查询组件：根据文件名称，选择最合适的节点返回给API客户端\">\n" +
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
      "    <flow name=\"FilesProcesFlow\"\n" +
      "          components=\"ResouceCtrlBySetCliHandler,UploadDecryptHandler,UploadDecompressHandler,SaveUploadFileByEsbFileHandler,FileDistributeByTailHandler,FileRouteHandler,SyncDistributeUploadHandler,LogOnFinishHandler\"\n" +
      "          describe=\"全部流程，包含API版本校验\">*</flow>\n" +
      "</flows>";
  private static final String NODES = XML_HEAD + "<nodes></nodes>";
  private static final String ROUTE = XML_HEAD + "<rules></rules>";
  private static final String RULE = XML_HEAD + "<RuleRoot></RuleRoot>";
  private static final String SERVICES_INFO = XML_HEAD + "<services></services>";
  private static final String SYSTEM = XML_HEAD + "<systems></systems>";
  private static final String USER = XML_HEAD + "<UserRoot></UserRoot>";
  private static final String CLIENT_STATUS = XML_HEAD + "<clients></clients>";
  private static final String FILE_PROCESS = XML_HEAD +
      "<services>\n" +
      "\n" +
      "    <service name=\"ResouceCtrlBySetCliHandler\" describe=\"设置资源处理流程\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.ResouceCtrlBySetCliHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"UploadDecryptHandler\" describe=\"上传解密处理程序\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.UploadDecryptHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"UploadDecompressHandler\" describe=\"上传解压缩处理程序\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.UploadDecompressHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"SaveUploadFileByEsbFileHandler\" describe=\"通过Esb文件处理程序保存上传文件\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.SaveUploadFileByEsbFileHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"FileDistributeByTailHandler\" describe=\"通过尾部处理程序分发文件\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.FileDistributeByTailHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"FileRouteHandler\" describe=\"文件路由处理程序\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.FileRouteHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"SyncDistributeUploadHandler\" describe=\"同步分发上传处理程序\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.SyncDistributeUploadHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "\n" +
      "    <service name=\"LogOnFinishHandler\" describe=\"登录完成处理程序\">\n" +
      "        <implement>com.dcfs.esb.ftp.process.handler.LogOnFinishHandler</implement>\n" +
      "        <params/>\n" +
      "    </service>\n" +
      "</services>";

  // 新增netty.xml        "    <prarm  />\n" +
  private static final String NETTY_FLIE = XML_HEAD +
      "<baseConfig>\n" +
      "  <nettyConfig >\n" +

      "    <channelSpeed >\n" +
      "    </channelSpeed>\n" +
      "  </nettyConfig >\n" +
      "</baseConfig>";
  private static final String ADD_NETTY_FLIE = XML_HEAD + "<baseConfig></baseConfig>";
  private static final String INIT_NETTY_FLIE = XML_HEAD + "<baseConfig></baseConfig>";
  /*
 "<baseConfig>\n" +
      "  <nettyConfig >\n" +
      "    <prarm maxSpeed='' sleepTime='' scanTime='' />\n" +
      "    <channelSpeed >\n" +
      "       <channel userName='' readlimit='' wrtelimit='' ></channel>\n" +
      "    </channelSpeed>\n" +
      "  </nettyConfig >\n" +
      "</baseConfig>";
      */
  private static final String INITP_NETTY_FLIE = XML_HEAD +
      "<baseConfig>\n" +
      "  <nettyConfig >\n" +

      "  </nettyConfig >\n" +
      "</baseConfig>";
  private static final String INITC_NETTY_FLIE = XML_HEAD +
      "<baseConfig>\n" +
      "  <nettyConfig >\n" +
      "    <channelSpeed >\n" +
      "    </channelSpeed>\n" +
      "  </nettyConfig >\n" +
      "</baseConfig>";

  private static final String KEY_FLIE = XML_HEAD +
      "<keys>\n" +
      "</keys>";

  public static String getEmptyXml(String fileName) {
    fileName = fileName.toLowerCase();
    String xml = null;
    if ("components.xml".equals(fileName)) xml = COMPONENTS; /* 这个是 DefStandardFlow 和 FullCheckFlow 流程组件信息 */
    else if ("file_process.xml".equals(fileName)) xml = FILE_PROCESS; /* 这个是  file_process 流程组件信息 */
    else if ("crontab.xml".equals(fileName)) xml = CRONTAB;
    else if ("file.xml".equals(fileName)) xml = FILE;
    else if ("file_clean.xml".equals(fileName)) xml = FILE_CLEAN;
    else if ("file_rename.xml".equals(fileName)) xml = FILE_RENAME;
    else if ("flow.xml".equals(fileName)) xml = FLOW;   /* 此处拼接 上下文的 组件信息  ？？？？？？？？？？？？？？？？？？？？*/
    else if ("nodes.xml".equals(fileName)) xml = NODES;
    else if ("route.xml".equals(fileName)) xml = ROUTE;
    else if ("rule.xml".equals(fileName)) xml = RULE;
    else if ("services_info.xml".equals(fileName)) xml = SERVICES_INFO;
    else if ("system.xml".equals(fileName)) xml = SYSTEM;
    else if ("user.xml".equals(fileName)) xml = USER;
    else if ("client_status.xml".equals(fileName)) xml = CLIENT_STATUS;
    else if ("netty.xml".equals(fileName)) xml = NETTY_FLIE;
    else if ("initnetty.xml".equals(fileName)) xml = INIT_NETTY_FLIE;
    else if ("initpnetty.xml".equals(fileName)) xml = INITP_NETTY_FLIE;
    else if ("initcnetty.xml".equals(fileName)) xml = INITC_NETTY_FLIE;
    else if ("keys.xml".equals(fileName)) xml = KEY_FLIE;
    if (xml == null) xml = EMPTY_XML;
    return xml;
  }

  public static Document getEmptyDoc(String fileName) {
    return XMLDealTool.readXml(getEmptyXml(fileName));
  }
}
