package com.dc.smarteam.common.json;

import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2016/3/5.
 */
public class SendEntity {
    private String target;
    private String operateType;
    private JSONObject data;

    public SendEntity() {
    }

    public SendEntity(String target, String operateType, JSONObject data) {
        this.target = target;
        this.operateType = operateType;
        this.data = data;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
