package com.dcfs.esb.ftp.spring.core.entity;


import com.dcfs.esb.ftp.spring.core.listener.StringEntityListener;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EntityListeners({StringEntityListener.class})
@MappedSuperclass
public abstract class StringBaseEntity implements Serializable {

    @Temporal(TemporalType.DATE)
    @Column(updatable = false, name = "create_date")
    private Date createDate;

    @Column(updatable = false, name = "create_by")
    private String createBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_by")
    private String updateBy;

    private String remarks;

    @Column(name = "del_flag")
    private String delFlag;

    public abstract String getId();

    public abstract void setId(String id);

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        StringBaseEntity localBaseEntity = (StringBaseEntity) obj;
        return getId() != null && getId().equals(localBaseEntity.getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(this.getId())
                .hashCode();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}