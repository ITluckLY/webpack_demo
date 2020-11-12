package com.dcfs.esc.ftp.comm.buffer;

import com.dcfs.esc.ftp.comm.chunk.BytesEntry;
import com.dcfs.esc.ftp.comm.util.BytesUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by mocg on 2017/6/2.
 */
public class ByteArrayBuf extends AbstractByteBuf {

    private Charset charset = Charset.forName("UTF-8");
    private byte[] bytes;

    public ByteArrayBuf(byte[] bytes) {
        this.bytes = bytes;
    }

    public static ByteArrayBuf wrap2Write(byte[] bytes) {
        ByteArrayBuf buf = new ByteArrayBuf(bytes);
        buf.setIndex(0, 0);
        return buf;
    }

    public static ByteArrayBuf wrap2Read(byte[] bytes) {
        ByteArrayBuf buf = new ByteArrayBuf(bytes);
        buf.setIndex(0, bytes.length);
        return buf;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public int capacity() {
        return bytes == null ? 0 : bytes.length;
    }

    public ByteArrayBuf increaseMaxCapacity(int increase) {
        int newMaxCapacity = Integer.MAX_VALUE - maxCapacity() > increase ? maxCapacity() + increase : Integer.MAX_VALUE;
        super.maxCapacity(newMaxCapacity);
        return this;
    }

    @Override
    protected byte getByte0(int index) {
        return bytes[index];
    }

    @Override
    protected short getShort0(int index) {
        return BytesUtil.bytes2short(bytes, index);
    }

    @Override
    protected int getInt0(int index) {
        return BytesUtil.bytes2Int(bytes, index);
    }

    @Override
    protected long getLong0(int index) {
        return BytesUtil.bytes2long(bytes, index);
    }

    @Override
    public ByteArrayBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        System.arraycopy(bytes, index, dst, dstIndex, length);
        return this;
    }

    @Override
    protected void setByte0(int index, byte value) {
        bytes[index] = value;
    }

    @Override
    protected void setByte0(int index, int value) {
        bytes[index] = (byte) value;
    }

    @Override
    protected void setShort0(int index, int value) {
        BytesUtil.fillShort(bytes, index, value);
    }

    @Override
    protected void setInt0(int index, int value) {
        BytesUtil.fillInt(bytes, index, value);
    }

    @Override
    protected void setLong0(int index, long value) {
        BytesUtil.fillLong(bytes, index, value);
    }

    @Override
    public ByteArrayBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        System.arraycopy(src, srcIndex, bytes, index, length);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return IOUtils.read(in, bytes, index, length);
    }

    @Override
    protected AbstractByteBuf ensureWritable0(int minWritableBytes) {
        byte[] newbytes = new byte[writerIndex() + minWritableBytes];
        System.arraycopy(bytes, 0, newbytes, 0, bytes.length);
        bytes = newbytes;
        return this;
    }

    @Override
    public byte[] getReadableBytes() {
        if (writerIndex == bytes.length) return bytes;
        byte[] data = new byte[writerIndex - readerIndex];
        System.arraycopy(bytes, readerIndex, data, 0, data.length);
        return data;
    }

    public BytesEntry getBytesEntry() {
        return new BytesEntry(bytes, readerIndex, writerIndex - readerIndex);
    }

    public ByteArrayBuf writeString(String str) {
        if (str == null) writeInt(-1);
        else if (str.length() == 0) writeInt(0);
        else {
            byte[] arr = str.getBytes(charset);
            writeInt(arr.length);
            writeBytes(arr);
        }
        return this;
    }

    public String readString() {
        int len = readInt();
        if (len < 0) return null;
        else if (len == 0) return "";
        else {
            byte[] arr = new byte[len];
            readBytes(arr);
            return new String(arr, charset);
        }
    }

    public ByteArrayBuf writeByteString(String str) {
        if (str == null) writeByte(-1);
        else if (str.length() == 0) writeByte(0);
        else {
            byte[] arr = str.getBytes(charset);
            if (arr.length > Byte.MAX_VALUE) throw new IndexOutOfBoundsException("writeByteString#string.bytes.length");
            writeByte(arr.length);
            writeBytes(arr);
        }
        return this;
    }

    public String readByteString() {
        int len = readByte();
        if (len < 0) return null;
        else if (len == 0) return "";
        else {
            byte[] arr = new byte[len];
            readBytes(arr);
            return new String(arr, charset);
        }
    }

    public ByteArrayBuf writeShortString(String str) {
        if (str == null) writeShort(-1);
        else if (str.length() == 0) writeShort(0);
        else {
            byte[] arr = str.getBytes(charset);
            if (arr.length > Short.MAX_VALUE) throw new IndexOutOfBoundsException("writeShortString#string.bytes.length");
            writeShort(arr.length);
            writeBytes(arr);
        }
        return this;
    }

    public String readShortString() {
        int len = readShort();
        if (len < 0) return null;
        else if (len == 0) return "";
        else {
            byte[] arr = new byte[len];
            readBytes(arr);
            return new String(arr, charset);
        }
    }

    public ByteArrayBuf writeByteArr(byte[] arr) {
        if (arr == null) writeInt(-1);
        else if (arr.length == 0) writeInt(0);
        else {
            writeInt(arr.length);
            writeBytes(arr);
        }
        return this;
    }

    public byte[] readByteArr() {
        int len = readInt();
        if (len < 0) return null;//NOSONAR
        else if (len == 0) return new byte[0];
        else {
            byte[] arr = new byte[len];
            readBytes(arr);
            return arr;
        }
    }

    public ByteArrayBuf writeShortByteArr(byte[] arr) {
        if (arr == null) writeShort(-1);
        else if (arr.length == 0) writeInt(0);
        else {
            if (arr.length > Short.MAX_VALUE) throw new IndexOutOfBoundsException("writeShortByteArr#bytes.length");
            writeInt(arr.length);
            writeBytes(arr);
        }
        return this;
    }

    public byte[] readShortByteArr() {
        int len = readShort();
        if (len < 0) return null;//NOSONAR
        else if (len == 0) return new byte[0];
        else {
            byte[] arr = new byte[len];
            readBytes(arr);
            return arr;
        }
    }

    public ByteArrayBuf writeBooleanObj(Boolean val) {
        if (val == null) writeByte(-1);
        else writeByte(val ? 1 : 0);
        return this;
    }

    public Boolean readBooleanObj() {
        byte b = readByte();
        if (b < 0) return null;//NOSONAR
        return b > 0;
    }

    public ByteArrayBuf writeShortObj(Short val) {
        writeByte(val == null ? -1 : 1);
        if (val != null) {
            writeShort(val);
        }
        return this;
    }

    public Short readShortObj() {
        byte b = readByte();
        if (b < 0) return null;
        return readShort();
    }

    public ByteArrayBuf writeIntObj(Integer val) {
        writeByte(val == null ? -1 : 1);
        if (val != null) {
            writeInt(val);
        }
        return this;
    }

    public Integer readIntObj() {
        byte b = readByte();
        if (b < 0) return null;
        return readInt();
    }

    public ByteArrayBuf writeLongObj(Long val) {
        writeByte(val == null ? -1 : 1);
        if (val != null) {
            writeLong(val);
        }
        return this;
    }

    public Long readLongObj() {
        byte b = readByte();
        if (b < 0) return null;
        return readLong();
    }

}
