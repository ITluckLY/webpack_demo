package com.dcfs.esc.ftp.namenode.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by cgmo on 2017/5/29.
 */
public class NOOPByteBufHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("==NOOPByteBufHandler=====:" + Thread.currentThread());//NOSONAR
        System.out.println("==NOOPByteBufHandler=====ID:" + Thread.currentThread().getId());//NOSONAR
        System.out.println("==NOOPByteBufHandler=====ThreadName:" + Thread.currentThread().getName());//NOSONAR
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        System.out.println("===NOOPByteBufHandler====:" + threadGroup);//NOSONAR
        System.out.println("===NOOPByteBufHandler====ThreadName:" + (threadGroup == null ? null : threadGroup.getName()));//NOSONAR


        super.channelRead(ctx, msg);
    }
}
