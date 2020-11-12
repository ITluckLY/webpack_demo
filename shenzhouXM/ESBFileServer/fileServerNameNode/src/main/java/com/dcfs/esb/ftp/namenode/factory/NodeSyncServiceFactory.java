package com.dcfs.esb.ftp.namenode.factory;

import com.dcfs.esb.ftp.namenode.service.NodeSyncService;
import com.dcfs.esb.ftp.server.invoke.nodesync.NodeSyncServiceFace;
import com.dcfs.esb.ftp.server.invoke.nodesync.NodeSyncServiceFactoryFace;

/**
 * Created by mocg on 2016/10/26.
 */
public class NodeSyncServiceFactory implements NodeSyncServiceFactoryFace {

    public NodeSyncServiceFace getNodeSyncService() {
        return NodeSyncService.getInstance();
    }
}
