package com.dc.smarteam.modules.sys.service;

import com.dc.smarteam.modules.sys.entity.Office;
import java.util.List;

public interface OfficeService {

    public Office get(String id);

    public List<Office> findAll();

    public List<Office> findList(Boolean isAll);

    public List<Office> findList(Office office);

    public void save(Office office);

    public void delete(Office office);

}