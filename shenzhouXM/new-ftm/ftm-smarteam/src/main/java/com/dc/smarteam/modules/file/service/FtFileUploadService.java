package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.modules.file.entity.FtFileRollbackLog;
import com.dc.smarteam.modules.file.entity.FtFileUpload;
import com.dc.smarteam.modules.file.entity.FtFileUploadLog;

import java.util.List;

public interface FtFileUploadService {


    /**
     *  获取列表数据
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<FtFileUpload> getFtFileUploadList(int pageNo, int pageSize);

    /**
     * 获取总条数
     * @return
     */
    public int getFtFileUploadTotal();

    /**
     * 根据ID 获取数据
     * @param id
     * @return
     */
    public FtFileUpload getFtFileUploadById(String id);

    /**
     * 删除数据
     * @param id
     */
    public void delOne(String id);

    public int update(FtFileUpload FtFileUpload);

    public void save(FtFileUpload ftFileUpload);

    public FtFileUpload get(FtFileUpload FtFileUpload);

    public FtFileUpload get(String id);
}
