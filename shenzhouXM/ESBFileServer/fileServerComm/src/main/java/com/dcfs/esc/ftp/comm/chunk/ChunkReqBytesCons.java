package com.dcfs.esc.ftp.comm.chunk;

/**
 * 特殊功能的chunk请求字节数组 如存活探测 心跳
 * 无返回，根据是否发送成功判断
 * Created by mocg on 2017/7/26.
 */
public class ChunkReqBytesCons {
    /*存活探测*/
    public static final byte[] DETECTION_REQ_BYTES = new byte[]{0, 0, 0, 0};//NOSONAR
    /*心跳*/
    public static final byte[] HEARTBEAT_REQ_BYTES = new byte[]{0, 0, 0, 6, 0, 0, 0, 0, 0, 16};//NOSONAR

    public static final int DETECTION_REQ_BYTES_LENGTH = DETECTION_REQ_BYTES.length;

    public static final int HEARTBEAT_REQ_BYTES_LENGTH = HEARTBEAT_REQ_BYTES.length;

    public static final int HEARTBEAT_FLAG_INDEX = StreamChunk.FLAG_INDEX;

    private ChunkReqBytesCons() {
    }
}
