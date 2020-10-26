package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * Created by huangzbb on 2016/8/12.
 */
public class FtFileRename extends DataEntity<FtFileRename> {
    private static final long serialVersionUID = 1L;

    private String type;            // 路径类型
    private String path;            // 文件或目录路径
    private String sysname;         // 系统名称

    public FtFileRename() {
        super();
    }

    public FtFileRename(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "路径类型长度必须介于 0 和 256 之间")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Length(min = 0, max = 256, message = "路径长度必须介于 0 和 256 之间")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }
}
