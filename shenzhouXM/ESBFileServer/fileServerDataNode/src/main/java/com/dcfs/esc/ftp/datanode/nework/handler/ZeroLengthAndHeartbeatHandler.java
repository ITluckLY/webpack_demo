package com.dcfs.esc.ftp.datanode.nework.handler;

import com.dcfs.esc.ftp.comm.chunk.util.HeartbeatUtil;
import com.dcfs.esc.ftp.datanode.helper.ChannelHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/7/26.
 */
public class ZeroLengthAndHeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ZeroLengthAndHeartbeatHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//NOSONAR
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            if (HeartbeatUtil.isDetectionReq(byteBuf)) {//探测
                if (log.isTraceEnabled()) {
                    String remoteAddress = ChannelHelper.getRemoteAddress(ctx);
                    log.trace("ZeroLength#remoteAddress:{}", remoteAddress);
                }
                ReferenceCountUtil.release(msg);
                ctx.close();
                return;
            } else if (HeartbeatUtil.isHeartbeatReq(byteBuf)) {//心跳
                if (log.isTraceEnabled()) {
                    String remoteAddress = ChannelHelper.getRemoteAddress(ctx);
                    log.trace("Heartbeat#remoteAddress:{}", remoteAddress);
                }
                ReferenceCountUtil.release(msg);
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }
}
