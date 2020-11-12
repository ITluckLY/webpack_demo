package com.dcfs.esc.ftp.comm.helper;

import com.dcfs.esb.ftp.utils.NullDefTool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Created by mocg on 2017/8/18.
 */
public class ChannelHandlerContextHelper {
    private ChannelHandlerContextHelper() {
    }

    private static final AttributeKey<Long> nanoAttributeKey = AttributeKey.valueOf("long.nano");

    public static void setNano(ChannelHandlerContext ctx, long nano) {
        Attribute<Long> attr = ctx.channel().attr(nanoAttributeKey);
        attr.set(nano);
    }

    public static long getNano(ChannelHandlerContext ctx) {
        Attribute<Long> attr = ctx.channel().attr(nanoAttributeKey);
        return NullDefTool.defNull(attr.get(), 0L);
    }
}
