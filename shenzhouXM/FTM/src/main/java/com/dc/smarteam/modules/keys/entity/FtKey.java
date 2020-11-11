package com.dc.smarteam.modules.keys.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 公私钥管理Entity
 * @author gaona
 * 2019/12/5
 */
public class FtKey extends DataEntity<FtKey> implements CfgData {
    private static final long serialVersionUID = 1L;

    private String user;
    private String type;
    private String content;


    private String systemName;

//    public FtKey() {
//        super();
//    }
//
//    public FtKey(String id) {
//        super(id);
//    }

    @Length(min = 0, max = 30, message = "名称长度必须介于 0 和 30 之间")
    public String getUser() {return user; }

    public void setUser(String user) { this.user = user; }

    @Length(min = 0, max = 256, message = "目录权限长度必须介于 0 和 256 之间")
    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    @Length(min = 0, max = 2000, message = "目录权限长度必须介于 0 和 2000 之间")
    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    @Override
    public String getParamName() {
        return user;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    public String getSystemName() {
        return systemName;
    }
}
