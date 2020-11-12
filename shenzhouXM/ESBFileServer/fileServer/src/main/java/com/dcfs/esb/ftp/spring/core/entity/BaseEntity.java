package com.dcfs.esb.ftp.spring.core.entity;


import com.dcfs.esb.ftp.spring.core.listener.EntityListener;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EntityListeners({EntityListener.class})
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false, name = "created_time")
    private Date createdTime;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, name = "modified_time")
    private Date modifiedTime;

    public abstract Long getId();

    public abstract void setId(Long id);

    public Date getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return this.modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        BaseEntity localBaseEntity = (BaseEntity) obj;
        return getId() != null && getId().equals(localBaseEntity.getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(this.getId())
                .hashCode();
    }
}