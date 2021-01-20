package com.dcfs.esb.ftp.server.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.impls.service.*;
import com.dcfs.esb.ftp.interfases.service.GeneralService;
import com.dcfs.esb.ftp.servcomm.model.ServiceInfo;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.java2d.loops.ProcessPath;


import java.util.*;

public class ServiceContainer {
  private static final Logger log = LoggerFactory.getLogger(ServiceContainer.class);
  private static final Object lock = new Object();
  private static ServiceContainer instance;
  /*components.xml中定义的基础流程*/
  private Map<String, GeneralService> base;
  /*内置的基础流程*/
  private Map<String, GeneralService> innerBase;
  /*key:交易码 value:services_info.xml中定义的流程*/
  private Map<String, String> flows;
  /*key:系统名称_交易码 value:services_info.xml中定义的流程*/
  private Map<String, String> flowsBySys;


  /*components.xml中定义的校验流程*/
  private Map<String, ProcessPath.ProcessHandler> initBase;
  /*ps 内置的校验流程*/
  private Map<String, ProcessPath.ProcessHandler> initHandler;
  /*key:交易码 value:services_info.xml中定义的流程  中的 psFlow */
  private Map<String, String> psFlows;
  /*key:系统名称_交易码 value:services_info.xml中定义的流程  中的 psFlow */
  private Map<String, String> psFlowBySys;
  // 设置 handler 和 路径
  private Map<String, String> addHanlerModel;


  //key sysname_trancode
  private Map<String, List<PutAuthUser>> putAuthUserListMap;
  //key sysname_trancode
  private Map<String, List<GetAuthUser>> getAuthUserListMap;
  private Map<String, ServiceInfo> serviceInfoMap;

  private ServiceContainer() {
    initBaseService();
    initInnerBaseService();
    initServiceFlow();
  }

  public static ServiceContainer getInstance() {
    if (instance != null) return instance;
    synchronized (lock) {
      if (instance == null) instance = new ServiceContainer();
    }
    return instance;
  }

  public static void reload() {
    try {
      CachedCfgDoc.getInstance().reloadComponents();
      CachedCfgDoc.getInstance().reloadServicesInfo();
    } catch (FtpException e) {
      throw new NestedRuntimeException(e.getMessage(), e);
    }
    synchronized (lock) {
      instance = new ServiceContainer();
    }
  }


  /**
   * 加载所有基础服务
   */
  // *****
  private void initBaseService() {
    Map<String, GeneralService> tmpbase = new HashMap<>();
    Map<String, String> hdMap = new HashMap<>();
    Map<String, ProcessPath.ProcessHandler> tmpbaseHandler = new HashMap<>();
    try {
      Document doc = CachedCfgDoc.getInstance().loadComponents();
      List l = doc.selectNodes("/services/service");
      for (Object aL : l) {
        String name = null;
        Map<String, String> params = new HashMap<>();
        String impl = null;
        try {
          Element e = (Element) aL;
          name = e.attributeValue("name");  // 节点 名称。
          impl = e.element("implement").getText().trim();//  节点的实现类
          Element paramsEle = e.element("params");
          if (paramsEle != null) {
            for (Object aP : paramsEle.elements()) {
              Element item = (Element) aP;
              params.put(item.getText().trim(), item.getText().trim());
            }
          }
          if (name.endsWith("Handler")) {
            //  ProcessPath.ProcessHandler p = (ProcessPath.ProcessHandler) Class.forName(impl).newInstance(); // 找不到这个位置 com.dcfs.esb.ftp.process.handler.UploadDecompressHandler
            //p.start(null);  设置null 会报空指针异常 .
            // tmpbaseHandler.put(name, p);

            hdMap.put(name, impl);
            log.info("成功载入校验服务:{}", name + " : " + impl);
          } else if (name.endsWith("Service")) {
            GeneralService s = (GeneralService) Class.forName(impl).newInstance();
            s.start(params);
            tmpbase.put(name, s);
            log.info("成功载入基础服务:{}", s);
          }

        } catch (Exception e) {
          log.error("加载基础服务" + name + "失败#clsass:" + impl, e);
        }
      }
    } catch (Exception e) {
      log.error("基础服务容器初始化失败", e);
      throw new NestedRuntimeException("基础服务容器初始化失败", e);
    }
    this.base = tmpbase;
    this.initBase = tmpbaseHandler;
    this.addHanlerModel = hdMap;
  }

  // *****
  private void initInnerBaseService() {
    Map<String, GeneralService> tmpinnerBase = new HashMap<>();
    try {
      //前置处理
      //IsolStateCheckService
      IsolStateCheckService isolStateCheckService = new IsolStateCheckService();
      isolStateCheckService.start(null);
      tmpinnerBase.put("IsolStateCheckService", isolStateCheckService);
      //ApiVersionCheckService
      ApiVersionCheckService apiVersionCheckService = new ApiVersionCheckService();
      apiVersionCheckService.start(null);
      tmpinnerBase.put("ApiVersionCheckService", apiVersionCheckService);
      //IPCheckService
      IPCheckService ipCheckService = new IPCheckService();
      ipCheckService.start(null);
      tmpinnerBase.put("IPCheckService", ipCheckService);
      //PwdAuthService
      PwdAuthService pwdAuthService = new PwdAuthService();
      pwdAuthService.start(null);
      tmpinnerBase.put("PwdAuthService", pwdAuthService);
      //pwdAuthWithSeqService
      PwdAuthWithSeqService pwdAuthWithSeqService = new PwdAuthWithSeqService();
      pwdAuthWithSeqService.start(null);
      tmpinnerBase.put("PwdAuthWithSeqService", pwdAuthWithSeqService);
      //PushDataNodeListService
      PushDataNodeListService pushDataNodeListService = new PushDataNodeListService();
      pushDataNodeListService.start(null);
      tmpinnerBase.put("PushDataNodeListService", pushDataNodeListService);
      //MountCheckService
      MountCheckService mountCheckService = new MountCheckService();
      mountCheckService.start(null);
      tmpinnerBase.put("MountCheckService", mountCheckService);
      //InitService
      InitService initService = new InitService();
      initService.start(null);
      tmpinnerBase.put("InitService", initService);
      //ResourceCtrlService
      ResourceCtrlService resourceCtrlService = new ResourceCtrlService();
      resourceCtrlService.start(null);
      tmpinnerBase.put("ResourceCtrlService", resourceCtrlService);
      //FileQueryService
      FileQueryService fileQueryService = new FileQueryService();
      fileQueryService.start(null);
      tmpinnerBase.put("FileQueryService", fileQueryService);
      //FileRenameService
      FileRenameService fileRenameService = new FileRenameService();
      fileRenameService.start(null);
      tmpinnerBase.put("FileRenameService", fileRenameService);

      //FileService 无用
      DefaultFileService fileService = new DefaultFileService();
      fileService.start(null);
      tmpinnerBase.put("FileService", fileService);
    } catch (Exception e) {
      log.error("内部基础服务容器初始化失败", e);
      throw new NestedRuntimeException("内部基础服务容器初始化失败", e);
    }
    this.innerBase = tmpinnerBase;
  }



  @SuppressWarnings("unchecked")
  private void initServiceFlow() {
    Map<String, String> tmpflows = new HashMap<>();
    Map<String, String> tmpflowsBySys = new HashMap<>();

    Map<String, String> tmpPsFlows = new HashMap<>();
    Map<String, String> tmpPsFlowsBySys = new HashMap<>();

    Map<String, List<PutAuthUser>> tmpputAuthUserListMap = new HashMap<>();
    Map<String, List<GetAuthUser>> tmpgetAuthUserListMap = new HashMap<>();
    Map<String, ServiceInfo> tmpserviceInfoMap = new HashMap<>();
    try {
      Document doc = CachedCfgDoc.getInstance().loadServicesInfo();
      List l = doc.selectNodes("/services/service");
      // ***** 从数据中获取 文件处理流程
      String tranCode = null;
      for (Object aL : l) {
        try {
          /**
           *  此处新增一部分数据
           */

          Element seviceEle = (Element) aL;
          String sysName = seviceEle.attributeValue("sysname");
          tranCode = seviceEle.attributeValue("trancode");
          String flowname = seviceEle.attributeValue("flow");
          String psFlowName = seviceEle.attributeValue("psFlow"); // 此处获取数据 Ok
          List<Element> putUserList = seviceEle.selectNodes("putAuth/user");
          List<Element> getUserList = seviceEle.selectNodes("getAuth/user");

          tmpflows.put(tranCode, flowname);
          String sysTranCode = sysName + "_" + tranCode;
          tmpflowsBySys.put(sysTranCode, flowname);

          // 添加获取psflow 的属性
          tmpPsFlows.put(tranCode, psFlowName);

          tmpPsFlowsBySys.put(sysTranCode, psFlowName);


          log.info("成功载入服务#系统名称:{},业务服务:{},处理流程:{}", sysName, tranCode, flowname);
          String rename = seviceEle.attributeValue("rename", "1");
          for (Element ele : putUserList) {
            String directoy = ele.attributeValue("directoy");
            if (directoy == null || directoy.isEmpty()) directoy = "/" + tranCode + "/" + sysName;
            if (directoy.charAt(directoy.length() - 1) == '/') directoy = directoy.substring(0, directoy.length() - 1);
            addPutAuthUser(sysTranCode, new PutAuthUser(ele.getText().trim(), directoy, "1".equals(rename)), tmpputAuthUserListMap);
          }
          for (Element ele : getUserList) {
            addGetAuthUser(sysTranCode, new GetAuthUser(ele.getText().trim()), tmpgetAuthUserListMap);
          }
          ServiceInfo serviceInfo = new ServiceInfo();
          serviceInfo.setSysName(sysName);
          serviceInfo.setTrancode(tranCode);
          serviceInfo.setFlow(flowname);
          serviceInfo.setPsFlow(psFlowName);
          serviceInfo.setDiscribe(seviceEle.attributeValue("describe"));
          serviceInfo.setRename(BooleanTool.toBoolean(seviceEle.attributeValue("rename")));
          serviceInfo.setFilePeriod(StringTool.toLong(seviceEle.attributeValue("filePeriod"), 0L));
          serviceInfo.setPriority(StringTool.toInteger(seviceEle.attributeValue("priority"), 3));
          serviceInfo.setSize(StringTool.toInteger(seviceEle.attributeValue("size"), 20));
          tmpserviceInfoMap.put(sysTranCode, serviceInfo); // 此处添加了 psflow、 居然没有添加进来
          log.info("成功载入服务的put与get权限#系统名称:{},业务服务:{},处理流程:{},校验流程:{}", sysName, tranCode, flowname, psFlowName);
        } catch (Exception e) {
          log.error("加载业务服务信息" + tranCode + "失败", e);
        }
      }
    } catch (Exception e) {
      log.error("初始化业务服务信息失败", e);
      throw new NestedRuntimeException("初始化业务服务信息失败", e);
    }
    this.flows = tmpflows;
    this.flowsBySys = tmpflowsBySys;

    this.psFlows = tmpPsFlows;
    this.psFlowBySys = tmpPsFlowsBySys;

    this.putAuthUserListMap = tmpputAuthUserListMap;
    this.getAuthUserListMap = tmpgetAuthUserListMap;
    this.serviceInfoMap = tmpserviceInfoMap;
  }

  public GeneralService getService(String name) {
    GeneralService service = base.get(name);
    if (service == null) service = innerBase.get(name);
    return service;
  }

  public Map<String, String> getHandles() {
    return addHanlerModel;
  }

  public String getFlow(String tranCode) {
    return flows.get(tranCode);
  }

  // ***** 再次 添加 属性值
  public String getFlow(String sysName, String tranCode) {
    return flowsBySys.get(sysName + "_" + tranCode);
  }
  // ***** 再次 psflow 添加 属性值

  public String getPsFlow(String tranCode) {
    return psFlows.get(tranCode);
  }

  public String getPsFlow(String sysName, String tranCode) {
    //  根据key 获取 value  psFlow
    return psFlowBySys.get(sysName + "_" + tranCode);
  }


  // *****
  public ServiceInfo getServiceInfo(String sysName, String tranCode) {
    return serviceInfoMap.get(sysName + "_" + tranCode);
  }

  public GeneralService registerService(String name, GeneralService impl) {
    return base.put(name, impl);
  }

  public void registerServiceFlow(String name, String flow) {
    flows.put(name, flow);
  }

  public GeneralService dropService(String name) {
    return base.remove(name);
  }

  public void dropServiceFlow(String name) {
    flows.remove(name);
  }


  private void addPutAuthUser(String sysTranCode, PutAuthUser putAuthUser, Map<String, List<PutAuthUser>> putAuthUserListMap) {
    List<PutAuthUser> list = putAuthUserListMap.get(sysTranCode);
    if (list == null) {
      list = new ArrayList<>();
      putAuthUserListMap.put(sysTranCode, list);
    }
    list.add(putAuthUser);
  }

  private void addGetAuthUser(String sysTranCode, GetAuthUser getAuthUser, Map<String, List<GetAuthUser>> getAuthUserListMap) {
    List<GetAuthUser> list = getAuthUserListMap.get(sysTranCode);
    if (list == null) {
      list = new ArrayList<>();
      getAuthUserListMap.put(sysTranCode, list);
    }
    list.add(getAuthUser);
  }

  public List<PutAuthUser> getPutAuthUsers(String sysName, String tranCode) {
    List<PutAuthUser> list = putAuthUserListMap.get(sysName + "_" + tranCode);
    if (list == null) return Collections.emptyList();
    return list;
  }

  public List<GetAuthUser> getGetAuthUsers(String sysName, String tranCode) {
    List<GetAuthUser> list = getAuthUserListMap.get(sysName + "_" + tranCode);
    if (list == null) return Collections.emptyList();
    return list;
  }

}
