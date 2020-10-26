/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.ipconfig.entity;

import com.dc.smarteam.aspectCfg.base.Exclude;
import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import com.dc.smarteam.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

/**
 * IP控制Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
@Exclude
public class FtUserIp extends DataEntity<FtUserIp> implements CfgData{

    private static final long serialVersionUID = 1L;
    private String ipAddress;            // IP地址
    private String des;            // 中文描述
    private String state;            // 状态
    private String ftUserId;            // 用户ID
    private User user;            // 用户ID
    private String systemName;      //20160718添加系统选项

    public FtUserIp() {
        super();
    }

    public FtUserIp(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "系统名称长度必须介于 0 和 256 之间")
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Length(min = 0, max = 256, message = "IP地址长度必须介于 0 和 256 之间")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Length(min = 0, max = 256, message = "中文描述长度必须介于 0 和 256 之间")
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFtUserId() {
        return ftUserId;
    }

    public void setFtUserId(String ftUserId) {
        this.ftUserId = ftUserId;
    }

    @Override
    public String getParamName() {
        return ftUserId+"/"+ipAddress;
    }
}