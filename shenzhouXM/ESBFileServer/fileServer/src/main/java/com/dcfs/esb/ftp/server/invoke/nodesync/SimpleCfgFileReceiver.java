package com.dcfs.esb.ftp.server.invoke.nodesync;

import java.io.IOException;

/**
 * Created by mocg on 2016/10/10.
 */
public class SimpleCfgFileReceiver extends AbstractCfgFileReceiver {


    @Override
    public boolean receive(String sysName, String cfgFileName, String cfgContent) throws IOException {
        return false;
    }
}
