/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.protocol.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;

/**
 * 对应后台的systems.xml
 *
 * @author kern
 * @version 2015-12-24
 */
public class SysProtocol extends DataEntity<SysProtocol> implements CfgData{

    private static final long serialVersionUID = 1L;
    private String name; //目标名称：
    private String protocol;//协议名称：
    private String ip;//地址：
    private String port;//端口：
    private String username;// 用户名：
    private String password;  //密码
    private String uploadPath;  //上传路径：
    private String downloadPath;//下载路径

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String getParamName() {
        return this.getName();
    }
}