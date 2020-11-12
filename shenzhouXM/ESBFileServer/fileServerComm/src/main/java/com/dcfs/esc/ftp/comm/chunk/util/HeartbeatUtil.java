package com.dcfs.esc.ftp.comm.chunk.util;

import com.dcfs.esc.ftp.comm.chunk.ChunkReqBytesCons;
import io.netty.buffer.ByteBuf;

/**
 * Created by mocg on 2017/8/14.
 */
public class HeartbeatUtil {
    private HeartbeatUtil() {
    }

    /**
     * 是否心跳请求
     *
     * @param byteBuf
     * @return
     */
    public static boolean isHeartbeatReq(ByteBuf byteBuf) {
        int readableBytes = byteBuf.readableBytes();
        if (readableBytes == ChunkReqBytesCons.HEARTBEAT_REQ_BYTES_LENGTH) {
            //data长度为6
            if (byteBuf.getByte(0) == 0 //NOSONAR
                    && byteBuf.getByte(1) == 0
                    && byteBuf.getByte(2) == 0 //NOSONAR
                    && byteBuf.getByte(3) == 6 //NOSONAR
                    ) {
                byte flagByte = byteBuf.getByte(ChunkReqBytesCons.HEARTBEAT_FLAG_INDEX);
                //心跳标识
                if ((flagByte & 0x10) > 0) return true;
            }
        }
        return false;
    }

    /**
     * 是否心跳请求
     *
     * @param bytes
     * @return
     */
    public static boolean isHeartbeatReq(byte[] bytes) {
        if (bytes.length == ChunkReqBytesCons.HEARTBEAT_REQ_BYTES_LENGTH) {
            //data长度为6
            if (bytes[0] == 0 //NOSONAR
                    && bytes[1] == 0
                    && bytes[2] == 0 //NOSONAR
                    && bytes[3] == 6 //NOSONAR
                    ) {
                byte flagByte = bytes[ChunkReqBytesCons.HEARTBEAT_FLAG_INDEX];
                //心跳标识
                if ((flagByte & 0x10) > 0) return true;
            }
        }
        return false;
    }

    /**
     * 是否探测请求
     *
     * @param byteBuf
     * @return
     */
    public static boolean isDetectionReq(ByteBuf byteBuf) {
        int readableBytes = byteBuf.readableBytes();
        if (readableBytes == ChunkReqBytesCons.DETECTION_REQ_BYTES_LENGTH) {
            //data长度为0
            if (byteBuf.getByte(0) == 0 //NOSONAR
                    && byteBuf.getByte(1) == 0
                    && byteBuf.getByte(2) == 0 //NOSONAR
                    && byteBuf.getByte(3) == 0 //NOSONAR
                    ) {
                return true;
            }
        }
        return false;
    }
}
