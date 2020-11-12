package com.dcfs.esb.ftp.server.system;

/**
 * Created by mocg on 2017/6/26.
 */
public abstract class BaseProtocol implements IProtocol {
    protected FileRouteArgs routeArgs;

    @Override
    public void setFileRouteArgs(FileRouteArgs routeArgs) {
        this.routeArgs = routeArgs;
    }
}
