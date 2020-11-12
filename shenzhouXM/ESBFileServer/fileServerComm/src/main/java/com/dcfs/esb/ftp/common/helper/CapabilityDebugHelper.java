package com.dcfs.esb.ftp.common.helper;

import com.dcfs.esb.ftp.utils.StringTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mocg on 2016/12/27.
 */
public class CapabilityDebugHelper {
    private static final Logger log = LoggerFactory.getLogger(CapabilityDebugHelper.class);
    private static final ThreadLocal<List<String>> threadLocalOfDebug = new ThreadLocal<List<String>>();
    private static boolean debug = false;
    private static boolean outBean = false;

    public static void init() {
        if (debug) {
            if (threadLocalOfDebug.get() == null) threadLocalOfDebug.set(new LinkedList<String>());//NOSONAR
        }
    }

    /**
     * @param flag 不能以数字开头
     */
    public static void markCurrTime(String flag) {
        if (debug) {
            List<String> list = threadLocalOfDebug.get();
            if (list != null) list.add(System.nanoTime() + flag);
        }
    }

    public static List<String> getMarkTimes() {
        if (debug) {
            return threadLocalOfDebug.get();
        } else return new LinkedList<String>();
    }

    public static void clean() {
        if (debug) {
            List<String> list = threadLocalOfDebug.get();
            if (list != null) list.clear();
            threadLocalOfDebug.remove();
        }
    }

    public static void printAndClean(long nano) {
        if (debug) {
            List<String> list = threadLocalOfDebug.get();
            if (list == null) return;
            StringBuilder builder = new StringBuilder();
            long preTime = 0;
            for (String log : list) {
                long time = getNanoTime(log);
                builder.append(log).append("---timeNano:").append(time - preTime).append("\r\n");
                preTime = time;
            }
            builder.append("size:").append(list.size());
            long totalTimeNano = 0;
            if (!list.isEmpty()) {
                String first = list.get(0);
                String last = list.get(list.size() - 1);
                totalTimeNano = getNanoTime(last) - getNanoTime(first);
            }
            builder.append("\r\ntotalTimeNano:").append(totalTimeNano);
            String str = builder.toString();
            log.warn("nano:{}#CapabilityTest:\r\n{}", nano, str);
            clean();
        }
    }

    private CapabilityDebugHelper() {
    }

    private static long getNanoTime(String log) {
        return StringTool.tolong(getNanoTimeStr(log));
    }

    private static String getNanoTimeStr(String str) {
        int len = str.length();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                len = i;
                break;
            }
        }
        return str.substring(0, len);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        CapabilityDebugHelper.debug = debug;
    }

    public static boolean isOutBean() {
        return outBean;
    }

    public static void setOutBean(boolean outBean) {
        CapabilityDebugHelper.outBean = outBean;
    }
}
