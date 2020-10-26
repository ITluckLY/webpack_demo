package com.dc.smarteam.modules.file.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import com.dc.smarteam.modules.file.entity.BizFileTransLogWarn;

import java.util.List;
import java.util.Map;

/**
 * Created by yangyga on 2017/8/3.
 */
@Mapper
public interface BizFileTransLogWarnDao extends CrudDao<BizFileTransLogWarn> {
    public Long findTotal(Map map);

    public Long findDownloadTotal(Map map);

    public Long findUploadTotal(Map map);

    public Long findUploadTotalFlow(Map map);

    public Long findTotalFlow(Map map);

    public Long findDownloadTotalFlow(Map map);

    public List<BizFileTransLogWarn> findListBytime(Map map);

    public Long findTotalSuss(Map map);

    public Long findDownloadTotalSuss(Map map);

    public Long findUploadTotalSuss(Map map);
    //add 20170904
    public Long findUploadFlow(Map map);
    //add 20170904
    public Long findDownloadFlow(Map map);

}
