package com.dcfs.esb.ftp.namenode.spring.core.repository;

import com.dcfs.esb.ftp.namenode.spring.core.entity.monitor.NodeMonitor;
import com.dcfs.esb.ftp.spring.core.repository.LongBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by huangzbb on 2016/8/19.
 */
@Repository
public interface NodeMonitorRepository extends LongBaseRepository<NodeMonitor> {
    List<NodeMonitor> findByNode(String node);
}
