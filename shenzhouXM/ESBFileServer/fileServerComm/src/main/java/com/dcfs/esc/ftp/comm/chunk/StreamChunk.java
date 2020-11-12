package com.dcfs.esc.ftp.comm.chunk;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.util.ByteUtil;

/**
 * Created by cgmo on 2017/6/1.
 */
public class StreamChunk {
    /*固定为4 chunk前面4个字节表示chunk总长度,不包括最开头的4个字节*/
    public static final int LENGTH_FIELD_LENGTH = 4;
    //chunk byte格式
    //chunk
    //chunk head
    /*bytes长度大小 不包括最开头的4个字节 适应io.netty.handler.codec.LengthFieldBasedFrameDecoder*/
    private int total; //4 byte
    /*内容格式标识*/
    private ChunkContentFormat contentFormat;//4 byte
    /*ref ChunkType*/
    private byte type; //1 byte
    /*设值其他boolean字段*/
    //private byte flag = 0; // 1 byte
    private int headLength;//2 byte
    //end fixed chunk head
    private byte[] headData; //长度不固定
    private byte[] data; //长度不固定
    //end chunk

    //上面固定的字节数
    private static final byte FIXED_LEN2 = 8; //contentFormat +type +setFlag +headLength
    private static final byte FIXED_LEN = LENGTH_FIELD_LENGTH + FIXED_LEN2; //total +contentFormat +type +setFlag +headLength
    //index
    private static final int TYPE_INDEX = LENGTH_FIELD_LENGTH + 4; //type 的字节序号
    public static final int FLAG_INDEX = LENGTH_FIELD_LENGTH + 5; //bool flag 的字节序号

    //Flag字段: lastChunk end compress scrt heartbeat 共用一个字节 0-lastChunk 1-end 2-compress 3-scrt 4-heartbeat
    private boolean lastChunk;//最后一片，不一定结束
    private boolean end; //结束(close) 不一定成功
    /*压缩标识*/
    private boolean compress;
    /*加密的标识*/
    private boolean scrt;
    /*心跳标识*/
    private boolean heartbeat = false;
    /*是否有authSeq*/
    private boolean hasAuthSeq;
    //
    private static final int LAST_CHUNK_INDEX = 0;
    private static final int END_INDEX = 1;
    private static final int COMPRESS_INDEX = 2;
    private static final int SCRT_INDEX = 3;
    private static final int HEARTBEAT_INDEX = 4;
    private static final int HAS_AUTH_SEQ_INDEX = 5;

    /*整个chunk的allBytes*/
    private byte[] chunkBytes;
    private boolean maked;
    private BytesEntry bytesEntry;

    private StreamChunk() {
    }

    public StreamChunk(ChunkType chunkType, byte[] data, ChunkConfig config) {
        this(chunkType, null, data, config);
    }

    public StreamChunk(ChunkType chunkType, byte[] headData, byte[] data, ChunkConfig config) {
        this.type = chunkType.getVal();
        this.lastChunk = config.isLastChunk();
        this.end = config.isEnd();
        this.compress = config.isCompress();
        this.scrt = config.isScrt();
        this.headData = headData;
        this.data = data;
        this.contentFormat = config.getChunkContentFormat();
        this.hasAuthSeq = config.isHasAuthSeq();
        this.bytesEntry = null;
    }

    public StreamChunk(ChunkType chunkType, BytesEntry bytesEntry, ChunkConfig config) {
        this.type = chunkType.getVal();
        this.lastChunk = config.isLastChunk();
        this.end = config.isEnd();
        this.compress = config.isCompress();
        this.scrt = config.isScrt();
        this.headData = null;
        this.data = null;
        this.contentFormat = config.getChunkContentFormat();
        this.hasAuthSeq = config.isHasAuthSeq();
        this.bytesEntry = bytesEntry;
    }

    private void checkAndInit() {
        headLength = headData == null ? 0 : headData.length;
        final int bitLen = 16;
        if (headLength > 1 << bitLen) throw new IllegalArgumentException("head.length");

        if (data != null && data.length > Integer.MAX_VALUE - headLength - FIXED_LEN) {
            throw new IllegalArgumentException("data.length");
        }
        if (bytesEntry != null && bytesEntry.getLen() > Integer.MAX_VALUE - headLength - FIXED_LEN) {
            throw new IllegalArgumentException("data.length");
        }

        //不包括最开头的4个字节 适应io.netty.handler.codec.LengthFieldBasedFrameDecoder
        int dataLen = 0;
        if (data != null) dataLen = data.length;
        else if (bytesEntry != null) dataLen = bytesEntry.getLen();
        total = FIXED_LEN2 + headLength + dataLen;
    }

    public byte[] toBytes() {
        if (chunkBytes != null && maked) return chunkBytes;
        checkAndInit();
        //total不包括最开头的4个字节 适应io.netty.handler.codec.LengthFieldBasedFrameDecoder
        chunkBytes = new byte[total + LENGTH_FIELD_LENGTH];
        ByteArrayBuf buf = ByteArrayBuf.wrap2Write(chunkBytes);
        buf.writeInt(total);
        //4 bytes
        writeChunkContentFormat(buf);

        buf.writeByte(type);
        byte flag = 0;
        flag = ByteUtil.setFlag(flag, LAST_CHUNK_INDEX, lastChunk);
        flag = ByteUtil.setFlag(flag, END_INDEX, end);
        flag = ByteUtil.setFlag(flag, COMPRESS_INDEX, compress);
        flag = ByteUtil.setFlag(flag, SCRT_INDEX, scrt);
        flag = ByteUtil.setFlag(flag, HEARTBEAT_INDEX, heartbeat);
        flag = ByteUtil.setFlag(flag, HAS_AUTH_SEQ_INDEX, hasAuthSeq);
        buf.writeByte(flag);

        buf.writeShort(headLength);
        if (headLength > 0) buf.writeBytes(headData);
        if (data != null) buf.writeBytes(data);
        else if (bytesEntry != null && bytesEntry.getLen() > 0) {
            buf.writeBytes(bytesEntry.getData(), bytesEntry.getOffset(), bytesEntry.getLen());
        }
        maked = true;
        return chunkBytes;
    }

    public static StreamChunk fromBytes(byte[] chunkBytes) {
        StreamChunk chunk = new StreamChunk();
        ByteArrayBuf buf = ByteArrayBuf.wrap2Read(chunkBytes);

        chunk.total = buf.readInt();
        //4 bytes
        chunk.contentFormat = chunk.readChunkContentFormat(buf);

        chunk.type = buf.readByte();
        byte flag = buf.readByte();
        chunk.lastChunk = ByteUtil.getFlag(flag, LAST_CHUNK_INDEX);
        chunk.end = ByteUtil.getFlag(flag, END_INDEX);
        chunk.compress = ByteUtil.getFlag(flag, COMPRESS_INDEX);
        chunk.scrt = ByteUtil.getFlag(flag, SCRT_INDEX);
        chunk.heartbeat = ByteUtil.getFlag(flag, HEARTBEAT_INDEX);
        chunk.hasAuthSeq = ByteUtil.getFlag(flag, HAS_AUTH_SEQ_INDEX);

        chunk.headLength = buf.readUnsignedShort();
        if (chunk.headLength > 0) {
            chunk.headData = new byte[chunk.headLength];
            buf.readBytes(chunk.headData);
        }
        //不包括最开头的4个字节 适应io.netty.handler.codec.LengthFieldBasedFrameDecoder
        int datalen = chunk.total - FIXED_LEN2 - chunk.headLength;
        if (datalen > 0) {
            chunk.data = new byte[datalen];
            buf.readBytes(chunk.data);
        }
        return chunk;
    }

    private void writeChunkContentFormat(ByteArrayBuf buf) {
        if (contentFormat == null || ChunkContentFormat.DCFS.equals(contentFormat)) {
            //dcfs标识
            buf.writeByte('d');
            buf.writeByte('c');
            buf.writeByte('f');
            buf.writeByte('s');
        } else if (ChunkContentFormat.JSON.equals(contentFormat)) {
            //json标识
            buf.writeByte('j');
            buf.writeByte('s');
            buf.writeByte('o');
            buf.writeByte('n');
        } else if (ChunkContentFormat.XML.equals(contentFormat)) {
            //xml标识
            buf.writeByte('x');
            buf.writeByte('m');
            buf.writeByte('l');
            buf.writeByte('-');
        } else {
            //未知格式标识
            buf.writeByte('-');
            buf.writeByte('-');
            buf.writeByte('-');
            buf.writeByte('-');
        }
    }

    private ChunkContentFormat readChunkContentFormat(ByteArrayBuf buf) {
        //内容格式
        byte b0 = buf.readByte();
        byte b1 = buf.readByte();
        byte b2 = buf.readByte();
        byte b3 = buf.readByte();

        if (b0 == 'd' && b1 == 'c' && b2 == 'f' && b3 == 's') return ChunkContentFormat.DCFS;//dcfs
        else if (b0 == 'j' && b1 == 's' && b2 == 'o' && b3 == 'n') return ChunkContentFormat.JSON;//json
        else if (b0 == 'x' && b1 == 'm' && b2 == 'l') return ChunkContentFormat.XML;//xml
        return ChunkContentFormat.UNDEFINED;
    }

    /**
     * 可以使重新生成bytes
     */
    public void remake() {
        maked = false;
        chunkBytes = null;
    }

    public static ChunkContentFormat findContentFormat(byte[] chunkBytes) {
        if (chunkBytes == null || chunkBytes.length < FIXED_LEN) return ChunkContentFormat.UNDEFINED;
        //内容格式 4 bytes
        byte b0 = chunkBytes[4];//NOSONAR
        byte b1 = chunkBytes[5];//NOSONAR
        byte b2 = chunkBytes[6];//NOSONAR
        byte b3 = chunkBytes[7];//NOSONAR

        if (b0 == 'd' && b1 == 'c' && b2 == 'f' && b3 == 's') return ChunkContentFormat.DCFS;//dcfs
        else if (b0 == 'j' && b1 == 's' && b2 == 'o' && b3 == 'n') return ChunkContentFormat.JSON;//json
        else if (b0 == 'x' && b1 == 'm' && b2 == 'l') return ChunkContentFormat.XML;//xml
        return ChunkContentFormat.UNDEFINED;
    }

    public static byte findType(byte[] chunkBytes) {
        return (chunkBytes != null && chunkBytes.length > FIXED_LEN) ? chunkBytes[TYPE_INDEX] : 0;
    }

    public static boolean findLastChunk(byte[] chunkBytes) {
        return chunkBytes != null && chunkBytes.length >= FIXED_LEN && ByteUtil.getFlag(chunkBytes[FLAG_INDEX], LAST_CHUNK_INDEX);
    }

    public static boolean findEnd(byte[] chunkBytes) {
        return chunkBytes == null || chunkBytes.length < FIXED_LEN || ByteUtil.getFlag(chunkBytes[FLAG_INDEX], END_INDEX);
    }

    public static boolean findCompress(byte[] chunkBytes) {
        return chunkBytes != null && chunkBytes.length >= FIXED_LEN && ByteUtil.getFlag(chunkBytes[FLAG_INDEX], COMPRESS_INDEX);
    }

    public static boolean findScrt(byte[] chunkBytes) {
        return chunkBytes != null && chunkBytes.length >= FIXED_LEN && ByteUtil.getFlag(chunkBytes[FLAG_INDEX], SCRT_INDEX);
    }

    public static boolean findHasAuthSeq(byte[] chunkBytes) {
        return chunkBytes != null && chunkBytes.length >= FIXED_LEN && ByteUtil.getFlag(chunkBytes[FLAG_INDEX], HAS_AUTH_SEQ_INDEX);
    }

    //getter

    public byte getType() {
        return type;
    }

    public ChunkContentFormat getContentFormat() {
        return contentFormat;
    }

    ////////////getter setter

    public boolean isLastChunk() {
        return lastChunk;
    }

    public void setLastChunk(boolean lastChunk) {
        this.lastChunk = lastChunk;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean isScrt() {
        return scrt;
    }

    public void setScrt(boolean scrt) {
        this.scrt = scrt;
    }

    public byte[] getHeadData() {
        return headData;
    }

    public void setHeadData(byte[] headData) {
        this.headData = headData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public int getHeadLength() {
        return headLength;
    }

}
