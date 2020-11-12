package com.dcfs.esc.ftp.datanode.nework.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/6/2.
 */
public abstract class MySimpleChannelInboundHandler<I> extends SimpleChannelInboundHandler<I> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
}
