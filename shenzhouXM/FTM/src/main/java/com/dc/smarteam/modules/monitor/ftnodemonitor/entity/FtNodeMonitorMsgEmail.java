/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * Entity
 *
 * @author lvchuan
 * @version 2016-06-26
 */
public class FtNodeMonitorMsgEmail extends DataEntity<FtNodeMonitorMsgEmail> {

    private static final long serialVersionUID = 1L;
    private String name;                 //组名
    private String telNoList;            //电话号码
    private String emailList;            //邮件

//    private String sendMsg;                  //发送内容

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public String getName() {
        return name;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public String getTelNoList() {
        return telNoList;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public void setTelNoList(String telNoList) {
        this.telNoList = telNoList;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public String getEmailList() {
        return emailList;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public void setEmailList(String emailList) {
        this.emailList = emailList;
    }

}
