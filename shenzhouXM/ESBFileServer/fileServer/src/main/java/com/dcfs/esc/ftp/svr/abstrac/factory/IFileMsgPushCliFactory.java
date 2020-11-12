package com.dcfs.esc.ftp.svr.abstrac.factory;

import com.dcfs.esc.ftp.svr.abstrac.model.IFileMsgPushCli;

/**
 * Created by mocg on 2017/6/26.
 */
public interface IFileMsgPushCliFactory {

    IFileMsgPushCli getFileMsgPushCli();
}
