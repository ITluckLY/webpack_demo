package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.ThreadSleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by huangzbb on 2016/12/14.
 */
public class BakCleanService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(BakCleanService.class);

    @Override
    public void run() {
        log.debug("开始清理过期备份配置文件...");
        final long keepTimeMillis = FtpConfig.getInstance().getCfgBakKeepTime();
        String configPath = Cfg.getConfigPath();
        File cfgPath = new File(configPath);
        while (true) {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                File[] files = cfgPath.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".bak") &&
                                currentTimeMillis - file.lastModified() > keepTimeMillis) {
                            boolean delete = file.delete();
                            log.info("过期备份配置文件[{}]清理#{}", file, delete);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("清理过期备份配置文件出错", e);
            } finally {
                ThreadSleepUtil.sleepIngoreEx(3600000);//NOSONAR
            }
        }
    }
}
