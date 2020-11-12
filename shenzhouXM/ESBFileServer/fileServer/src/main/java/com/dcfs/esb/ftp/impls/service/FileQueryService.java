package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.helper.FsFileHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.spring.factory.FileTargetNodeFactory;
import com.dcfs.esb.ftp.spring.outservice.IFileTargetNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 选择最合适的节点返回给用户
 * Created by mocg on 2016/7/18.
 */
public class FileQueryService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(FileQueryService.class);

    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        if (FileMsgTypeHelper.isGetFileAuth(bean.getFileMsgFlag())) {
            long nano = context.getCxtBean().getNano();
            String flowNo = context.getCxtBean().getFlowNo();
            boolean fileExists = FsFileHelper.existsFileAndSetFileSize(context, bean);
            bean.setFileExists(fileExists);
            log.info("nano:{}#flowNo:{}#文件是否存在#{}#{}", nano, flowNo, bean.getFileName(), fileExists);
            if (fileExists) {
                bean.setTargetNodeAddr("1");//1:表示继续使用本连接
            } else {
                if (bean.getTargetNodeAddr() == null) {
                    String requestFilePath = bean.getFileName();
                    log.debug("nano:{}#flowNo:{}#requestFilePath:{}", nano, flowNo, requestFilePath);
                    IFileTargetNodeService fileTargetNodeService = FileTargetNodeFactory.getInstance().getFileTargetNodeService();
                    String targetNodeAddr = null;
                    try {
                        targetNodeAddr = fileTargetNodeService.findTargetNodeAddrByFilePath(requestFilePath);
                    } catch (Throwable e) {//NOSONAR
                        //may be raise java.lang.ExceptionInInitializerError when zk Unable to connect
                        log.warn("nano:{}#flowNo:{}#", nano, flowNo, e);
                    }
                    log.debug("nano:{}#flowNo:{}#targetNodeAddr:{}", nano, flowNo, targetNodeAddr);
                    bean.setTargetNodeAddr(targetNodeAddr);
                }
            }
        }
    }
}
