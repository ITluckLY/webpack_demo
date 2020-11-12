package com.dcfs.esb.ftp.helper;

import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * 文件版本号
 * Created by mocg on 2016/10/25.
 */
public class FileVersionHelper {
    private static final Logger log = LoggerFactory.getLogger(FileVersionHelper.class);

    protected FileVersionHelper() {
    }

    public static long getFileVersion(String filePath) {
        try {
            File cfgFile = new File(filePath + SvrGlobalCons.DCFS_CFG_FILE_EXT);
            if (!cfgFile.exists() || !cfgFile.isFile()) {
                log.warn("对应的配置文件不存在#{}", filePath);
                return -1;
            }
            Properties properties = new Properties();
            PropertiesTool.load(properties, cfgFile);
            return Long.parseLong(properties.getProperty("version", "-1"));
        } catch (Exception e) {
            log.error("获取属性文件版本号出错", e);
            return -1;
        }
    }

    public static long getFileVersion(CachedContext context) {
        Long fileVersion = context.getCxtBean().getFileVersion();
        if (fileVersion == null) return -1;
        return fileVersion;
    }
}
