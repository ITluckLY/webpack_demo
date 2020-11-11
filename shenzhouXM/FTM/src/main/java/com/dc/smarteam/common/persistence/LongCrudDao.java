package com.dc.smarteam.common.persistence;

import java.util.List;

/**
 * DAO支持类实现 id类型为Long 数据库类型为数字型
 *
 * @param <T>LongCrudDao
 *
 */
public interface LongCrudDao<T> extends BaseDao {

    /**
     * 获取单条数据
     *
     * @param id
     */
    T get(Long id);


    /**
     * 获取单条数据
     *
     * @param entity
     */
    T get(T entity);

    /**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     *
     * @param entity
     */
    List<T> findList(T entity);

    /**
     * 查询所有数据列表
     *
     * @param entity
     */
    List<T> findAllList(T entity);

    /**
     * 插入数据
     *
     * @param entity
     */
    int insert(T entity);

    /**
     * 更新数据
     *
     * @param entity
     */
    int update(T entity);

    /**
     * 删除数据
     *
     * @param id
     * @see int delete(T entity)
     */
    int delete(Long id);

    /**
     * 删除数据
     *
     * @param entity
     * @return
     */
    int delete(T entity);

}