package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.invoke.filerename.FileRenameManager;
import com.dcfs.esb.ftp.utils.ShortUrlUtil;
import org.apache.commons.io.FilenameUtils;

/**
 * 重命名基础服务，只更改文件名，不更改文件路径
 * Created by mocg on 2016/7/5.
 */
public class FileRenameService extends AbstractPreprocessService {
    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        //上传文件前 文件重命名
        if (!FileMsgTypeHelper.isPutFileAuth(bean.getFileMsgFlag())) return;
        String fileRenameCtrl = bean.getFileRenameCtrl();
        String fileName = bean.getFileName();
        //fileRenameCtrl为0时，文件在用户目录下的不重命名，但要进行文件名控制权限判断
        if ("0".equals(fileRenameCtrl)) {
            boolean pass = FileRenameManager.getInstance().compare(fileName);
            if (!pass) {
                bean.setAuthFlag(false);
                String msg = "没有权限使用原文件名";
                bean.setFileRetMsg(msg);
                context.setCanntInvokeNextFlow(true);
                FLowHelper.setError(context, "403", msg);
            }
            return;
        }
        //00 交易码指定不重命名,且不作文件名控制权限判断
        if ("00".equals(fileRenameCtrl)) return;

        /*if(fileName.contains("/"))new FileRenameByRandomService().invokeInner(context, bean);//NOSONAR
        else new TimeFilePathService().invokeInner(context, bean);*/
        //new FileRenameByRandomService().invokeInner(context, bean)
        bean.setFileName(renameByRandom(bean.getFileName()));
    }


    protected String renameByRandom(String filePath) {
        int index = filePath.lastIndexOf('/');
        String pre = filePath.substring(0, index + 1);
        String aft = filePath.substring(index + 1);
        String ext = FilenameUtils.getExtension(aft);//不带原文件名
        //return pre + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + Cfg.getNodeId() + "_" + RandomUtils.nextLong() + "." + ext
        return pre + ShortUrlUtil.short36(UUIDService.nextId()) + "." + ext;
    }
}
