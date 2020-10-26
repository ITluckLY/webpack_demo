/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.modules.file.entity.FtFileUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件管理DAO接口
 *
 * @author liwang
 * @version 2016-01-12
 */
@Mapper
public interface FtFileUploadDao extends CrudDao<FtFileUpload> {

    public List<FtFileUpload> findAll(FtFileUpload ftFileUpload);

    public void save(FtFileUpload ftFileUpload);

    public int update(FtFileUpload ftFileUpload);

    public int delOne(String id);


    public List<FtFileUpload> getFtFileUploadList(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    public int getFtFileUploadTotal();

    public FtFileUpload getFtFileUploadById(@Param("id") String id);


}