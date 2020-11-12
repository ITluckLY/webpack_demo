package com.dcfs.esc.ftp.datanode.nework.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

/**
 * Created by cgmo on 2017/5/31.
 */
public class PrintInHandler extends SimpleChannelInboundHandler<String> {
    private int r = nextInt();

    private int nextInt() {
        System.out.println("nextint");//NOSONAR
        return new Random(1000).nextInt();//NOSONAR
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("ctxID:" + ctx.name() + "---" + ctx + " ---" + r);//NOSONAR
        System.out.println(msg);//NOSONAR
        ctx.fireChannelRead(msg);
    }
}
