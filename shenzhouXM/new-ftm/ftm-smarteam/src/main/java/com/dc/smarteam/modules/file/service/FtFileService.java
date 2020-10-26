package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.vo.ZTreeNode;
import com.dc.smarteam.modules.file.entity.FtFile;
import java.util.List;

public interface FtFileService {
    String basePath = "D:\\BaiduYunDownload";

    FtFile get(String id);

    List<FtFile> findList(FtFile ftFile);

    Page<FtFile> findPage(Page<FtFile> page, FtFile ftFile);

    void save(FtFile ftFile);

    void delete(FtFile ftFile);

    List<ZTreeNode> getFileTree() throws Throwable;

    void getContent(String path, Page<FtFile> page);
}
