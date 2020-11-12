package com.dcfs.esb.ftp.interfases;

import com.dcfs.esb.ftp.report.CfgSyncReport;

import java.io.IOException;

/**
 * 需要单粒模式
 * Created by mocg on 2016/7/29.
 */
public interface NodeCfgSyncFace {
    boolean sync(String sysName, String cfgName, CfgSyncReport report) throws IOException;
}
