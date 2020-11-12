package com.dcfs.esc.ftp.namenode.helper;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * Created by mocg on 2017/6/7.
 */
public class ChannelHelper {

    public static String getRemoteAddress(ChannelHandlerContext ctx) {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return remoteAddress.getAddress().getHostAddress();
    }

    /**
     * @param ctx
     * @return ip:port
     */
    public static String getRemoteAddrPort(ChannelHandlerContext ctx) {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort();
    }

    private ChannelHelper() {
    }
}
