package com.dcfs.esb.ftp.server.schedule.task;

import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.schedule.LoopTask;
import com.dcfs.esb.ftp.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class FileClear implements LoopTask {
    private static final Logger log = LoggerFactory.getLogger(FileClear.class);
    private Map<String, String> params;

    private long currentTimeMillis;
    private long lkeeptime;

    /**
     * dengdb add
     *
     * @throws Exception
     */
    //dir=/esb,user=/esb,keeptime=3d,isArchive=true,archivepath=/esb
    public void execute() throws Exception {
        //清理路径
        String dir = params.get("dir");
        //保留时间
        String keeptime = params.get("keeptime");

        //非null 处理
        if (dir == null) return;
        if (keeptime == null) return;

        dir = FileUtil.delDuplicateSeparator(dir);
        String rootPath = FtpConfig.getInstance().getFileRootPath();
        File dirFile = new File(rootPath, dir);
        if (!dirFile.exists()) return;

        lkeeptime = parseTime(keeptime);
        currentTimeMillis = System.currentTimeMillis();

        if (dirFile.isDirectory()) {
            if ("".equals(dir) || "/".equals(dir) || "\\".equals(dir)) deleteRootDir(dirFile);
            else deleteDir(dirFile);
        } else {
            deleteFile(dirFile);
        }

        long end = System.currentTimeMillis();
        log.info("文件批量清理结束,总耗时{}", (end - currentTimeMillis));
    }

    private void deleteRootDir(File dir) {
        if (!dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) deleteFile(file);
            else if (file.isDirectory()) deleteDir(file);
        }
    }

    private void deleteDir(File dir) {
        if (!dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) deleteFile(file);
            else if (file.isDirectory()) deleteDir(file);
        }
        FileUtils.deleteQuietly(dir);
    }

    private void deleteFile(File file) {
        if (!file.isFile()) return;
        try {
            long lastMoodify = file.lastModified();
            if (currentTimeMillis - lastMoodify > lkeeptime) {
                boolean delete = file.delete();
                if (log.isDebugEnabled()) log.debug("清理文件:{}#{}", delete, file.getAbsolutePath());
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) log.error("清理文件删除异常#" + file.getAbsolutePath(), e);
        }
    }

    private long parseTime(String exp) {
        exp = exp.trim().toLowerCase();
        long value = 0L;
        if (exp.endsWith("d")) {//天
            String num = exp.substring(0, exp.length() - 1);
            value = Integer.parseInt(num) * 24L * 60 * 60 * 1000;
        } else if (exp.endsWith("h")) {//小时
            String num = exp.substring(0, exp.length() - 1);
            value = Integer.parseInt(num) * 60L * 60 * 1000;
        } else if (exp.endsWith("m")) {//分钟
            String num = exp.substring(0, exp.length() - 1);
            value = Integer.parseInt(num) * 60L * 1000;
        }
        return value;
    }

    public void init(Map<String, String> params) {
        this.params = params;
    }

}
