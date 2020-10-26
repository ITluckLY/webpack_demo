package com.dc.smarteam.modules.sys.entity;


import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by xuchuang on 2018/6/26.
 */
public class OptTag extends DataEntity<OptTag> {

    private String name;
    private String dictId;
    private Date createTime;
    private String remark;
    private int status = 1;
    private String style;
    private Dict dict;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Dict getDict() {
        return dict;
    }

    public void setDict(Dict dict) {
        this.dict = dict;
    }

    @Override
    public String toString() {
        return "OptTag{" +
                "name='" + name + '\'' +
                ", dictId='" + dictId + '\'' +
                ", createTime=" + createTime +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", style=" + style +
                ", dict=" + dict +
                '}';
    }
}
