package com.dcfs.esc.ftp.datanode.nework.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by mocg on 2017/6/3.
 */
public class IPCheckHandler extends ChannelInboundHandlerAdapter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String host = remoteAddress.getAddress().getHostAddress();
        log.debug("remoteAddress#host:{}:{}", host, remoteAddress.getPort());
        //if ("127.0.0.1".equals(host)) {ctx.close();return;}//NOSONAR
        ctx.fireChannelActive();
    }
}
