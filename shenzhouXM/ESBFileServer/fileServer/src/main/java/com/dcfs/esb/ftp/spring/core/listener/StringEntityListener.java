package com.dcfs.esb.ftp.spring.core.listener;


import com.dcfs.esb.ftp.spring.core.entity.StringBaseEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

public class StringEntityListener {
    @PrePersist
    public void prePersist(Object obj) {
        if (obj instanceof StringBaseEntity) {
            StringBaseEntity entity = (StringBaseEntity) obj;
            entity.setCreateDate(new Date());
            entity.setUpdateDate(new Date());
        }
    }

    @PreUpdate
    public void preUpdate(Object obj) {
        if (obj instanceof StringBaseEntity) {
            StringBaseEntity entity = (StringBaseEntity) obj;
            entity.setUpdateDate(new Date());
        }
    }
}
