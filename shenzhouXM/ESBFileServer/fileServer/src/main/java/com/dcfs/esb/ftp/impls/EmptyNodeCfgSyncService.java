package com.dcfs.esb.ftp.impls;

import com.dcfs.esb.ftp.interfases.NodeCfgSyncFace;
import com.dcfs.esb.ftp.report.CfgSyncReport;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/29.
 */
public class EmptyNodeCfgSyncService implements NodeCfgSyncFace {
    @Override
    public boolean sync(String sysName, String cfgName, CfgSyncReport report) throws IOException {
        throw new NestedRuntimeException("需要另外创建个类，重新实现此方法");
    }
}
