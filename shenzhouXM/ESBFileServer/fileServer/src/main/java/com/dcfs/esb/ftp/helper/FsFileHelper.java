package com.dcfs.esb.ftp.helper;

import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.FileUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by mocg on 2016/12/20.
 */
public class FsFileHelper {
    protected FsFileHelper() {
    }

    //文件与配置文件需要同时存在
    public static boolean existsFileAndSetFileSize(CachedContext context, FileMsgBean bean) {//NOSONAR
        //String fileRootPath = context.getCxtBean().getFileRootPath();//NOSONAR
        String fileRootPath = FtpConfig.getInstance().getFileRootPath();
        String concatFileName = FileUtil.concatFilePath(fileRootPath, bean.getFileName());
        String localFileName = FilenameUtils.separatorsToSystem(concatFileName);
        File file = new File(localFileName);
        boolean exists = file.exists() && new File(localFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT).exists();
        bean.setFileExists(exists);
        if (exists) bean.setFileSize(file.length());
        return exists;
    }
}
