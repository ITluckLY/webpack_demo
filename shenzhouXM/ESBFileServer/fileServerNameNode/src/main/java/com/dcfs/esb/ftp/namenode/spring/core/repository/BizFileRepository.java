package com.dcfs.esb.ftp.namenode.spring.core.repository;

import com.dcfs.esb.ftp.namenode.spring.core.entity.biz.BizFile;
import com.dcfs.esb.ftp.spring.core.repository.LongBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by huangzbb on 2016/10/19.
 */
@Repository
public interface BizFileRepository extends LongBaseRepository<BizFile> {
    List<BizFile> findByRequestFilePath(String clientFilePath);
}
