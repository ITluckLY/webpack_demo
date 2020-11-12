package com.dcfs.esb.ftp.server.cmd;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 目录权限校验
 * 用户认证处理命令
 * 需保证线程安全
 */
public class DoAuth implements IFileCmd {
    private static final Logger log = LoggerFactory.getLogger(DoAuth.class);

    /**
     * 用户认证处理命令
     *
     * @throws FtpException
     */
    public Object doCommand(FileMsgBean bean, EsbFile file) throws FtpException {
        // 检查用户名和密码，确认用户是否合法
        // 迁移到com.dcfs.esb.ftp.impls.service.PwdAuthService
        //boolean userFlag = UserInfoFactory.getInstance().doAuth(bean);//NOSONAR
        boolean fileFlag = false;
        // 用户合法，再检查用户是否有文件操作的权限
        String fileName = bean.getFileName();
        if (FileMsgTypeHelper.isGetFileAuth(bean.getFileMsgFlag())) {
            // 判断下载
            try {
                fileFlag = EsbFileManager.getInstance().isAuthToUser(fileName, bean.getUid(), EsbFileManager.OP_R);
            } catch (Exception e) {
                log.error("", e);
                throw new FtpException(FtpErrCode.FILE_NOT_FOUND_ERROR, e);
            }
        } else if (FileMsgTypeHelper.isPutFileAuth(bean.getFileMsgFlag())) {
            // 判断上传
            try {
                fileFlag = EsbFileManager.getInstance().isAuthToUser(fileName, bean.getUid(), EsbFileManager.OP_W);
            } catch (Exception e) {
                log.error("", e);
                throw new FtpException(FtpErrCode.FILE_NOT_FOUND_ERROR, e);
            }
        }
        bean.setAuthFlag(fileFlag);

        //bean.setServerName(FtpConfig.getInstance().getServName());//NOSONAR
        log.debug("授权检查的结果:{}", bean.isAuthFlag());
        return bean;
    }
}
