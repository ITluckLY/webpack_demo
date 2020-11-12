package com.dcfs.esb.ftp.server.schedule;

import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.SafeArrayUtil;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ScheduleManager {
    private static final Logger log = LoggerFactory.getLogger(ScheduleManager.class);
    private static final Object lock = new Object();
    private static ScheduleManager instance;
    private Map<String, Schedule> map;

    private ScheduleManager() {
        init();
    }

    public static ScheduleManager getInstance() {
        if (instance != null) return instance;
        synchronized (lock) {
            if (instance == null) instance = new ScheduleManager();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Map<String, Schedule> tmpMap = new HashMap<>();
        try {
            Document doc = CachedCfgDoc.getInstance().loadCrontab();
            List l = doc.selectNodes("/schedules/task");
            Element e = null;
            for (Object aL : l) {
                try {
                    e = (Element) aL;
                    String id = e.attributeValue("ID");
                    String description = e.attributeValue("description");
                    String st = e.attributeValue("status");
                    int status = Integer.parseInt(st);
                    String nodeName = e.attributeValue("nodeName");
                    long count = Long.parseLong(e.attributeValue("count", "0"));
                    String exp = e.element("timeExp").getText().trim();
                    String[] strArr = exp.split(" ", 5);
                    Day day = new Day(strArr[0].trim(), strArr[1].trim());
                    Time time = new Time(strArr[2].trim(), strArr[3].trim(), SafeArrayUtil.get(strArr, 4, "*"));
                    String mission = e.element("mission").getText().trim();
                    String p = e.element("params").getText().trim();
                    Map<String, String> params = new HashMap<>();
                    String[] ps = p.split(",");
                    for (String param : ps) {
                        int idx = param.indexOf('=');
                        if (idx < 1) {
                            log.error("格式错误的定时任务参数:{}", param);
                            continue;
                        }
                        String key = param.substring(0, idx);
                        String value = param.substring(idx + 1);
                        params.put(key, value);
                    }
                    //节点名称为空或与当前节点名称相同才加载定时任务
                    if (isCurrNodeTask(nodeName)) {
                        Schedule sc = new Schedule(id, mission, nodeName, status, count, description, day, time, params);
                        log.info("加载定时任务:{}", sc);
                        tmpMap.put(id, sc);
                    }
                } catch (Exception ex) {
                    log.error("加载定时任务出错,任务配置:\n" + (e == null ? "null" : e.asXML()), ex);
                }
            }
            this.map = tmpMap;
        } catch (Exception e) {
            log.error("加载定时任务出错", e);
        }
    }

    public boolean addOne(String id, Schedule sc) {
        if (map.containsKey(id)) {
            log.info("已存在重复ID的定时任务");
            return false;
        }
        if (!isCurrNodeTask(sc.getNodeName())) return false;
        map.put(id, sc);
        return true;
    }

    public void deleteOne(String id) {
        Schedule sc = map.get(id);
        if (sc != null) {
            sc.stop();
            map.remove(id);
        }
    }

    public void startAll() {
        if (map == null) {
            log.warn("ScheduleManager.startAll# map is null");
            return;
        }
        Set<Entry<String, Schedule>> entrys = map.entrySet();
        for (Entry<String, Schedule> entry : entrys) {
            Schedule sc = entry.getValue();
            sc.start();
        }
    }

    public void stopAll() {
        if (map == null) return;
        Set<Entry<String, Schedule>> entrys = map.entrySet();
        for (Entry<String, Schedule> entry : entrys) {
            Schedule sc = entry.getValue();
            sc.stop();
        }
    }

    public void startOne(String id) {
        Schedule sc = map.get(id);
        sc.start();
    }

    public void stopOne(String id) {
        Schedule sc = map.get(id);
        sc.stop();
    }

    public void reInit() {
        stopAll();
        if (map != null) map.clear();
        init();
        startAll();
    }

    /**
     * 节点名称为空或与当前节点名称相同才加载定时任务
     *
     * @param nodeName
     * @return
     */
    public boolean isCurrNodeTask(String nodeName) {
        return StringUtils.isEmpty(nodeName) || StringUtils.equalsIgnoreCase(nodeName, FtpConfig.getInstance().getNodeName());
    }
}




