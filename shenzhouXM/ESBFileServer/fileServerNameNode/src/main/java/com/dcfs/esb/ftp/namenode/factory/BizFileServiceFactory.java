package com.dcfs.esb.ftp.namenode.factory;

import com.dcfs.esb.ftp.namenode.service.BizFileService;
import com.dcfs.esb.ftp.server.invoke.bizfile.BizFileServiceFace;
import com.dcfs.esb.ftp.server.invoke.bizfile.BizFileServiceFactoryFace;

/**
 * Created by huangzbb on 2016/10/26.
 */
public class BizFileServiceFactory implements BizFileServiceFactoryFace {

    public BizFileServiceFace getBizFileService() {
        return BizFileService.getInstance();
    }
}
