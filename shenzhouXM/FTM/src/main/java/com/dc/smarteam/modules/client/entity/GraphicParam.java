package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 *
 * 客户端概况
 * liuyfal  20180327
 *
 */
public class GraphicParam extends DataEntity<GraphicParam> {

    private static final long serialVersionUID = 30100136181910980L;
    private String type;
    private String sys;
    private String user;
    private String client;
    private String transCode;
    private String all;
    private String detail;
    private Date beginDate;
    private Date endDate;

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
