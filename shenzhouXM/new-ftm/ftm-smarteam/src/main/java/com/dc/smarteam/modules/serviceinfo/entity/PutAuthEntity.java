/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.entity;

import com.dc.smarteam.common.persistence.DataEntity;

/**
 * Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class PutAuthEntity extends DataEntity<PutAuthEntity> {

    private static final long serialVersionUID = 1L;
    private String trancode;              // 交易码
    private String directoy;
    private String userName;

    public String getTrancode() {
        return trancode;
    }

    public void setTrancode(String trancode) {
        this.trancode = trancode;
    }

    public String getDirectoy() {
        return directoy;
    }

    public void setDirectoy(String directoy) {
        this.directoy = directoy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}