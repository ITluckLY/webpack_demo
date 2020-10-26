package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;

/**
 * Created by xuchuang on 2018/6/13.
 */
public class FtClientStatus extends DataEntity<FtClientStatus> implements CfgData {

    private String id;
    private String name;
    private String mode;
    private String status;
    private String type;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getParamName() {
        return id;
    }

    public boolean isSel(FtClientStatus ftClientStatus){

        boolean b1 = true;
        boolean b2 = true;
        boolean b3 = true;
        boolean b4 = true;
        boolean b5 = true;

        if(ftClientStatus.getMode()!=null && !"".equals(ftClientStatus.getMode())){
            b1 = this.mode.equals(ftClientStatus.getMode());
        }
        if(ftClientStatus.getType()!=null && !"".equals(ftClientStatus.getType())){
            b2 = this.type.equals(ftClientStatus.getType());
        }
        if(ftClientStatus.getStatus()!=null && !"".equals(ftClientStatus.getStatus())){
            b3 = this.status.equals(ftClientStatus.getStatus());
        }
        if(ftClientStatus.getId()!=null && !"".equals(ftClientStatus.getId())){
            b4 = this.id.contains(ftClientStatus.getId());
        }
        if(ftClientStatus.getName()!=null && !"".equals(ftClientStatus.getName())){
            b5 = this.name.contains(ftClientStatus.getName());
        }

        return b1 & b2 & b3 & b4 & b5;
    }

    @Override
    public String toString() {
        return "FtClientStatus{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mode='" + mode + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
