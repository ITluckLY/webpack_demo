package com.dcfs.esb.ftp.server.schedule;

public class Day {
    private String month;
    private String day;//NOSONAR

    public Day(String month, String day) {
        this.month = month;
        this.day = day;
    }

    /**
     * 准确的月日
     *
     * @param month
     * @param day
     * @return
     */
    public boolean matches(String month, String day) {
        String a = "*";
        boolean b1 = a.equals(this.month) || this.month.equals(month);
        boolean b2 = a.equals(this.day) || this.day.equals(day);
        return b1 && b2;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String toString() {
        return month + " " + day;
    }
}
