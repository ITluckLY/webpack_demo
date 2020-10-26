/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.flow.entity;

import com.dc.smarteam.common.persistence.DataEntity;

/**
 * 规则管理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class Rule extends DataEntity<Rule> {

    private static final long serialVersionUID = 1L;
    private String name;
    private String onPut;
    private String className;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnPut() {
        return onPut;
    }

    public void setOnPut(String onPut) {
        this.onPut = onPut;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}