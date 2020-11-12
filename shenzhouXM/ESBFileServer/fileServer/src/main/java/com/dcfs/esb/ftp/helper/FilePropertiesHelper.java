package com.dcfs.esb.ftp.helper;

import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mocg on 2017/6/27.
 */
public class FilePropertiesHelper {

    private static final Logger log = LoggerFactory.getLogger(FilePropertiesHelper.class);
    private Properties properties = new Properties();

    public void load(String filePath) {
        File cfgFile = new File(filePath + SvrGlobalCons.DCFS_CFG_FILE_EXT);
        if (!cfgFile.exists() || !cfgFile.isFile()) {
            log.warn("对应的配置文件不存在#{}", cfgFile.getPath());
            return;
        }
        try {
            PropertiesTool.load(properties, cfgFile);
        } catch (IOException e) {
            log.error("load file properties err#{}", cfgFile.getPath(), e);
        }
    }

    public long getFileVersion() {
        try {
            return Long.parseLong(properties.getProperty("version", "-1"));
        } catch (Exception e) {
            log.error("获取属性文件版本号出错", e);
            return -1;
        }
    }

    public boolean isPack() {
        try {
            return BooleanTool.toBoolean(properties.getProperty("pack"));
        } catch (Exception e) {
            log.error("获取属性pack出错", e);
            return false;
        }
    }

    public String getTags() {
        return properties.getProperty("tags");
    }
}
