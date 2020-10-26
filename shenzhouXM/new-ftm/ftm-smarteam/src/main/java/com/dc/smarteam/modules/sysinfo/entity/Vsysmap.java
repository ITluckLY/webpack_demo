package com.dc.smarteam.modules.sysinfo.entity;

/**
 * Created by mocg on 2017/3/2.
 */
public class Vsysmap {
    private String key;
    private String val;

    public Vsysmap() {
    }

    public Vsysmap(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
