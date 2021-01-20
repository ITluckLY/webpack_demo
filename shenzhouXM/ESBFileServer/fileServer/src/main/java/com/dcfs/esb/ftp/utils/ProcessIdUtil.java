package com.dcfs.esb.ftp.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Created by mocg on 2017/3/6.
 */
public class ProcessIdUtil {
    private static final Logger log = LoggerFactory.getLogger(ProcessIdUtil.class);

    private static final String PID_FILE_NAME = "pid.dcfs";

    public static int getProcessID() {
        try {
            //返回Java虚拟机的运行时系统的托管Bean。
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            if (log.isDebugEnabled()) log.debug(runtimeMXBean.getName());
            Integer pid = Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
            log.info("pid is {}", pid);
            return pid;
        } catch (Exception e) {
            log.error("获取PID出错", e);
            return -1;
        }
    }

    // 创建文件 写文件 退出删除文件
    public static void writeOutPID() throws IOException {
        String path = MClassLoaderUtil.getFilePath(PID_FILE_NAME);
        File file = new File(path); // 新建文件 +路径
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtils.write(String.valueOf(getProcessID()), fos);
            fos.flush();
            //退出jvm自动删除pid文件
            file.deleteOnExit();
        }
    }

    public static boolean cleanPID() {
        try {
            String path = MClassLoaderUtil.getFilePath(PID_FILE_NAME);
            File file = new File(path);
            return !file.exists() || file.delete();
        } catch (Exception e) {
            log.error("清理PID出错", e);
            return false;
        }
    }

    private ProcessIdUtil() {
    }
}
