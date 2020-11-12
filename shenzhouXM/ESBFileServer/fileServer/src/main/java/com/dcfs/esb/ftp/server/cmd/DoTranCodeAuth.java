package com.dcfs.esb.ftp.server.cmd;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.servcomm.model.ServiceInfo;
import com.dcfs.esb.ftp.server.service.GetAuthUser;
import com.dcfs.esb.ftp.server.service.PutAuthUser;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 需保证线程安全
 * Created by mocg on 2016/10/13.
 */
public class DoTranCodeAuth {
    private static final Logger log = LoggerFactory.getLogger(DoTranCodeAuth.class);

    public void doCommand(CachedContext context, FileMsgBean bean) throws FtpException {//NOSONAR
        String fileName = bean.getFileName();
        String tranCode = bean.getTranCode();
        ContextBean cxtBean = context.getCxtBean();
        String sysName = cxtBean.getSysname();
        String user = cxtBean.getUser();
        ServiceContainer serviceContainer = ServiceContainer.getInstance();
        List<PutAuthUser> putAuthUserList = serviceContainer.getPutAuthUsers(sysName, tranCode);

        long nano = cxtBean.getNano();
        String flowNo = cxtBean.getFlowNo();
        boolean isAuth = false;
        String fileMsgFlag = bean.getFileMsgFlag();
        if (FileMsgTypeHelper.isGetFileAuth(fileMsgFlag)) {
            //用户在下载权限用户列表中，并且下载路径在上传路径列表包含内
            List<GetAuthUser> getAuthUserList = serviceContainer.getGetAuthUsers(sysName, tranCode);
            isAuth = getAuthUserList.contains(new GetAuthUser(user));
            if (isAuth) {
                boolean isStartsWith = false;
                for (PutAuthUser authUser : putAuthUserList) {//NOSONAR
                    String directoy = authUser.getDirectoy();
                    if (directoy.isEmpty() || fileName == null) continue;
                    if (directoy.charAt(directoy.length() - 1) != '/') directoy += "/";
                    if (fileName.startsWith(directoy)) {
                        isStartsWith = true;
                        break;
                    }
                }
                isAuth = isStartsWith;
            }
        } else if (FileMsgTypeHelper.isPutFileAuth(fileMsgFlag)) {
            //用户在上传权限用户列表中
            PutAuthUser putAuthUser = null;
            for (PutAuthUser authUser : putAuthUserList) {
                if (authUser.getUname().equals(user)) {
                    putAuthUser = authUser;
                    break;
                }
            }
            if (putAuthUser != null) {
                isAuth = true;
                boolean rename = putAuthUser.isRename();
                cxtBean.setFileRename(rename);
                bean.setFileRenameCtrl(rename ? null : "00");
                //文件为空
                if (fileName == null || fileName.length() == 0) {
                    String clientFileName = bean.getClientFileName().replaceAll("\\\\+", "/");
                    fileName = clientFileName.substring(clientFileName.lastIndexOf('/') + 1);
                }
                String separator = fileName.charAt(0) == '/' ? "" : "/";
                //最终文件名为两者相加
                bean.setFileName(putAuthUser.getDirectoy() + separator + fileName);
            } else isAuth = false;
        } else isAuth = true;

        bean.setAuthFlag(isAuth);
        log.debug("nano:{}#flowNo:{}#交易码权限校验是否通过?{}", nano, flowNo, isAuth);
        if (!isAuth) {
            FLowHelper.setError(context, FtpErrCode.AUTH_TRAN_CODE_FAILED);
            return;
        }

        //String flowName = ServiceContainer.getInstance().getFlow(sysName, tranCode);//NOSONAR
        String flowName = null;
        ServiceInfo serviceInfo = serviceContainer.getServiceInfo(sysName, tranCode);
        if (serviceInfo != null) {
            flowName = serviceInfo.getFlow();
            cxtBean.setTaskPriority(serviceInfo.getPriority());
            cxtBean.setTaskSize(serviceInfo.getSize());

        }
        cxtBean.setFlowName(flowName);
        if (flowName == null) {
            bean.setAuthFlag(false);
            log.error("nano:{}#flowNo:{}#交易码认证失败:{}", nano, flowNo, tranCode);
            FLowHelper.setError(context, FtpErrCode.UNKNOWN_TRAN_CODE);
        }
    }
}
