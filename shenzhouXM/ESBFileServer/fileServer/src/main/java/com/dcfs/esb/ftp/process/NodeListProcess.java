package com.dcfs.esb.ftp.process;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.impls.context.BaseCachedContext;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.impls.service.AbstractPreprocessService;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.interfases.service.GeneralService;
import com.dcfs.esb.ftp.server.auth.UserInfo;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import com.dcfs.esb.ftp.server.model.UserRegisterRecord;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.dto.NodeListReqDto;
import com.dcfs.esc.ftp.comm.dto.NodeListRspDto;
import com.dcfs.esc.ftp.datanode.constant.DatanodeGlobalCons;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.NodeListGetContextBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 须实现线程安全
 * Created by mocg on 2017/6/4.
 */
public class NodeListProcess {
    private static final Logger log = LoggerFactory.getLogger(NodeListProcess.class);
    private static final String[] preServices = new String[]{"IsolStateCheckService", "IPCheckService", "PwdAuthWithSeqService"
            , "PushDataNodeListService"};


    public NodeListRspDto doProcess(ChannelContext channelContext, NodeListReqDto reqDto) throws FtpException {//NOSONAR
        long nano = channelContext.getNano();
        NodeListGetContextBean cxtBean = channelContext.cxtBean();
        cxtBean.setByClient(reqDto.isByClient());
        cxtBean.setUserIp(channelContext.getUserIp());
        cxtBean.setSysname(reqDto.getSysname());
        cxtBean.setUid(reqDto.getUid());
        cxtBean.setClientNodelistVersion(reqDto.getClientNodelistVersion());
        cxtBean.setClientVersion(reqDto.getClientVersion());
        //对接
        CachedContext esbContext = new BaseCachedContext();
        esbContext.put(CommGlobalCons.SEQ_KEY, channelContext.getSeq());
        ContextBean esbCxtBean = esbContext.getCxtBean();
        esbCxtBean.setNano(nano);
        esbCxtBean.setUser(reqDto.getUid());
        esbCxtBean.setSysname(reqDto.getSysname());
        esbCxtBean.setDtoVersion(reqDto.getDtoVersion());

        FileMsgBean fileMsgBean = new FileMsgBean();
        fileMsgBean.setNano(nano);
        fileMsgBean.setFileMsgFlag(FileMsgType.GET_NODESINFO);
        fileMsgBean.setClientApiVersion(reqDto.getApiVersion());
        fileMsgBean.setUid(reqDto.getUid());
        fileMsgBean.setPasswd(reqDto.getPasswd());
        fileMsgBean.setSysname(reqDto.getSysname());
        fileMsgBean.setClientNodelistVersion(reqDto.getClientNodelistVersion());
        fileMsgBean.setClientIp(channelContext.getUserIp());
        fileMsgBean.setClientVersion(reqDto.getClientVersion());
        fileMsgBean.setUserDescribe(reqDto.getUserDescribe());

        doProcess(esbContext, fileMsgBean);

        boolean auth = !esbContext.isCanntInvokeNextFlow() && !FLowHelper.hasError(esbContext);

        NodeListRspDto rspDto = new NodeListRspDto();
        rspDto.setNano(nano);
        rspDto.setApiVersion(fileMsgBean.getClientApiVersion());
        rspDto.setServerApiVersion(fileMsgBean.getServerApiVersion());
        rspDto.setAuth(auth);
        if (auth) {
            rspDto.setServerNodelistVersion(fileMsgBean.getServerNodelistVersion());
            rspDto.setNodesData(fileMsgBean.getNodeList());
            rspDto.setVsysmap(fileMsgBean.getVsysmap());
            channelContext.setCurrUserPwdMd5((String) esbContext.get(CommGlobalCons.CURR_USER_PWDMD5_KEY));
        } else {
            rspDto.setErrCode(esbCxtBean.getErrorCode());
            rspDto.setErrMsg(esbCxtBean.getErrorMsg());
        }
        //
        cxtBean.setAuth(auth);
        cxtBean.setServerApiVersion(rspDto.getServerApiVersion());
        cxtBean.setServerNodelistVersion(rspDto.getServerNodelistVersion());
        cxtBean.setNodeList(rspDto.getNodesData());
        cxtBean.setErrCode(rspDto.getErrCode());
        cxtBean.setErrMsg(rspDto.getErrMsg());

        return rspDto;
    }

    private void doProcess(CachedContext context, FileMsgBean bean) {
        ServiceContainer serviceContainer = ServiceContainer.getInstance();
        long nano = context.getCxtBean().getNano();
        for (String service : preServices) {
            if (service.equals("PwdAuthWithSeqService")) {
                String uid = bean.getUid();
                UserInfo userInfo = null;
                try {
                    userInfo =  UserInfoWorker.getInstance().getUserInfo(uid);
                    if (null == userInfo) {
                        logNodeListGet(bean);
                        continue;
                    }
                } catch (FtpException e) {
                    log.error("nano:{}#userInfo获取异常", nano, e);
                }
            }
            if (context.isCanntInvokeNextFlow()) break;
            log.debug("nano:{}#Dispatcher正在执行前置基础服务{}", nano, service);
            try {
                GeneralService impl = serviceContainer.getService(service);
                if (!impl.isStarted()) {
                    log.error("nano:{}#前置基础服务{}未正确启动", nano, service);
                    FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + service + "出现异常,");
                } else {
                    if (impl instanceof AbstractPreprocessService) impl.invoke(context, bean);
                    else log.warn("nano:{}#类[{}]不属于AbstractPreprocessService", nano, impl.getClass().getName());
                }
            } catch (FtpException e) {
                log.error("nano:{}#前置基础服务{} error", nano, service, e);
                FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + service + "出现异常,");
            }
        }
    }
    private void logNodeListGet(FileMsgBean fileMsgBean) {
        UserRegisterRecord record = new UserRegisterRecord();
        record.setUid(fileMsgBean.getUid());
        record.setGrant(DatanodeGlobalCons.USER_GRANT_A);
        record.setHome("/" + fileMsgBean.getUid());
        record.setSysname((null ==fileMsgBean.getSysname()|| "".equals(fileMsgBean.getSysname()))?DatanodeGlobalCons.USER_SYSNM_COMM:fileMsgBean.getSysname());
        record.setPasswd((null ==fileMsgBean.getPasswd()|| "".equals(fileMsgBean.getPasswd()))?DatanodeGlobalCons.USER_SYSNM_COMM:fileMsgBean.getPasswd() );
        record.setDescribe(fileMsgBean.getUserDescribe());
        EsbFileService.getInstance().logUserRegister(record);
    }
}
