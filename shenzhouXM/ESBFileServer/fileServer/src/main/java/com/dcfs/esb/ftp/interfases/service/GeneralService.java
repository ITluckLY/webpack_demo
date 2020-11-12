package com.dcfs.esb.ftp.interfases.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.interfases.context.CachedContext;

import java.util.Map;

/**
 * 所有服务的超级接口
 *
 * @author zhuliang
 */
public interface GeneralService {

    void invoke(CachedContext context, FileMsgBean bean) throws FtpException;

    boolean start(Map<String, String> params);

    boolean stop();

    boolean isStarted();
}
