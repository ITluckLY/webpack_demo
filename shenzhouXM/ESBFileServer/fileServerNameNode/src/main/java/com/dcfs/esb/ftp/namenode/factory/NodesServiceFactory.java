package com.dcfs.esb.ftp.namenode.factory;

import com.dcfs.esb.ftp.namenode.service.NodesService;
import com.dcfs.esb.ftp.server.invoke.node.NodesServiceFace;
import com.dcfs.esb.ftp.server.invoke.node.NodesServiceFactoryFace;

/**
 * Created by huangzbb on 2016/10/31.
 */
public class NodesServiceFactory implements NodesServiceFactoryFace {
    public NodesServiceFace getNodesService() {
        return NodesService.getInstance();
    }
}
