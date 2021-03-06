/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.route.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 交易路由Entity
 *
 * @author kern
 * @version 2015-12-24
 */
public class EamRouteAlg extends DataEntity<EamRouteAlg> {

    private static final long serialVersionUID = 1L;
    private String name;        // 路由名称
    private String chineseName;        // 路由简称
    private String routeBasis;        // 基础路由
    private String rtableMaintain;        // RTABLE_MAINTAIN
    private String rtableStore;        // RTABLE_STORE
    private String delFlg;        // 删除标记

    public EamRouteAlg() {
        super();
    }

    public EamRouteAlg(String id) {
        super(id);
    }

    @Length(min = 1, max = 255, message = "路由名称长度必须介于 1 和 255 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 0, max = 255, message = "路由简称长度必须介于 0 和 255 之间")
    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    @Length(min = 0, max = 255, message = "基础路由长度必须介于 0 和 255 之间")
    public String getRouteBasis() {
        return routeBasis;
    }

    public void setRouteBasis(String routeBasis) {
        this.routeBasis = routeBasis;
    }

    @Length(min = 0, max = 255, message = "RTABLE_MAINTAIN长度必须介于 0 和 255 之间")
    public String getRtableMaintain() {
        return rtableMaintain;
    }

    public void setRtableMaintain(String rtableMaintain) {
        this.rtableMaintain = rtableMaintain;
    }

    @Length(min = 0, max = 255, message = "RTABLE_STORE长度必须介于 0 和 255 之间")
    public String getRtableStore() {
        return rtableStore;
    }

    public void setRtableStore(String rtableStore) {
        this.rtableStore = rtableStore;
    }

    @Length(min = 0, max = 1, message = "删除标记长度必须介于 0 和 1 之间")
    public String getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(String delFlg) {
        this.delFlg = delFlg;
    }

}