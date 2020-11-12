package com.dcfs.esb.ftp.impls.context;

import com.dcfs.esb.ftp.interfases.context.CachedContext;


public class ContextFactory {

    private ContextFactory() {
    }

    public static CachedContext createContext() {
        return new BaseCachedContext();
    }
}
