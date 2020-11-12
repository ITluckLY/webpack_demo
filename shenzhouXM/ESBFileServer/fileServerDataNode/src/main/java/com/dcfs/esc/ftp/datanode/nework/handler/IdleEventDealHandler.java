package com.dcfs.esc.ftp.datanode.nework.handler;

import com.dcfs.esc.ftp.comm.chunk.ChunkReqBytesCons;
import com.dcfs.esc.ftp.comm.constant.UnitCons;
import com.dcfs.esc.ftp.comm.helper.ChannelHandlerContextHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/8/17.
 */
public class IdleEventDealHandler extends ChannelDuplexHandler {
    private static final Logger log = LoggerFactory.getLogger(IdleEventDealHandler.class);

    //发送心跳最小间隔 10s
    private static final long MIN_SEND_PERIOD = 10000L;
    /*两次请求间最大时间间隔*/
    private final long maxReaderIdleTime;
    private long lastReadTime;
    private long lastSendTime;

    public IdleEventDealHandler(int maxReaderIdleTimeSeconds) {
        this.maxReaderIdleTime = maxReaderIdleTimeSeconds * UnitCons.ONE_SECONDS_MILLIS;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        lastReadTime = System.currentTimeMillis();
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            long nano = ChannelHandlerContextHelper.getNano(ctx);
            IdleState idleState = idleStateEvent.state();
            if (IdleState.WRITER_IDLE.equals(idleState)) {
                log.trace("nano:{}#WRITER_IDLE", nano);
                sendHeartbeat(ctx);
            } else if (IdleState.READER_IDLE.equals(idleState)) {
                log.trace("nano:{}#READER_IDLE", nano);
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - lastReadTime < maxReaderIdleTime) {
                    sendHeartbeat(ctx);
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("nano:{}#closeByREADER_IDLE##currentTimeMillis:{}, lastReadTime:{},maxReaderIdleTime:{}"
                                , nano, currentTimeMillis, lastReadTime, maxReaderIdleTime);
                    }
                    ctx.close();
                }
            } else if (IdleState.ALL_IDLE.equals(idleState)) {
                log.trace("nano:{}#ALL_IDLE", nano);
            }
        } else super.userEventTriggered(ctx, evt);
    }

    private void sendHeartbeat(ChannelHandlerContext ctx) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastSendTime > MIN_SEND_PERIOD) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(ChunkReqBytesCons.HEARTBEAT_REQ_BYTES);
            ctx.writeAndFlush(byteBuf);
            lastSendTime = currentTimeMillis;
        }
    }
}
