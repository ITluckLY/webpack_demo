/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.user.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 用户管理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtUser extends DataEntity<FtUser> implements CfgData{

    private static final long serialVersionUID = 1L;
    private String name;          // 名称
    private String systemName;    // 系统名称
    private String des;           // 中文描述
    private String userDir;       // 用户目录
    private String permession;    // 目录权限
    private String pwd;           //密码
    private String clientAddress; //客户端地址

    public FtUser() {
        super();
    }

    public FtUser(String id) {
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
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Length(min = 0, max = 256, message = "中文描述长度必须介于 0 和 256 之间")
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Length(min = 0, max = 256, message = "用户目录长度必须介于 0 和 256 之间")
    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getPermession() {
        return permession;
    }

    public void setPermession(String permession) {
        this.permession = permession;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    @Override
    public String toString() {
        return "FtUser{" +
                "name='" + name + '\'' +
                ", systemName='" + systemName + '\'' +
                ", des='" + des + '\'' +
                ", userDir='" + userDir + '\'' +
                ", permession='" + permession + '\'' +
                ", pwd='" + pwd + '\'' +//NOSONAR
                ", clientAddress='" + clientAddress + '\'' +
                '}';
    }


    @Override
    //获取参数名(用于识别该条参数的标志？)
    public String getParamName() {
        return this.getName();
    }
}