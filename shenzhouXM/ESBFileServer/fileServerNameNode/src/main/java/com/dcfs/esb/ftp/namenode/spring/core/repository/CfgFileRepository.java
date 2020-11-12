package com.dcfs.esb.ftp.namenode.spring.core.repository;

import com.dcfs.esb.ftp.namenode.spring.core.entity.biz.CfgFile;
import com.dcfs.esb.ftp.spring.core.repository.LongBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by huangzbb on 2016/8/31.
 */
@Repository
public interface CfgFileRepository extends LongBaseRepository<CfgFile> {
    List<CfgFile> findByFilename(String filename);

    List<CfgFile> findByFilenameAndSystemAndNodetype(String filename, String system, String nodetype);
}