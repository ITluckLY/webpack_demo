package com.dcfs.esb.ftp.server.socket.dispatcher;

/**
 * Created by mocg on 2016/12/26.
 */
public interface BaseDispatcher {
    void run();

    void initContext();

    void preprocess();

    void process(int step);

    void postprocess();
}
