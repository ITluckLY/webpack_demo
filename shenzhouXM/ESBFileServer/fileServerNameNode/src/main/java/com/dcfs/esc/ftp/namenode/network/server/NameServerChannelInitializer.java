package com.dcfs.esc.ftp.namenode.network.server;

import com.dcfs.esc.ftp.comm.chunk.ChunkCfgCons;
import com.dcfs.esc.ftp.namenode.network.handler.ChunkDispatchHandler;
import com.dcfs.esc.ftp.namenode.network.handler.ZeroLengthAndHeartbeatHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Created by cgmo on 2017/5/29.
 */
public class NameServerChannelInitializer extends ChannelInitializer {
    private final SslContext sslCtx;
    private static final EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(10);

    NameServerChannelInitializer(final SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //默认分片大小最大5M
        p.addLast(new LengthFieldBasedFrameDecoder(ChunkCfgCons.MAX_CHUNK_FRAME_LENGTH, 0, ChunkCfgCons.LENGTH_FIELD_LENGTH));
        p.addLast(new ZeroLengthAndHeartbeatHandler());
        p.addLast(eventExecutors, "ChunkDispatchHandler", new ChunkDispatchHandler());
    }

}
