package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.persistence.DataEntity;
import net.sf.json.JSONObject;

public class FtsApiParam extends DataEntity<FtsApiParam> {
    private static final long serialVersionUID = 7707916735126714834L;
    private String systemName;        // 名称
    private String servers;        // 值
    private String uid;        // 描述
    private String passwd;

    public FtsApiParam() {
        super();
    }
    public FtsApiParam(String id) {
        super(id);
    }
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}

