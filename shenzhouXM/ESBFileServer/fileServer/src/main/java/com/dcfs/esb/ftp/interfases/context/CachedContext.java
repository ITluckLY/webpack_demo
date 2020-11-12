package com.dcfs.esb.ftp.interfases.context;

/**
 *  缓存上下文
 *
 */
public interface CachedContext extends Cloneable {
    boolean containsKey(String key);

    Object get(String key);

    Object put(String key, Object value);

    CachedContext copy();

    Object remove(String key);

    void clear();

    ContextBean getCxtBean();

    boolean isCanntInvokeNextFlow();

    void setCanntInvokeNextFlow(boolean canntInvokeNextFlow);
}
