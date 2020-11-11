/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysmanager.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 系统管理员Entity
 *
 * @author lvchuan
 * @version 2016-06-23
 */
public class FtSysManager extends DataEntity<FtSysManager> {

    private static final long serialVersionUID = 1L;
    private String name;
    private String phone;
    private String email;
    private String systems;
    private String department;
    private String password;
    private String company;


    public FtSysManager() {
        super();
    }

    public FtSysManager(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 0, max = 256, message = "系统名称长度必须介于 0 和 256 之间")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getSystems() {
        return systems;
    }

    public void setSystems(String systems) {
        this.systems = systems;
    }

    @Length(min = 0, max = 256, message = "系统名称长度必须介于 0 和 256 之间")
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}