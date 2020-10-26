package com.dc.smarteam.modules.sys.service;

import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.modules.sys.dao.OptTagDao;
import com.dc.smarteam.modules.sys.entity.OptTag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by xuchuang on 2018/6/26.
 */

@Service
public class OptTagService extends CrudService<OptTagDao,OptTag> {

    @Transactional
    public int add(OptTag optTag){
        List<OptTag> list = dao.findList(optTag);
        if(list!=null&&!list.isEmpty()){
            return -1;
        }
        optTag.setId(IdGen.uuid());
        optTag.setCreateTime(new Date());
        return dao.insert(optTag);
    }

    @Transactional
    public int update(OptTag optTag){
        return dao.update(optTag);
    }

}
