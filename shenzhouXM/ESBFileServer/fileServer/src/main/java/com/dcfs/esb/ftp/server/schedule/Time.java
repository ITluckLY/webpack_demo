package com.dcfs.esb.ftp.server.schedule;

public class Time {
    private String hour;
    private String min;
    private String second;

    public Time(String hour, String min, String second) {
        this.hour = hour;
        this.min = min;
        this.second = second;
    }

    /**
     * 准确的时分
     *
     * @param hour
     * @param min
     * @return
     */
    public boolean matches(String hour, String min, String second) {
        String a = "*";
        boolean b1 = a.equals(this.hour) || this.hour.equals(hour);
        boolean b2 = a.equals(this.min) || this.min.equals(min);
        boolean b3 = a.equals(this.second) || this.second.equals(second);
        return b1 && b2 && b3;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String toString() {
        return hour + " " + min + " " + second;
    }

}
