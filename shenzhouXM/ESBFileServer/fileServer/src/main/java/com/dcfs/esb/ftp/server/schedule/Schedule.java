package com.dcfs.esb.ftp.server.schedule;

import com.dcfs.esb.ftp.utils.SafeArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;

public class Schedule {
    private static final Logger log = LoggerFactory.getLogger(Schedule.class);
    private String id;
    private String mission;
    private String nodeName;
    private int status;
    private long count;
    private boolean isExecute;
    private Day day;
    private Time time;
    private String description;
    private Map<String, String> params;
    private Timer timer;

    public Schedule(String id, String mission, String nodeName, int status, long count, String description, Day day, Time time, Map<String, String> params) {
        this.id = id;
        this.mission = mission;
        this.nodeName = nodeName;
        this.status = status;
        this.count = count;
        this.description = description;
        this.day = day;
        this.time = time;
        this.params = params;
        timer = new Timer(true);
    }

    public Schedule(String id, String mission, String nodeName, int status, String description, String expression, Map<String, String> params) {
        this.id = id;
        this.mission = mission;
        this.nodeName = nodeName;
        this.status = status;
        this.description = description;
        if (expression != null && !"".equals(expression)) {
            String[] s = expression.trim().split(" ", 5);
            this.day = new Day(s[0].trim(), s[1].trim());
            this.time = new Time(s[2].trim(), s[3].trim(), SafeArrayUtil.get(s, 4, "*"));
        }
        this.params = params;
        timer = new Timer(true);
        this.count = 0;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public void setDate(Day day) {
        this.day = day;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean isExecute() {
        return isExecute;
    }

    public void setExecute(boolean execute) {
        isExecute = execute;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void start() {
        if (status > 0) {
            isExecute = true;
            log.info("启动定时任务:{},执行次数:{}", id, count);
            LoopChecker checker = new LoopChecker(this, day, time, count, isExecute, mission, params);
            timer.schedule(checker, 0L, 999L);
        } else {
            log.info("启动定时任务:{},状态为不启动状态", id);
        }
    }

    public void stop() {
        log.info("停止定时任务#id:{},mission:{}", id, mission);
        timer.cancel();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[id:");
        sb.append(id);
        sb.append(",mission:");
        sb.append(mission);
        sb.append(",status:");
        sb.append(status);
        sb.append(",nodeName:");
        sb.append(nodeName);
        sb.append(",description:");
        sb.append(description);
        sb.append(",exprssion:(");
        sb.append(day).append(" ").append(time);
        sb.append("),paraeters:");
        sb.append(params);
        sb.append("]");
        return sb.toString();

    }

}
