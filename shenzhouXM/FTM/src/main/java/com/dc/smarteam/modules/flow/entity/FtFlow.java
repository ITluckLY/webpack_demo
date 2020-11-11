/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.flow.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 流程管理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtFlow extends DataEntity<FtFlow> implements CfgData{

    private static final long serialVersionUID = 1L;
    private String name;        // 名称
    private String des;        // 中文描述
    private String systemName;        // 上传系统
    private String components; //组件链接字符串

    public FtFlow() {
        super();
    }

    public FtFlow(String id) {
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

    @Length(min = 0, max = 256, message = "上传系统长度必须介于 0 和 256 之间")
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    @Override
    public String getParamName() {
        return name;
    }
}