package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.utils.FileUtil;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;

/**
 * 文件路径基础服务，只更改文件名，包括文件路径
 * <File name="文件名(可以根据文件名找到文件[标识(TV1)_系统名称_yyyyMM dd HH mm ssSSS_long型随机数.ext])"
 * sys="所属系统" uploadTime="文件上传时间(毫秒数)" cliReqFilePath="客户端要求的文件相对路径"
 * rename="重命名后的文件相对路径">文件绝对路径</File>
 */
public class TimeFilePathService extends AbstractBaseService {
    private static final Logger log = Logger.getLogger(TimeFilePathService.class);

    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        log.info("更改文件路径");
        String fileMsgFlag = bean.getFileMsgFlag();
        if (FileMsgTypeHelper.isPutFileAuth(fileMsgFlag)) {
            long rdmlong = RandomUtils.nextLong();
            String name = bean.getFileName();
            int indexOf = name.lastIndexOf('.');
            String ext = "efs";
            if (indexOf > -1) ext = name.substring(indexOf + 1);
            else {
                String clientFileName = bean.getClientFileName();
                int indexOf1 = clientFileName.lastIndexOf('.');
                if (indexOf1 > -1) ext = clientFileName.substring(indexOf1 + 1);
            }

            String filePath = FileUtil.concat("TV1", DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")
                    + "_" + rdmlong + "." + ext);

            bean.setFileName(filePath);
        } else if (FileMsgTypeHelper.isGetFileAuth(fileMsgFlag)) {
            //nothing
        }

    }

    private File newOnlyFile(String dirPath, String ssSSS, String ext) {
        long rdmlong = RandomUtils.nextLong();
        String filePath = FileUtil.convertToLocalPath(FileUtil.concatFilePath(dirPath, ssSSS + "_" + rdmlong + "." + ext));
        File file = new File(filePath);
        if (file.exists()) return newOnlyFile(dirPath, ssSSS, ext);
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        return file;
    }

}
