package com.dc.smarteam.modules.servicenode.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import java.util.List;

public interface FtServiceNodeService {

    FtServiceNode get(String id);

    List<FtServiceNode> findList(FtServiceNode ftServiceNode);

    Page<FtServiceNode> findPage(Page<FtServiceNode> page, FtServiceNode ftServiceNode);

    void save(FtServiceNode ftServiceNode);

    void delete(FtServiceNode ftServiceNode);

}
