package com.dc.smarteam.modules.sysinfo.entity;

import org.hibernate.validator.constraints.Length;

/**
 * 系统管理员和系统关联
 * Created by lvchuan on 2016/6/23.
 */
public class FtRelation {
    private String system;
    private String admin;

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }


}
