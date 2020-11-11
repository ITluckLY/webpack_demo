package com.dc.smarteam.helper;

import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    private DateHelper() {
    }

    /**
     * 获取当天零点
     */
    public static Date getStartDate() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTime();
    }

    /**
     * 获取第二天零点
     */
    public static Date getEndDate() {
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(new Date());
        nextDate.set(Calendar.HOUR_OF_DAY, 0);
        nextDate.set(Calendar.MINUTE, 0);
        nextDate.set(Calendar.SECOND, 0);
        nextDate.add(Calendar.DAY_OF_MONTH, +1);
        return nextDate.getTime();
    }

}
