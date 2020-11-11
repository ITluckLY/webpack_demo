/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.component.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 组件管理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtComponent extends DataEntity<FtComponent> implements CfgData{

    private static final long serialVersionUID = 1L;
    private String name;        // 名称
    private String implement; //实现类
    private String des;        // 中文描述
    private String param;        // 参数

    public FtComponent() {
        super();
    }

    public FtComponent(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 0, max = 256, message = "中文描述长度必须介于 0 和 256 之间")
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Length(min = 0, max = 256, message = "参数长度必须介于 0 和 256 之间")
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getImplement() {
        return implement;
    }

    public void setImplement(String implement) {
        this.implement = implement;
    }

    @Override
    public String getParamName() {
        return this.getName();
    }
}