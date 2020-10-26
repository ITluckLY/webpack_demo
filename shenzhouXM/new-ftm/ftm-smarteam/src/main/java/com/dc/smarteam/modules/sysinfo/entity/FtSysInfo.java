/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysinfo.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 系统管理Entity
 *
 * @author lvchuan
 * @version 2016-06-22
 */
public class FtSysInfo extends DataEntity<FtSysInfo> {

    private static final long serialVersionUID = 1L;
    private String name;
    private String des;
    private String admin;

    //-------------------------
    private String adminId;
    //-------------------------

    private String sysNodeModel;            //节点模式      单节点模式（single），多节点并行模式(more)，主备模式(ms) [设置后不可改]
    private String switchModel;        //主备切换      自动模式(auto)、手动模式(handle)
    private String storeModel;       //文件存储模式  单点（single）、同步(sync)、异步(async)


    public FtSysInfo() {
        super();
    }

    public FtSysInfo(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 0, max = 256, message = "系统名称长度必须介于 0 和 256 之间")
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getSysNodeModel() {
        return sysNodeModel;
    }

    public void setSysNodeModel(String sysNodeModel) {
        this.sysNodeModel = sysNodeModel;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getSwitchModel() {
        return switchModel;
    }

    public void setSwitchModel(String switchModel) {
        this.switchModel = switchModel;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getStoreModel() {
        return storeModel;
    }

    public void setStoreModel(String storeModel) {
        this.storeModel = storeModel;
    }
}