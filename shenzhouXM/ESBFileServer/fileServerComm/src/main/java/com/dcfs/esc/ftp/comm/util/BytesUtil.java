package com.dcfs.esc.ftp.comm.util;

/**
 * Created by cgmo on 2017/6/1.
 */
public class BytesUtil {

    private BytesUtil() {
    }

    public static void fillByte(byte[] bytes, int index, byte b) {
        bytes[index] = b;
    }

    public static void fillByte(byte[] bytes, int fromIndex, int val) {
        bytes[fromIndex] = (byte) (val);
    }

    public static void fillShort(byte[] bytes, int fromIndex, short val) {
        bytes[fromIndex] = (byte) (val >> 8);//NOSONAR
        bytes[fromIndex + 1] = (byte) (val);
    }

    public static void fillShort(byte[] bytes, int fromIndex, int val) {
        fillShort(bytes, fromIndex, (short) val);
    }

    public static void fillInt(byte[] bytes, int fromIndex, int val) {
        bytes[fromIndex] = (byte) (val >> 24);//NOSONAR
        bytes[fromIndex + 1] = (byte) (val >> 16);//NOSONAR
        bytes[fromIndex + 2] = (byte) (val >> 8);//NOSONAR
        bytes[fromIndex + 3] = (byte) (val);//NOSONAR
    }

    public static void fillLong(byte[] bytes, int fromIndex, long val) {
        bytes[fromIndex] = (byte) (val >> 56);//NOSONAR
        bytes[fromIndex + 1] = (byte) (val >> 48);//NOSONAR
        bytes[fromIndex + 2] = (byte) (val >> 40);//NOSONAR
        bytes[fromIndex + 3] = (byte) (val >> 32);//NOSONAR
        bytes[fromIndex + 4] = (byte) (val >> 24);//NOSONAR
        bytes[fromIndex + 5] = (byte) (val >> 16);//NOSONAR
        bytes[fromIndex + 6] = (byte) (val >> 8);//NOSONAR
        bytes[fromIndex + 7] = (byte) (val);//NOSONAR
    }

    public static void fill(byte[] srcBytes, byte[] destBytes, int destPos) {
        if (srcBytes != null) fill(srcBytes, destBytes, destPos, srcBytes.length);
    }

    public static void fill(byte[] srcBytes, byte[] destBytes, int destPos, int length) {
        int len = Math.min(srcBytes.length, length);
        System.arraycopy(srcBytes, 0, destBytes, destPos, len);
    }

    public static short bytes2short(byte[] bytes) {
        return bytes2short(bytes, 0);
    }

    public static short bytes2short(byte[] bytes, int start) {
        return (short) ((bytes[start] & 0xff) << 8 | //NOSONAR
                bytes[start + 1] & 0xff);
    }

    public static int bytes2Int(byte[] bytes) {
        return bytes2Int(bytes, 0);
    }

    public static int bytes2Int(byte[] bytes, int start) {
        return (bytes[start] & 0xff) << 24 | //NOSONAR
                (bytes[start + 1] & 0xff) << 16 | //NOSONAR
                (bytes[start + 2] & 0xff) << 8 | //NOSONAR
                bytes[start + 3] & 0xff; //NOSONAR
    }

    public static int bytes2Int(final byte[] bytes, int start, int len) {
        byte[] target = bytes;
        if (bytes.length < start + 4) { //NOSONAR
            target = new byte[4]; //NOSONAR
            len = Math.min(4, len); //NOSONAR
            System.arraycopy(bytes, start, target, 4 - len, len); //NOSONAR
        }
        return bytes2Int(target, start);
    }

    public static long bytes2long(byte[] bytes) {
        return bytes2long(bytes, 0);
    }

    public static long bytes2long(byte[] bytes, int start) {
        return ((long) bytes[start] & 0xff) << 56 | //NOSONAR
                ((long) bytes[start + 1] & 0xff) << 48 | //NOSONAR
                ((long) bytes[start + 2] & 0xff) << 40 | //NOSONAR
                ((long) bytes[start + 3] & 0xff) << 32 | //NOSONAR
                ((long) bytes[start + 4] & 0xff) << 24 | //NOSONAR
                ((long) bytes[start + 5] & 0xff) << 16 | //NOSONAR
                ((long) bytes[start + 6] & 0xff) << 8 | //NOSONAR
                (long) bytes[start + 7] & 0xff; //NOSONAR
    }

    public static long bytes2long(byte[] bytes, int start, int len) {
        byte[] target = bytes;
        if (bytes.length < start + 8) { //NOSONAR
            target = new byte[8]; //NOSONAR
            len = Math.min(8, len); //NOSONAR
            System.arraycopy(bytes, start, target, 8 - len, len); //NOSONAR
        }
        return bytes2long(target, start);
    }

    public static byte[] short2Bytes(short value) {
        byte[] bytes = new byte[2]; //NOSONAR
        bytes[0] = (byte) (value >> 8); //NOSONAR
        bytes[1] = (byte) (value); //NOSONAR
        return bytes;
    }

    public static byte[] int2Bytes(int value) {
        byte[] bytes = new byte[4]; //NOSONAR
        bytes[0] = (byte) (value >> 24); //NOSONAR
        bytes[1] = (byte) (value >> 16); //NOSONAR
        bytes[2] = (byte) (value >> 8); //NOSONAR
        bytes[3] = (byte) (value); //NOSONAR
        return bytes;
    }

    public static byte[] long2Bytes(long value) {
        byte[] bytes = new byte[8]; //NOSONAR
        bytes[0] = (byte) (value >> 56); //NOSONAR
        bytes[1] = (byte) (value >> 48); //NOSONAR
        bytes[2] = (byte) (value >> 40); //NOSONAR
        bytes[3] = (byte) (value >> 32); //NOSONAR
        bytes[4] = (byte) (value >> 24); //NOSONAR
        bytes[5] = (byte) (value >> 16); //NOSONAR
        bytes[6] = (byte) (value >> 8); //NOSONAR
        bytes[7] = (byte) (value); //NOSONAR
        return bytes;
    }

    public static byte[] bytes(byte[] srcBytes, int srcPos, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(srcBytes, srcPos, bytes, 0, length);
        return bytes;
    }

}
