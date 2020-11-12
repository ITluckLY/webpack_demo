package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.utils.ShortUrlUtil;
import org.apache.commons.io.FilenameUtils;

/**
 * 重命名基础服务，只更改文件名，不更改文件路径
 */
public class FileRenameByRandomService extends AbstractBaseService {

    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        String fileMsgFlag = bean.getFileMsgFlag();
        if (FileMsgTypeHelper.isPutFileAuth(fileMsgFlag)) {
            bean.setFileName(renameByRandom(bean.getFileName()));
        }
    }

    private String renameByRandom(String filePath) {
        int index = filePath.lastIndexOf('/');
        String pre = filePath.substring(0, index + 1);
        String aft = filePath.substring(index + 1);
        String ext = FilenameUtils.getExtension(aft);//不带原文件名
        //return pre + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + Cfg.getNodeId() + "_" + RandomUtils.nextLong() + "." + ext;//NOSONAR
        return pre + ShortUrlUtil.short36(UUIDService.nextId()) + "." + ext;
    }

}
