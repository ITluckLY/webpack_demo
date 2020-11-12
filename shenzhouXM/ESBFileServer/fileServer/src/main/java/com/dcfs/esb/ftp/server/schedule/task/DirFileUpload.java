package com.dcfs.esb.ftp.server.schedule.task;

import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esb.ftp.server.schedule.LoopTask;
import com.dcfs.esb.ftp.server.system.IProtocol;
import com.dcfs.esb.ftp.server.system.ProtocolFactory;
import com.dcfs.esb.ftp.server.system.SystemInfo;
import com.dcfs.esb.ftp.server.system.SystemManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirFileUpload implements LoopTask {

    private static final Logger log = LoggerFactory.getLogger(DirFileUpload.class);
    private Map<String, String> params;

    public void execute() throws Exception {
        String dir = params.get("dir");
        String filter = params.get("filter");
        String destination = params.get("destination");
        String abpath = EsbFileManager.getInstance().getFileAbsolutePath(dir);
        final Pattern p = Pattern.compile(filter);
        File parent = new File(abpath);
        SystemInfo systemInfo = SystemManage.getInstance().getSystemInfo(destination);
        if (systemInfo == null) {
            log.error("未知的文件上传目标系统:{}", destination);
            return;
        }
        File[] children = parent.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                Matcher m = p.matcher(name);
                return m.matches();
            }
        });
        if (children == null) return;
        long start = System.currentTimeMillis();
        for (File f : children) {
            String fn = f.getName();
            try {
                log.info("文件上传中:{}", fn);
                long t1 = System.currentTimeMillis();
                IProtocol protocol = ProtocolFactory.getProtocol(systemInfo, fn, fn);
                if (protocol != null) protocol.uploadBySync();
                else log.error("protocol is null");
                long t2 = System.currentTimeMillis();
                log.info("文件上传完成,耗时{}", (t2 - t1));
            } catch (Exception e) {
                log.error("文件上传失败:" + fn, e);
            }
        }
        long end = System.currentTimeMillis();
        log.info("文件批量上传结束,总耗时{}", (end - start));
    }

    public void init(Map<String, String> params) {
        this.params = params;
    }

}
