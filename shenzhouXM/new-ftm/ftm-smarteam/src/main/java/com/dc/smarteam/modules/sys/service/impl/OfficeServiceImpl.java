package com.dc.smarteam.modules.sys.service.impl;

import com.dc.smarteam.common.service.TreeService;
import com.dc.smarteam.modules.sys.dao.OfficeDao;
import com.dc.smarteam.modules.sys.entity.Office;
import com.dc.smarteam.modules.sys.service.OfficeService;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("OfficeServiceImpl")
@Transactional
public class OfficeServiceImpl extends TreeService<OfficeDao, Office> implements OfficeService {

    public List<Office> findAll() {
        return UserUtils.getOfficeList();
    }

    public List<Office> findList(Boolean isAll) {
        if (isAll != null && isAll) {
            return UserUtils.getOfficeAllList();
        } else {
            return UserUtils.getOfficeList();
        }
    }

    @Transactional(readOnly = true)
    public List<Office> findList(Office office) {
        office.setParentIds(office.getParentIds() + "%");
        return dao.findByParentIdsLike(office);
    }

    @Transactional(readOnly = false)
    public void save(Office office) {
        super.save(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
    }

    @Transactional(readOnly = false)
    public void delete(Office office) {
        super.delete(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
    }
}
