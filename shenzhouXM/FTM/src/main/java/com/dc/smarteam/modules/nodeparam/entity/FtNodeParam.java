/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.nodeparam.entity;

import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.persistence.DataEntity;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.Length;

/**
 * 节点参数Entity
 *
 * @author liwang
 * @version 2016-01-11
 */
public class FtNodeParam extends DataEntity<FtNodeParam> {
    private static final long serialVersionUID = 1L;
    private String name;        // 名称
    private String value;        // 值
    private String des;        // 描述
    private String nodeId;

    public FtNodeParam() {
        super();
    }

    public FtNodeParam(JSONObject json) {
        this.name = JsonToEntityFactory.getInstance().getString(json, "@key");
        this.value = JsonToEntityFactory.getInstance().getString(json, "#text");
        this.des = JsonToEntityFactory.getInstance().getString(json, "@describe");
    }

    @Length(max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(max = 256, message = "值长度必须介于 0 和 256 之间")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Length(max = 256, message = "描述长度必须介于 0 和 256 之间")
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

}