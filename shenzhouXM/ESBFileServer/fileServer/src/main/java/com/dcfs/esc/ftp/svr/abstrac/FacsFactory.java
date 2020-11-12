package com.dcfs.esc.ftp.svr.abstrac;

import com.dcfs.esc.ftp.svr.abstrac.factory.IFileMsgPushCliFactory;

/**
 * 工厂的工厂
 * Created by mocg on 2017/6/26.
 */
public class FacsFactory {
    private static FacsFactory ourInstance = new FacsFactory();

    public static FacsFactory getInstance() {
        return ourInstance;
    }

    private FacsFactory() {
    }

    private IFileMsgPushCliFactory fileMsgPushCliFactory;

    public IFileMsgPushCliFactory getFileMsgPushCliFactory() {
        return fileMsgPushCliFactory;
    }

    public void setFileMsgPushCliFactory(IFileMsgPushCliFactory fileMsgPushCliFactory) {
        this.fileMsgPushCliFactory = fileMsgPushCliFactory;
    }
}
