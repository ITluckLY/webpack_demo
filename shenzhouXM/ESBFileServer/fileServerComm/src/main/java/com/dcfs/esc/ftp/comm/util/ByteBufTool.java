package com.dcfs.esc.ftp.comm.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * Created by cgmo on 2017/5/29.
 */
public class ByteBufTool {

    private ByteBufTool() {
    }

    public static byte[] bytes(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

    public static String string(ByteBuf buf, String charset) {
        return new String(bytes(buf), Charset.forName(charset));
    }

    public static String string(ByteBuf buf) {
        return string(buf, "utf-8");
    }

}
