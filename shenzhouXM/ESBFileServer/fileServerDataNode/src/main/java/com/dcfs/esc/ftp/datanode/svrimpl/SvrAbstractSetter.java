package com.dcfs.esc.ftp.datanode.svrimpl;

import com.dcfs.esc.ftp.svr.abstrac.FacsFactory;

/**
 * Created by mocg on 2017/6/26.
 */
public class SvrAbstractSetter {
    private static SvrAbstractSetter ourInstance = new SvrAbstractSetter();

    public static SvrAbstractSetter getInstance() {
        return ourInstance;
    }

    private SvrAbstractSetter() {
    }

    public void setIt() {
        FacsFactory.getInstance().setFileMsgPushCliFactory(FileMsgPushCliFactory.getInstance());
    }
}
