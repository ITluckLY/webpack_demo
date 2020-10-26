package com.dc.smarteam.common.persistence;

import com.dc.smarteam.common.supcan.annotation.treelist.cols.SupCol;
import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.helper.IDHelper;
import com.dc.smarteam.modules.sys.entity.User;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 数据Entity类
 *
 * @author mocg
 */
public abstract class LongDataEntity<T> extends NIDBaseEntity<T, Long> {

    protected Long id;
    protected String remarks;   // 备注
    protected User createBy;    // 创建者
    protected Date createDate;  // 创建日期
    protected User updateBy;    // 更新者
    protected Date updateDate;  // 更新日期
    protected String delFlag;   // 删除标记（0：正常；1：删除；2：审核）

    public LongDataEntity() {
        super();
        this.delFlag = DEL_FLAG_NORMAL;
    }

    public LongDataEntity(Long id) {
        super(id);
    }

    @SupCol(isUnique = "true", isHide = "true")
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 插入之前执行方法，需要手动调用
     */
    @Override
    public void preInsert() {
        // 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
        if (!this.isNewRecord) {
            setId(IdGen.longUUID());
        }
        User user = UserUtils.getUser();
        if (IDHelper.isNotEmpty(user.getId())) {
            this.updateBy = user;
            this.createBy = user;
        }
        this.updateDate = new Date();
        this.createDate = this.updateDate;
    }

    /**
     * 更新之前执行方法，需要手动调用
     */
    @Override
    public void preUpdate() {
        User user = UserUtils.getUser();
        if (IDHelper.isNotEmpty(user.getId())) {
            this.updateBy = user;
        }
        this.updateDate = new Date();
    }


    @JsonIgnore
    public User getCreateBy() {
        if (null != createBy) {
            createBy = UserUtils.get(createBy.id);
        }
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonIgnore
    public User getUpdateBy() {
        if (null != updateBy) {
            updateBy = UserUtils.get(updateBy.id);
        }
        return updateBy;
    }

    public void setUpdateBy(User updateBy) {
        this.updateBy = updateBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @JsonIgnore
    @Length(min = 1, max = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

}
