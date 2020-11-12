package com.dcfs.esc.ftp.datanode.nework.handler;

import com.dcfs.esc.ftp.comm.helper.ChannelHandlerContextHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/8/28.
 */
public class MyLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger log = LoggerFactory.getLogger(MyLengthFieldBasedFrameDecoder.class);

    private final boolean isTrace = log.isTraceEnabled();
    private long nano;
    private long preNanoTime;
    private boolean isFirst = true;

    public MyLengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (isTrace) {
            if (nano == 0) nano = ChannelHandlerContextHelper.getNano(ctx);
            long nanoTime = System.nanoTime();
            long timeNano = nanoTime - preNanoTime;
            preNanoTime = nanoTime;
            ByteBuf byteBuf = (ByteBuf) msg;
            log.trace("nano:{}#{}#timeNano:{}, readlen:{}, nanoTime:{}"
                    , nano, isFirst, timeNano, byteBuf.readableBytes(), nanoTime);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded;
        try {
            decoded = super.decode(ctx, in);
        } catch (TooLongFrameException e) {
            long nano2 = ChannelHandlerContextHelper.getNano(ctx);
            log.error("nano:{}", nano2, e);
            log.error("nano:{}#TooLongFrameException#prettyHexDump:\n{}", nano2, ByteBufUtil.prettyHexDump(in, 0, in.writerIndex()));
            throw e;
        }
        if (isTrace) {
            isFirst = decoded != null;
        }

        return decoded;
    }
}
