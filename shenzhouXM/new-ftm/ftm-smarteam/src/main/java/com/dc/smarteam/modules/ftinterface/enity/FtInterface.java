package com.dc.smarteam.modules.ftinterface.enity;

import com.dc.smarteam.modules.keys.entity.FtKey;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.user.entity.FtUser;

/**
 * Created by Administrator on 2019/12/10.
 */
public class FtInterface {

    private static final long serialVersionUID = 1L;


    private String target;


    private String operateType;

    private FtUser ftUser;

    private FtKey ftKey;

    private FtServiceInfo ftServiceInfo;

    private PutAuthEntity putAuthEntity;

    private Data data;


    public PutAuthEntity getPutAuthEntity() {
        return putAuthEntity;
    }

    public void setPutAuthEntity(PutAuthEntity putAuthEntity) {
        this.putAuthEntity = putAuthEntity;
    }

    public FtServiceInfo getFtServiceInfo() {
        return ftServiceInfo;
    }

    public void setFtServiceInfo(FtServiceInfo ftServiceInfo) {
        this.ftServiceInfo = ftServiceInfo;
    }

    public FtKey getFtKey() {
        return ftKey;
    }

    public void setFtKey(FtKey ftKey) {
        this.ftKey = ftKey;
    }

    public FtUser getFtUser() {
        return ftUser;
    }

    public void setFtUser(FtUser ftUser) {
        this.ftUser = ftUser;
    }


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setFtUser(Data data) {
    }
}
