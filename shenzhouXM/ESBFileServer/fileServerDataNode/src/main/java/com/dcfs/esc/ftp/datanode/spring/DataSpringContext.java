package com.dcfs.esc.ftp.datanode.spring;

import com.dcfs.esb.ftp.spring.SpringContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by mocg on 2016/7/26.
 */
public class DataSpringContext {
    private static DataSpringContext ourInstance = new DataSpringContext();
    private AbstractApplicationContext context = SpringContext.getInstance().getContext();
    private boolean inited = false;

    private DataSpringContext() {
    }

    public static DataSpringContext getInstance() {
        return ourInstance;
    }

    private synchronized void init0() {
        if (inited) return;
        inited = true;
    }

    public void init() {
        if (!inited) init0();
    }

}
