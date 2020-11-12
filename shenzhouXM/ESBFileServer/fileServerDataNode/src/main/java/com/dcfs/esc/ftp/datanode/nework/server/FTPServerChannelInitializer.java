package com.dcfs.esc.ftp.datanode.nework.server;

import com.dcfs.esc.ftp.comm.chunk.ChunkCfgCons;
import com.dcfs.esc.ftp.datanode.nework.handler.ChunkDispatchHandler;
import com.dcfs.esc.ftp.datanode.nework.handler.IdleEventDealHandler;
import com.dcfs.esc.ftp.datanode.nework.handler.MyLengthFieldBasedFrameDecoder;
import com.dcfs.esc.ftp.datanode.nework.handler.ZeroLengthAndHeartbeatHandler;
import com.dcfs.esc.ftp.datanode.pool.ThreadExecutorFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Created by cgmo on 2017/5/29.
 */
public class FTPServerChannelInitializer extends ChannelInitializer {
    private final SslContext sslCtx;
    private static final EventExecutorGroup eventExecutors = ThreadExecutorFactory.getEventExecutorGroupForChunkDispatch();

    FTPServerChannelInitializer(final SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //p.addLast(new LoggingHandler(LogLevel.INFO))
        //p.addLast(new IPCheckHandler())
        final int readerIdleTimeSeconds = 25; //25秒
        final int writerIdleTimeSeconds = 15; //15秒
        final int maxReaderIdleTimeSeconds = 60 * 2; //2分钟
        p.addLast(new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, 0));
        p.addLast(new IdleEventDealHandler(maxReaderIdleTimeSeconds));
        //默认分片大小最大5M
        p.addLast(new MyLengthFieldBasedFrameDecoder(ChunkCfgCons.MAX_CHUNK_FRAME_LENGTH, 0, ChunkCfgCons.LENGTH_FIELD_LENGTH));
        p.addLast(new ZeroLengthAndHeartbeatHandler());
        p.addLast(eventExecutors, "ChunkDispatchHandler", new ChunkDispatchHandler());
    }

}
