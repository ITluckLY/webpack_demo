package com.dcfs.esb.ftp.server.schedule;

import com.dcfs.esb.ftp.server.schedule.task.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

public class LoopChecker extends TimerTask {
    public static final String FILE_CLEAR = "FileClear";
    public static final String FILE_UPLOAD = "FileUpload";
    public static final String FILE_DOWNLOAD = "FileDownload";
    public static final String NODE_FILE_UPLOAD = "NodeFileUpload";
    public static final String DIR_FILE_UPLOAD = "DirFileUpload";
    private static final Logger log = LoggerFactory.getLogger(LoopChecker.class);
    private Schedule schedule;
    private Day day;
    private Time time;
    private String mission;
    private long count;
    private boolean isExecute;
    private Map<String, String> params;

    public LoopChecker(Schedule schedule, Day day, Time time, long count, boolean execute, String mission, Map<String, String> params) {
        this.schedule = schedule;
        this.day = day;
        this.time = time;
        this.count = count;
        this.isExecute = execute;
        this.mission = mission;
        this.params = params;
    }

    @Override
    public void run() {
        if (!isExecute || count < 0) {
            schedule.stop();
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
        String str = format.format(new Date());
        String mon = str.substring(0, 2);//NOSONAR
        String d = str.substring(2, 4);//NOSONAR
        String hour = str.substring(4, 6);//NOSONAR
        String min = str.substring(6, 8);//NOSONAR
        String second = str.substring(8, 10);//NOSONAR
        boolean b = day.matches(mon, d) && time.matches(hour, min, second);
        if (!b) return;
        if (count == 0) {
            LoopTask task = getTask(mission);
            task.init(params);
            log.info("开始执行任务{}", mission);
            try {
                task.execute();
            } catch (Exception e) {
                log.error("执行任务异常:" + mission, e);
            }
        } else if (count > 0) {
            LoopTask task = getTask(mission);
            task.init(params);
            log.info("开始执行任务{}", mission);
            try {
                task.execute();
            } catch (Exception e) {
                log.error("执行任务异常:" + mission, e);
            }
            count--;
            if (count == 0) isExecute = false;
        }
    }

    public LoopTask getTask(String name) {//NOSONAR
        LoopTask task = null;
        if (mission.equals(FILE_CLEAR)) {
            task = new FileClear();
        } else if (mission.equals(FILE_UPLOAD)) {
            task = new FileUpload();
        } else if (mission.equals(FILE_DOWNLOAD)) {
            task = new FileDownload();
        } else if (mission.equals(NODE_FILE_UPLOAD)) {
            task = new NodeFileUpload();
        } else if (mission.equals(DIR_FILE_UPLOAD)) {
            task = new DirFileUpload();
        }
        return task;
    }

}