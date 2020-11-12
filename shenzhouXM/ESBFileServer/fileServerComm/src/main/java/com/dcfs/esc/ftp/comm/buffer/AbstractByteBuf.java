package com.dcfs.esc.ftp.comm.buffer;

import java.io.IOException;
import java.io.InputStream;

import static io.netty.util.internal.MathUtil.isOutOfBounds;

/**
 * ref io.netty.buffer.AbstractByteBuf
 * Created by mocg on 2017/6/2.
 */
public abstract class AbstractByteBuf {

    //类型的字节数
    private static final int SHORT_BYTE_LEN = 2;
    private static final int INT_BYTE_LEN = 4;
    private static final int LONG_BYTE_LEN = 8;

    int readerIndex;
    int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    private int maxCapacity = 2 * 1024 * 1024; //2M 不一定初始化时立马分配空间 //NOSONAR

    public abstract int capacity();

    public int maxCapacity() {
        return maxCapacity;
    }

    protected final AbstractByteBuf maxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public int readerIndex() {
        return readerIndex;
    }

    public AbstractByteBuf readerIndex(int readerIndex) {
        if (readerIndex < 0 || readerIndex > writerIndex) {
            throw new IndexOutOfBoundsException(String.format(
                    "readerIndex: %d (expected: 0 <= readerIndex <= writerIndex(%d))", readerIndex, writerIndex));
        }
        this.readerIndex = readerIndex;
        return this;
    }

    public int writerIndex() {
        return writerIndex;
    }

    public AbstractByteBuf writerIndex(int writerIndex) {
        if (writerIndex < readerIndex || writerIndex > capacity()) {
            throw new IndexOutOfBoundsException(String.format(
                    "writerIndex: %d (expected: readerIndex(%d) <= writerIndex <= capacity(%d))",
                    writerIndex, readerIndex, capacity()));
        }
        this.writerIndex = writerIndex;
        return this;
    }

    public AbstractByteBuf setIndex(int readerIndex, int writerIndex) {
        if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity()) {
            throw new IndexOutOfBoundsException(String.format(
                    "readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))",
                    readerIndex, writerIndex, capacity()));
        }
        setIndex0(readerIndex, writerIndex);
        return this;
    }

    public boolean isReadable() {
        return writerIndex > readerIndex;
    }


    public boolean isReadable(int numBytes) {
        return writerIndex - readerIndex >= numBytes;
    }


    public boolean isWritable() {
        return capacity() > writerIndex;
    }


    public boolean isWritable(int numBytes) {
        return capacity() - writerIndex >= numBytes;
    }


    public int readableBytes() {
        return writerIndex - readerIndex;
    }


    public int writableBytes() {
        return capacity() - writerIndex;
    }


    public int maxWritableBytes() {
        return maxCapacity() - writerIndex;
    }

    public AbstractByteBuf markReaderIndex() {
        markedReaderIndex = readerIndex;
        return this;
    }


    public AbstractByteBuf resetReaderIndex() {
        readerIndex(markedReaderIndex);
        return this;
    }


    public AbstractByteBuf markWriterIndex() {
        markedWriterIndex = writerIndex;
        return this;
    }


    public AbstractByteBuf resetWriterIndex() {
        writerIndex = markedWriterIndex;
        return this;
    }

    public byte getByte(int index) {
        checkIndex(index);
        return getByte0(index);
    }

    protected abstract byte getByte0(int index);

    public boolean getBoolean(int index) {
        return getByte(index) != 0;
    }


    public short getUnsignedByte(int index) {
        return (short) (getByte(index) & 0xFF);
    }


    public short getShort(int index) {
        checkIndex(index, SHORT_BYTE_LEN);
        return getShort0(index);
    }

    protected abstract short getShort0(int index);


    public int getUnsignedShort(int index) {
        return getShort(index) & 0xFFFF;
    }

    public int getInt(int index) {
        checkIndex(index, INT_BYTE_LEN);
        return getInt0(index);
    }

    protected abstract int getInt0(int index);


    public long getUnsignedInt(int index) {
        return getInt(index) & 0xFFFFFFFFL;
    }

    public long getLong(int index) {
        checkIndex(index, LONG_BYTE_LEN);
        return getLong0(index);
    }

    protected abstract long getLong0(int index);


    public char getChar(int index) {
        return (char) getShort(index);
    }

    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }


    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    public AbstractByteBuf getBytes(int index, byte[] dst) {
        getBytes(index, dst, 0, dst.length);
        return this;
    }

    public abstract AbstractByteBuf getBytes(int index, byte[] dst, int dstIndex, int length);

    public AbstractByteBuf setByte(int index, byte value) {
        checkIndex(index);
        setByte0(index, value);
        return this;
    }

    protected abstract void setByte0(int index, byte value);

    public AbstractByteBuf setByte(int index, int value) {
        checkIndex(index);
        setByte0(index, value);
        return this;
    }

    protected abstract void setByte0(int index, int value);

    public AbstractByteBuf setBoolean(int index, boolean value) {
        setByte(index, value ? 1 : 0);
        return this;
    }

    public AbstractByteBuf setShort(int index, int value) {
        checkIndex(index, SHORT_BYTE_LEN);
        setShort0(index, value);
        return this;
    }

    protected abstract void setShort0(int index, int value);


    public AbstractByteBuf setChar(int index, int value) {
        setShort(index, value);
        return this;
    }

    public AbstractByteBuf setInt(int index, int value) {
        checkIndex(index, INT_BYTE_LEN);
        setInt0(index, value);
        return this;
    }

    protected abstract void setInt0(int index, int value);


    public AbstractByteBuf setFloat(int index, float value) {
        setInt(index, Float.floatToRawIntBits(value));
        return this;
    }


    public AbstractByteBuf setLong(int index, long value) {
        checkIndex(index, LONG_BYTE_LEN);
        setLong0(index, value);
        return this;
    }

    protected abstract void setLong0(int index, long value);


    public AbstractByteBuf setDouble(int index, double value) {
        setLong(index, Double.doubleToRawLongBits(value));
        return this;
    }


    public AbstractByteBuf setBytes(int index, byte[] src) {
        setBytes(index, src, 0, src.length);
        return this;
    }

    public abstract AbstractByteBuf setBytes(int index, byte[] src, int srcIndex, int length);


    public byte readByte() {
        checkReadableBytes0(1);
        int i = readerIndex;
        byte b = getByte0(i);
        readerIndex = i + 1;
        return b;
    }


    public boolean readBoolean() {
        return readByte() != 0;
    }


    public short readUnsignedByte() {
        return (short) (readByte() & 0xFF);
    }


    public short readShort() {
        checkReadableBytes0(SHORT_BYTE_LEN);
        short v = getShort0(readerIndex);
        readerIndex += SHORT_BYTE_LEN;
        return v;
    }

    public int readUnsignedShort() {
        return readShort() & 0xFFFF;
    }


    public int readInt() {
        checkReadableBytes0(INT_BYTE_LEN);
        int v = getInt0(readerIndex);
        readerIndex += INT_BYTE_LEN;
        return v;
    }

    public long readUnsignedInt() {
        return readInt() & 0xFFFFFFFFL;
    }

    public long readLong() {
        checkReadableBytes0(LONG_BYTE_LEN);
        long v = getLong0(readerIndex);
        readerIndex += LONG_BYTE_LEN;
        return v;
    }

    public char readChar() {
        return (char) readShort();
    }


    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }


    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }


    public AbstractByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        checkReadableBytes(length);
        getBytes(readerIndex, dst, dstIndex, length);
        readerIndex += length;
        return this;
    }


    public AbstractByteBuf readBytes(byte[] dst) {
        readBytes(dst, 0, dst.length);
        return this;
    }

    public AbstractByteBuf skipBytes(int length) {
        checkReadableBytes(length);
        readerIndex += length;
        return this;
    }

    public AbstractByteBuf writeBoolean(boolean value) {
        writeByte(value ? 1 : 0);
        return this;
    }

    public AbstractByteBuf writeByte(byte value) {
        ensureAccessible();
        ensureWritableForBasicType(1);
        setByte0(writerIndex++, value);
        return this;
    }

    public AbstractByteBuf writeByte(int value) {
        ensureAccessible();
        ensureWritableForBasicType(1);
        setByte0(writerIndex++, value);
        return this;
    }


    public AbstractByteBuf writeShort(int value) {
        ensureAccessible();
        ensureWritableForBasicType(SHORT_BYTE_LEN);
        setShort0(writerIndex, value);
        writerIndex += SHORT_BYTE_LEN;
        return this;
    }

    public AbstractByteBuf writeInt(int value) {
        ensureAccessible();
        ensureWritableForBasicType(INT_BYTE_LEN);
        setInt0(writerIndex, value);
        writerIndex += INT_BYTE_LEN;
        return this;
    }

    public AbstractByteBuf writeLong(long value) {
        ensureAccessible();
        ensureWritableForBasicType(LONG_BYTE_LEN);
        setLong0(writerIndex, value);
        writerIndex += LONG_BYTE_LEN;
        return this;
    }

    public AbstractByteBuf writeChar(int value) {
        writeShort(value);
        return this;
    }


    public AbstractByteBuf writeFloat(float value) {
        writeInt(Float.floatToRawIntBits(value));
        return this;
    }


    public AbstractByteBuf writeDouble(double value) {
        writeLong(Double.doubleToRawLongBits(value));
        return this;
    }


    public AbstractByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        ensureAccessible();
        ensureWritable(length);
        setBytes(writerIndex, src, srcIndex, length);
        writerIndex += length;
        return this;
    }


    public AbstractByteBuf writeBytes(byte[] src) {
        writeBytes(src, 0, src.length);
        return this;
    }

    public AbstractByteBuf clear() {
        readerIndex = writerIndex = 0;
        return this;
    }

    public int writeBytes(InputStream in, int length) throws IOException {
        ensureAccessible();
        ensureWritable(length);
        int writtenBytes = setBytes(writerIndex, in, length);
        if (writtenBytes > 0) {
            writerIndex += writtenBytes;
        }
        return writtenBytes;
    }

    public abstract int setBytes(int index, InputStream in, int length) throws IOException;

    protected final void checkIndex(int index) {
        checkIndex(index, 1);
    }

    protected final void checkIndex(int index, int fieldLength) {
        ensureAccessible();
        checkIndex0(index, fieldLength);
    }

    final void checkIndex0(int index, int fieldLength) {
        if (isOutOfBounds(index, fieldLength, capacity())) {
            throw new IndexOutOfBoundsException(String.format(
                    "index: %d, length: %d (expected: range(0, %d))", index, fieldLength, capacity()));
        }
    }

    protected final void checkSrcIndex(int index, int length, int srcIndex, int srcCapacity) {
        checkIndex(index, length);
        if (isOutOfBounds(srcIndex, length, srcCapacity)) {
            throw new IndexOutOfBoundsException(String.format(
                    "srcIndex: %d, length: %d (expected: range(0, %d))", srcIndex, length, srcCapacity));
        }
    }

    protected final void checkDstIndex(int index, int length, int dstIndex, int dstCapacity) {
        checkIndex(index, length);
        if (isOutOfBounds(dstIndex, length, dstCapacity)) {
            throw new IndexOutOfBoundsException(String.format(
                    "dstIndex: %d, length: %d (expected: range(0, %d))", dstIndex, length, dstCapacity));
        }
    }

    /**
     * Throws an {@link IndexOutOfBoundsException} if the current
     * {@linkplain #readableBytes() readable bytes} of this buffer is less
     * than the specified value.
     */
    protected final void checkReadableBytes(int minimumReadableBytes) {
        if (minimumReadableBytes < 0) {
            throw new IllegalArgumentException("minimumReadableBytes: " + minimumReadableBytes + " (expected: >= 0)");
        }
        checkReadableBytes0(minimumReadableBytes);
    }

    protected final void checkNewCapacity(int newCapacity) {
        ensureAccessible();
        if (newCapacity < 0 || newCapacity > maxCapacity()) {
            throw new IllegalArgumentException("newCapacity: " + newCapacity + " (expected: 0-" + maxCapacity() + ')');
        }
    }

    private void checkReadableBytes0(int minimumReadableBytes) {
        ensureAccessible();
        if (readerIndex > writerIndex - minimumReadableBytes) {
            throw new IndexOutOfBoundsException(String.format(
                    "readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s",
                    readerIndex, minimumReadableBytes, writerIndex, this));
        }
    }

    /**
     * Should be called by every method that tries to access the buffers content to check
     * if the buffer was released before.
     */
    protected final void ensureAccessible() {

    }


    final void setIndex0(int readerIndex, int writerIndex) {
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
    }

    public AbstractByteBuf ensureWritable(int minWritableBytes) {
        if (minWritableBytes < 0) {
            throw new IllegalArgumentException(String.format(
                    "minWritableBytes: %d (expected: >= 0)", minWritableBytes));
        }
        if (minWritableBytes <= writableBytes()) {
            return this;
        }

        if (minWritableBytes > maxCapacity - writerIndex) {
            throw new IndexOutOfBoundsException(String.format(
                    "writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s",
                    writerIndex, minWritableBytes, maxCapacity, this));
        }

        ensureWritable0(minWritableBytes);

        return this;
    }

    protected abstract AbstractByteBuf ensureWritable0(int minWritableBytes);

    /**
     * for 字节数固定的基本类型，不包括String
     *
     * @param minWritableBytes
     */
    protected void ensureWritableForBasicType(int minWritableBytes) {
        if (minWritableBytes <= writableBytes()) {
            return;
        }
        if (minWritableBytes > maxCapacity - writerIndex) {
            throw new IndexOutOfBoundsException(String.format(
                    "writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s",
                    writerIndex, minWritableBytes, maxCapacity, this));
        }
        // Normalize the current capacity to the power of 2.
        //int newCapacity = alloc().calculateNewCapacity(writerIndex + minWritableBytes, maxCapacity);//NOSONAR

        // Adjust to the new capacity.
        //capacity(newCapacity);//NOSONAR

        ensureWritable0(minWritableBytes);
    }

    public abstract byte[] getReadableBytes();
}
