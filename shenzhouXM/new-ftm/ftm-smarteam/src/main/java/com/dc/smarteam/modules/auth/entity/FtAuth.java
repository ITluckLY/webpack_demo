/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.auth.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 目录权限Entity
 *
 * @author yangxc
 * @version 2016-04-13
 */
public class FtAuth extends DataEntity<FtAuth> {

    private static final long serialVersionUID = 1L;
    private String name;        // 名称
    private String path;        // 路径
    private String permession;        // 目录权限
    //-------------------------------------

    public FtAuth() {
        super();
    }

    public FtAuth(String id) {
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
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getPermession() {
        return permession;
    }

    public void setPermession(String permession) {
        this.permession = permession;
    }

}