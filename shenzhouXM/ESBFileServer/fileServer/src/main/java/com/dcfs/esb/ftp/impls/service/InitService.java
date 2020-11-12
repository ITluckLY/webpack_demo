package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgBeanHelper;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.server.cmd.DoAuth;
import com.dcfs.esb.ftp.server.cmd.DoTranCodeAuth;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.UserInfoFactory;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esb.ftp.server.invoke.filerename.FileRenameManager;
import com.dcfs.esb.ftp.server.invoke.node.NodeWorker;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import com.dcfs.esb.ftp.utils.*;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 权限校验
 */
public class InitService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(InitService.class);
    private DoAuth auth = new DoAuth();
    private DoTranCodeAuth tranCodeAuth = new DoTranCodeAuth();

    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        ContextBean cxtBean = context.getCxtBean();
        long nano = cxtBean.getNano();
        String flowNo = cxtBean.getFlowNo();
        if (log.isDebugEnabled()) {
            log.debug("nano:{}#flowNo:{}#初始的FileMsgBean#{}", nano, flowNo, FileMsgBeanHelper.convertHeadToXml(bean));
        }

        //检查是符合条件
        if (!check(context, bean, cxtBean)) return;

        // 文件上传和下载的认证
        EsbFile esbFile = cxtBean.getEsbFile();
        if (esbFile != null) {
            esbFile.close();
            cxtBean.setEsbFile(null);
        }
        log.debug("nano:{}#flowNo:{}#用户登录和密钥同步：{}@{}", nano, flowNo, bean.getUid(), cxtBean.getClientAddr());
        //防止文件重命名漏洞
        String fileRenameCtrl = bean.getFileRenameCtrl();
        if (!"1".equals(fileRenameCtrl) && !"0".equals(fileRenameCtrl)) {
            bean.setFileRenameCtrl(null);
        }

        String tranCode = bean.getTranCode();
        if (StringUtils.isNotEmpty(tranCode)) {//根据交易码找到流程
            tranCodeAuth.doCommand(context, bean);
            context.put(ContextConstants.FILE_POWER_PATH, bean.getFileName());
        } else {//没有交易码则走默认流程
            dealWithoutTranCode(context, bean, cxtBean, fileRenameCtrl);
        }

        if (FLowHelper.hasError(context) || context.isCanntInvokeNextFlow()) return;

        setRemoteFileSize(context, bean);
    }

    /**
     * 检查是符合条件
     *
     * @param context
     * @param bean
     * @param cxtBean
     * @return
     */
    private boolean check(CachedContext context, FileMsgBean bean, ContextBean cxtBean) {
        long nano = cxtBean.getNano();
        String flowNo = cxtBean.getFlowNo();

        //校验路径是否包含 ../或..\ 等跳转符号
        if (FileNameUtils.containsJumpSymbol(bean.getFileName())) {
            log.warn("nano:{}#flowNo:{}#路径包含跳转符号#fileName:{}", nano, flowNo, bean.getFileName());
            bean.setAuthFlag(false);
            context.setCanntInvokeNextFlow(true);
            FLowHelper.setError(context, FtpErrCode.PATH_CONTAINS_JUMP_SYMBOL);
            return false;
        }

        //上传
        if (FileMsgTypeHelper.isPut(bean.getFileMsgFlag())) {
            //校验文件大小是否合法
            long fileSize = bean.getFileSize();
            long maxFileSize = FtpConfig.getInstance().getMaxFileSize();
            if (maxFileSize < fileSize) {
                log.warn("nano:{}#flowNo:{}#上传文件大小超过服务端限制#client:{},server:{}", nano, flowNo, fileSize, maxFileSize);
                bean.setAuthFlag(false);
                context.setCanntInvokeNextFlow(true);
                FLowHelper.setError(context, FtpErrCode.FILE_SIZE_OUT_OF_LIMIT);
                return false;
            }
            //是否有足够剩余容量
            String fileRootPath = FtpConfig.getInstance().getFileRootPath();
            long freeSpace = new File(fileRootPath).getFreeSpace();
            if (freeSpace < fileSize) {
                log.warn("nano:{}#flowNo:{}#上传文件大小超过服务端剩余容量#client:{},server:{}", nano, flowNo, fileSize, freeSpace);
                bean.setAuthFlag(false);
                context.setCanntInvokeNextFlow(true);
                FLowHelper.setError(context, FtpErrCode.FILE_SIZE_OUT_OF_CAPACITY);
                return false;
            }
        }

        //校验系统名称是否相同
        boolean isSameSysname = ObjectsTool.equals(bean.getSysname(), cxtBean.getSysname());
        if (!isSameSysname) {
            log.warn("nano:{}#flowNo:{}#系统名称不一致#client:{},server:{}", nano, flowNo, bean.getSysname(), cxtBean.getSysname());
            bean.setAuthFlag(false);
            context.setCanntInvokeNextFlow(true);
            FLowHelper.setError(context, FtpErrCode.SYSNAME_NOT_SAME);
            return false;
        }
        return true;
    }

    /**
     * 没有交易码则走默认流程
     *
     * @param context
     * @param bean
     * @param cxtBean
     * @param fileRenameCtrl
     * @return
     * @throws FtpException
     */
    private void dealWithoutTranCode(CachedContext context, FileMsgBean bean, ContextBean cxtBean, String fileRenameCtrl) throws FtpException {//NOSONAR
        cxtBean.setFlowName(null);
        String fn = bean.getFileName();
        String uid = bean.getUid();
        long nano = cxtBean.getNano();
        String flowNo = cxtBean.getFlowNo();
        if (!cxtBean.isMount()) {//不是请求挂载文件
            //文件为空
            if (StringUtils.isEmpty(fn)) {
                String clientFileName = bean.getClientFileName().replaceAll("\\\\+", "/");
                fn = clientFileName.substring(clientFileName.lastIndexOf('/') + 1);
                bean.setFileName(fn);
            }
            if (bean.getTarFileName() != null && bean.getTarSysName() != null) {
                //远程上传、下载必须添加用户目录
                log.info("nano:{}#flowNo:{}#需要从远程系统上传/下载文件,文件在用户主目录落地", nano, flowNo);
                String home = UserInfoFactory.getInstance().getUserInfo(uid).getHome();
                bean.setFileName(FileUtil.concatFilePath(home, fn));
            } else {
                if (fn.length() == 0) {
                    String home = "/" + NodeWorker.getInstance().getSysName() + UserInfoWorker.getInstance().getUserInfo(uid).getHome();
                    bean.setFileName(FileUtil.concatFilePath(home, fn));
                } else if (!fn.startsWith("/")) {//修正远程上传、下载时，文件目录结构层次多了一层BUG
                    String home = "/" + NodeWorker.getInstance().getSysName() + UserInfoWorker.getInstance().getUserInfo(uid).getHome();
                    String newFileName = FileUtil.concatFilePath(home, fn);
                    bean.setFileName(newFileName);
                    log.info("nano:{}#flowNo:{}#用户未设置上传目录,使用用户主目录:{}", nano, flowNo, newFileName);
                }
            }
        }

        context.put(ContextConstants.FILE_POWER_PATH, bean.getFileName());
        auth.doCommand(bean, null);
        if (!BooleanTool.toBoolean(bean.isAuthFlag())) {
            FLowHelper.setError(context, FtpErrCode.AUTH_DIR_FAILED);
            return;
        }
        //fileRenameCtrl为0时，文件在用户目录下的不重命名，但要进行文件名控制权限判断
        boolean notRename = "0".equals(fileRenameCtrl);
        cxtBean.setFileRename(!notRename);
        if (BooleanTool.toBoolean(bean.isAuthFlag()) && notRename) {
            boolean pass = FileRenameManager.getInstance().compare(bean.getFileName());
            if (!pass) {
                bean.setAuthFlag(false);
                String msg = "没有权限使用原文件名";
                bean.setFileRetMsg(msg);
                context.setCanntInvokeNextFlow(true);
                FLowHelper.setError(context, FtpErrCode.AUTH_ORIGINL_FILE_NAME_FAILED, msg);
            }
        }
    }

    /**
     * 根据上次传输的服务器上的文件名返回文件大小
     *
     * @param context
     * @param bean
     */
    private void setRemoteFileSize(CachedContext context, FileMsgBean bean) {
        if (BooleanTool.toBoolean(bean.isAuthFlag())) {
            //根据上次传输的服务器上的文件名返回文件大小
            String lastRemoteFileName = bean.getLastRemoteFileName();
            if (StringUtils.isNotEmpty(lastRemoteFileName)) {
                long remoteFileSize = -1L;
                String cfgFileName = EsbFileManager.getInstance().getFileAbsolutePath(lastRemoteFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT);
                Properties properties = new Properties();
                try {
                    PropertiesTool.safeLoad(properties, cfgFileName);
                    String tmpFile = properties.getProperty("tmpFile");
                    if (tmpFile != null) {
                        File lastFile = new File(tmpFile);
                        if (lastFile.exists()) {
                            remoteFileSize = lastFile.length();
                            context.put(ContextConstants.STR_UPLOAD_TMP_FILE_KEY, tmpFile);
                        }
                    }
                } catch (IOException e) {
                    log.error("nano:{}#flowNo:{}#", bean.getNano(), bean.getFlowNo(), e);
                }
                bean.setRemoteFileSize(remoteFileSize);
            }
        }
    }
}
