package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by mocg on 2017/4/13.
 */
public abstract class BaseModel {
    @XStreamAsAttribute
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void init() {
    }
}
