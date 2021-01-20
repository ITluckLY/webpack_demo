package com.dcfs.esc.ftp.datanode.process;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.impls.context.BaseCachedContext;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.impls.flow.ServiceFlowBean;
import com.dcfs.esb.ftp.impls.flow.ServiceFlowManager;
import com.dcfs.esb.ftp.impls.service.AbstractPreprocessService;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.service.GeneralService;
import com.dcfs.esb.ftp.server.file.EsbFileWorker;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.NullDefTool;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.dto.FileDownloadAuthReqDto;
import com.dcfs.esc.ftp.comm.dto.FileDownloadAuthRspDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadAuthReqDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadAuthRspDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.DownloadContextBean;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.ProcessHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by mocg on 2017/6/4.
 */
public class AuthProcess {
  private static final Logger log = LoggerFactory.getLogger(AuthProcess.class);
  private String[] preServices = new String[]{"IsolStateCheckService", "ApiVersionCheckService", "IPCheckService", "PwdAuthWithSeqService"
      , "PushDataNodeListService", "InitService", "FileRenameService", "ResourceCtrlService", "FileQueryService"};
  private String[] oldpsFlowService = new String[]{};


  // *****
  public FileUploadAuthRspDto doProcess(ChannelContext channelContext, UploadContextBean cxtBean, FileUploadAuthReqDto dto) throws FtpException {//NOSONAR
    long nano = channelContext.getNano();  //
    String flowNo = channelContext.getFlowNo();


    //对接
    CachedContext esbContext = new BaseCachedContext();
    esbContext.put(CommGlobalCons.SEQ_KEY, channelContext.getSeq());
    com.dcfs.esb.ftp.interfases.context.ContextBean esbCxtBean = esbContext.getCxtBean();
    esbCxtBean.setNano(nano);
    esbCxtBean.setUser(dto.getUid());
    esbCxtBean.setSysname(dto.getSysname());
    esbCxtBean.setTranCode(dto.getTranCode());
    esbCxtBean.setDtoVersion(dto.getDtoVersion());
    esbCxtBean.setFlowNo(flowNo);

    FileMsgBean fileMsgBean = new FileMsgBean();
    fileMsgBean.setNano(nano);
    fileMsgBean.setFileMsgFlag(FileMsgType.PUT_AUTH);
    fileMsgBean.setFileRenameCtrl(dto.isFileRename() ? null : "0");
    fileMsgBean.setClientApiVersion(dto.getApiVersion());
    fileMsgBean.setUid(dto.getUid());
    fileMsgBean.setPasswd(dto.getPasswd());
    fileMsgBean.setSysname(dto.getSysname());
    fileMsgBean.setTranCode(dto.getTranCode());
    fileMsgBean.setFileName(dto.getFileName());
    fileMsgBean.setFileSize(dto.getFileSize());
    fileMsgBean.setTarSysName(dto.getTargetSysname());
    fileMsgBean.setTarFileName(dto.getTargetFileName());
    fileMsgBean.setClientFileName(dto.getClientFileName());
    fileMsgBean.setLastRemoteFileName(dto.getLastRemoteFileName());
    fileMsgBean.setVsysmap(dto.getVsysmap());
    fileMsgBean.setClientNodelistVersion(dto.getClientNodelistVersion());
    fileMsgBean.setClientIp(channelContext.getUserIp());
    fileMsgBean.setFlowNo(channelContext.getFlowNo());
    //对接
    cxtBean.setEsbContext(esbContext);
    cxtBean.setFileMsgBean(fileMsgBean);

    doProcess(esbContext, fileMsgBean);  //此处目前走的是 基础流程
    boolean auth = !esbContext.isCanntInvokeNextFlow() && !FLowHelper.hasError(esbContext);
    cxtBean.setAuth(auth);
    cxtBean.setErrCode(esbCxtBean.getErrorCode());
    cxtBean.setErrMsg(esbCxtBean.getErrorMsg());

    String authSeq = String.valueOf(new Random().nextLong());
    FileUploadAuthRspDto rspDto = new FileUploadAuthRspDto(auth, authSeq, cxtBean.getErrCode(), cxtBean.getErrMsg());
    rspDto.setApiVersion(fileMsgBean.getClientApiVersion());
    rspDto.setServerApiVersion(fileMsgBean.getServerApiVersion());
    rspDto.setNodeList(fileMsgBean.getNodeList());
    rspDto.setServerNodelistVersion(fileMsgBean.getServerNodelistVersion());
    if (auth) {
      cxtBean.setSvrFilePath(fileMsgBean.getFileName());
      cxtBean.setLastTmpFilePath((String) esbContext.get(ContextConstants.STR_UPLOAD_TMP_FILE_KEY));
      long remoteFileSize = NullDefTool.defNull(fileMsgBean.getRemoteFileSize(), -1L);
      rspDto.setRemoteFileSize(remoteFileSize);
      cxtBean.setPosition(remoteFileSize > 0 ? remoteFileSize : 0);

      //todo 节点重定向
      /* 文件目标节点 ip:port  返回-1:表示没有可用节点，null、0:表示按遍历方式 1:表示继续使用本连接*/
      String targetNodeAddr = fileMsgBean.getTargetNodeAddr();
      rspDto.setRedirect(targetNodeAddr != null && targetNodeAddr.contains(":"));
      rspDto.setTargetNodeAddr(targetNodeAddr);
      log.debug("nano:{}#flowNo:{}#节点重定向:{},targetNodeAddr:{}", nano, flowNo, rspDto.isRedirect(), targetNodeAddr);

      channelContext.setCurrUserPwdMd5((String) esbContext.get(CommGlobalCons.CURR_USER_PWDMD5_KEY));


      // doSetProcess(  cxtBean, fileMsgBean);


    } else {
      rspDto.setErrCode(esbCxtBean.getErrorCode());
      rspDto.setErrMsg(esbCxtBean.getErrorMsg());
      //授权失败，但允许遍历下一个节点
      if (StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.TOKEN_RESOURCE_NOT_ENOUGH)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.TASK_RESOURCE_NOT_ENOUGH)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.RESOURCE_NOT_ENOUGH)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.SYSNAME_NOT_SAME)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.FILE_SIZE_OUT_OF_CAPACITY)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.FILE_SIZE_OUT_OF_LIMIT)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.NODE_ISOLSTATE)) {
        rspDto.setTryNextNode(true);
      }
    }

    return rspDto;
  }


  /**
   * // *****
   *
   * @param channelContext
   * @param cxtBean
   * @param dto
   * @return
   * @throws FtpException
   */
  public FileDownloadAuthRspDto doProcess(ChannelContext channelContext, DownloadContextBean cxtBean, FileDownloadAuthReqDto dto) throws FtpException { //NOSONAR
    long nano = channelContext.getNano();
    String flowNo = channelContext.getFlowNo();
    CachedContext esbContext = new BaseCachedContext();
    esbContext.put(CommGlobalCons.SEQ_KEY, channelContext.getSeq());
    com.dcfs.esb.ftp.interfases.context.ContextBean esbCxtBean = esbContext.getCxtBean();
    esbCxtBean.setNano(nano);
    esbCxtBean.setUser(dto.getUid());
    esbCxtBean.setSysname(dto.getSysname());
    esbCxtBean.setTranCode(dto.getTranCode());
    esbCxtBean.setDtoVersion(dto.getDtoVersion());
    esbCxtBean.setFlowNo(flowNo);

    FileMsgBean fileMsgBean = new FileMsgBean();
    fileMsgBean.setNano(nano);
    fileMsgBean.setFileMsgFlag(FileMsgType.GET_AUTH);
    fileMsgBean.setClientApiVersion(dto.getApiVersion());
    fileMsgBean.setUid(dto.getUid());
    fileMsgBean.setPasswd(dto.getPasswd());
    fileMsgBean.setFileName(dto.getFileName());
    fileMsgBean.setTranCode(dto.getTranCode());
    fileMsgBean.setTarSysName(dto.getTargetSysname());
    fileMsgBean.setTarFileName(dto.getTargetFileName());
    fileMsgBean.setClientFileName(dto.getClientFileName());
    fileMsgBean.setVsysmap(dto.getVsysmap());
    fileMsgBean.setClientNodelistVersion(dto.getClientNodelistVersion());
    fileMsgBean.setSysname(dto.getSysname());
    fileMsgBean.setFlowNo(flowNo);
    //对接
    cxtBean.setEsbContext(esbContext);
    cxtBean.setFileMsgBean(fileMsgBean);

    doProcess(esbContext, fileMsgBean);


    boolean auth = !esbContext.isCanntInvokeNextFlow() && !FLowHelper.hasError(esbContext);
    cxtBean.setAuth(auth);
    cxtBean.setErrCode(esbCxtBean.getErrorCode());
    cxtBean.setErrMsg(esbCxtBean.getErrorMsg());


    FileDownloadAuthRspDto rspDto = new FileDownloadAuthRspDto(auth, null, 0);
    rspDto.setApiVersion(fileMsgBean.getClientApiVersion());
    rspDto.setServerApiVersion(fileMsgBean.getServerApiVersion());
    rspDto.setNodeList(fileMsgBean.getNodeList());
    rspDto.setServerNodelistVersion(fileMsgBean.getServerNodelistVersion());
    boolean fileExists = BooleanTool.toBoolean(fileMsgBean.isFileExists());
    if (auth) {
      //ref context.put(ContextConstants.FilePowerPath, bean.getFileName());//NOSONAR
      String fileName = (String) esbContext.get(ContextConstants.FILE_POWER_PATH);
      String fileAbsolutePath = EsbFileWorker.getInstance().getFileAbsolutePath(fileName);
      cxtBean.setAbsFilePath(fileAbsolutePath);

      rspDto.setFileSize(fileMsgBean.getFileSize());
      rspDto.setFileExists(fileExists);

      //节点重定向
      /* 文件目标节点 ip:port  返回-1:表示没有可用节点，null、0:表示按遍历方式 1:表示继续使用本连接*/
      String targetNodeAddr = fileMsgBean.getTargetNodeAddr();
      rspDto.setRedirect(targetNodeAddr != null && targetNodeAddr.contains(":"));
      rspDto.setTargetNodeAddr(targetNodeAddr);
      //todo
      rspDto.setFileExistsInSys(true);
      log.debug("nano:{}#flowNo:{}#节点重定向:{},targetNodeAddr:{},fileExistsInSys:{}", nano, flowNo, rspDto.isRedirect(), targetNodeAddr, rspDto.isFileExistsInSys());

      cxtBean.setFileExists(fileExists);
      channelContext.setCurrUserPwdMd5((String) esbContext.get(CommGlobalCons.CURR_USER_PWDMD5_KEY));
    } else {
      rspDto.setErrCode(esbCxtBean.getErrorCode());
      rspDto.setErrMsg(esbCxtBean.getErrorMsg());
      //授权失败，但允许遍历下一个节点
      if (StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.TOKEN_RESOURCE_NOT_ENOUGH)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.TASK_RESOURCE_NOT_ENOUGH)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.RESOURCE_NOT_ENOUGH)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.SYSNAME_NOT_SAME)
          || StringUtils.equals(esbCxtBean.getErrorCode(), FtpErrCode.NODE_ISOLSTATE)) {
        rspDto.setTryNextNode(true);
      }
    }
    return rspDto;
  }

  // *****  此处应该吧 psFlow  放进这个上下文中   ??????????????///
  private void doProcess(CachedContext context, FileMsgBean bean) {//NOSONAR

    //有交易码则走services_info.xml中定义的流程
    String tranCode = bean.getTranCode();
    String sysname = bean.getSysname();
    // ps.start();
    if (StringUtils.isNotBlank(tranCode)) {
      String flowName = ServiceContainer.getInstance().getFlow(sysname, tranCode);

      // 获取   <service sysname="comm" trancode="fts0000001" flow="DefStandardFlow" psflow="FilesProcesFlow"  对应的 flow =name 值，
      if (StringUtils.isNotBlank(flowName)) {
        ServiceFlowBean serviceFlow = ServiceFlowManager.getInstance().getFlow(flowName);
        String[] baseService = serviceFlow.getBaseService(); // 获取组件的名称 可以正常數據 流程組件名称
        if (baseService.length != 0) preServices = baseService; // 重新给当前的数组里赋值

      }
    }
    ServiceContainer serviceContainer = ServiceContainer.getInstance(); // 获取数据对象  此处可以获取到 校验流程编号

    long nano = context.getCxtBean().getNano(); // 获取 号  生产一个号
    String flowNo = context.getCxtBean().getFlowNo(); //  此处可以获取到流水号
    for (String service : preServices) {
      if (context.isCanntInvokeNextFlow()) break;
      log.debug("nano:{}#flowNo:{}#Dispatcher正在执行前置基础服务{}", nano, flowNo, service);
      try {
        //如果此处传递过来的是handler 会报错
        if (service.endsWith("Handler")) {
          context.put("Handler", service); // 此处是handler
        } else {

          GeneralService    impl = serviceContainer.getService(service); // 如果不为空 返回service ，true

        if (!impl.isStarted()) {
          log.error("nano:{}#flowNo:{}#前置基础服务{}未正确启动", nano, flowNo, service);
          FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + service + "出现异常,");
        } else {
          if (impl instanceof AbstractPreprocessService) impl.invoke(context, bean);// 上下文信息和文件信息
          else
            log.warn("nano:{}#flowNo:{}#类[{}]不属于AbstractPreprocessService", nano, flowNo, impl.getClass().getName());
        }
        }
      } catch (FtpException e) {
        log.error("nano:{}#flowNo:{}#前置基础服务{} error", nano, flowNo, service, e);
        FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + service + "出现异常,");
      }
    }


  }

/*  private void doSetProcess(UploadContextBean cxtBean, FileMsgBean bean) {

    //有交易码则走services_info.xml中定义的流程
    String tranCode = bean.getTranCode();
    String sysname = bean.getSysname();

    // 获取 psFlow 组件名称
    String getpsFlowName = ServiceContainer.getInstance().getPsFlow(sysname, tranCode);
    ServiceContainer serviceContainer = ServiceContainer.getInstance();  // 获取数据对象  此处可以获取到 校验流程编号
    // 此处测试
    if (StringUtils.isNotBlank(getpsFlowName)) {
      ServiceFlowBean serviceFlow = ServiceFlowManager.getInstance().getFlow(getpsFlowName);
      String[] psFlowService = serviceFlow.getBaseService(); // 获取组件的名称 可以正常數據 流程組件名称
      if (psFlowService.length != 0) {
        oldpsFlowService = psFlowService; // 重新给当前的数组里赋值
      }
    }

    ProcessExecutor processExecutor = new ProcessExecutor();


    for (String phandler : oldpsFlowService) {


      log.debug("nano:{}#flowNo:{}#Dispatcher正在执行前置基础服务{}", nano, flowNo, phandler);
      try {

      GeneralService impl = serviceContainer.getService(phandler); // 如果不为空 返回service ，true

       // ProcessHandler ph = serviceContainer.getService(impl);

     // processExecutor.setHandlers(ph);
      cxtBean.setProcessExecutor(processExecutor);


        if (!impl.isStarted()) {
          log.error("nano:{}#flowNo:{}#前置基础服务{}未正确启动", nano, flowNo, phandler);
          //FLowHelper.setError(cxtBean, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + phandler + "出现异常,");
        } else {
          if (impl instanceof AbstractPreprocessService) {
          //  impl.invoke(cxtBean, bean);// 上下文信息和文件信息
          } else {
            log.warn("nano:{}#flowNo:{}#类[{}]不属于AbstractPreprocessService", nano, flowNo, impl.getClass().getName());
          }
        }
      } catch (Exception e) {
        log.error("nano:{}#flowNo:{}#前置基础服务{} error", nano, flowNo, phandler, e);
       // FLowHelper.setError(cxtBean, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + phandler + "出现异常,");
      }
    }


  }*/
}
