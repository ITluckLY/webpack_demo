package com.dcfs.esb.ftp.spring.core.listener;


import com.dcfs.esb.ftp.spring.core.entity.BaseEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

public class EntityListener {
    @PrePersist
    public void prePersist(Object obj) {
        if (obj instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) obj;
            entity.setCreatedTime(new Date());
            entity.setModifiedTime(new Date());
        }
    }

    @PreUpdate
    public void preUpdate(Object obj) {
        if (obj instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) obj;
            entity.setModifiedTime(new Date());
        }
    }
}
