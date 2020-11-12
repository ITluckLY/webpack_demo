package com.dcfs.esc.ftp.comm.util;

/**
 * Created by mocg on 2017/6/2.
 */
public class ByteUtil {

    public static byte setFlag(byte b, int index, boolean bool) {
        if (bool) return (byte) (b | (1 << index & 0xff));
        else return (byte) (b & ~(1 << index));
    }

    public static boolean getFlag(byte b, int index) {
        return (b & (1 << index & 0xff)) != 0;
    }

    private ByteUtil() {
    }
}
