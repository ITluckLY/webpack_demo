package com.dc.smarteam.common.persistence;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.supcan.annotation.treelist.SupTreeList;
import com.dc.smarteam.common.supcan.annotation.treelist.cols.SupCol;
import com.dc.smarteam.modules.sys.entity.User;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Map;

/**
 * Entity支持类
 * 泛型ID
 *
 * @author mocg
 */
@SupTreeList
public abstract class NIDBaseEntity<T, D> implements Serializable {
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final String DEL_FLAG_AUDIT = "2";

    /**
     * 当前用户
     */
    protected User currentUser;
    /**
     * 当前实体分页对象
     */
    protected Page<T> page;
    /**
     * 自定义SQL（SQL标识，SQL内容）
     */
    protected transient Map<String, String> sqlMap;
    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    protected boolean isNewRecord = false;

    public NIDBaseEntity() {
    }

    public NIDBaseEntity(D id) {
        this();
        this.setId(id);
    }

    @SupCol(isUnique = "true", isHide = "true")
    public abstract D getId();

    public abstract void setId(D id);

    @JsonIgnore
    @XmlTransient
    public User getCurrentUser() {
        if (currentUser == null) {
            currentUser = UserUtils.getUser();
        }
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @JsonIgnore
    @XmlTransient
    public Page<T> getPage() {
        if (page == null) {
            page = new Page<T>();
        }
        return page;
    }

    public Page<T> setPage(Page<T> page) {
        this.page = page;
        return page;
    }

    @JsonIgnore
    @XmlTransient
    public Map<String, String> getSqlMap() {
        if (sqlMap == null) {
            sqlMap = Maps.newHashMap();
        }
        return sqlMap;
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    /**
     * 插入之前执行方法，子类实现
     */
    public abstract void preInsert();

    /**
     * 更新之前执行方法，子类实现
     */
    public abstract void preUpdate();

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     *
     * @return
     */
    public boolean getIsNewRecord() {
        return isNewRecord || getId() == null;
    }

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    public void setIsNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    /**
     * 全局变量对象
     */
    @JsonIgnore
    public Global getGlobal() {
        return Global.getInstance();
    }

    /**
     * 获取数据库名称
     */
    @JsonIgnore
    public String getDbName() {
        return Global.getConfig("jdbc.type");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof NIDBaseEntity)) return false;
        NIDBaseEntity that = (NIDBaseEntity) obj;
        return null != this.getId() && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(this.getId())
                .hashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
