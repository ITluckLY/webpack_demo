package com.dcfs.esc.ftp.datanode.svrimpl;

import com.dcfs.esc.ftp.svr.abstrac.factory.IFileMsgPushCliFactory;
import com.dcfs.esc.ftp.svr.abstrac.model.IFileMsgPushCli;

/**
 * Created by mocg on 2017/6/26.
 */
public class FileMsgPushCliFactory implements IFileMsgPushCliFactory {
    private static FileMsgPushCliFactory ourInstance = new FileMsgPushCliFactory();

    public static FileMsgPushCliFactory getInstance() {
        return ourInstance;
    }

    private FileMsgPushCliFactory() {
    }

    @Override
    public IFileMsgPushCli getFileMsgPushCli() {
        return new FileMsgPushCliImpl();
    }
}
