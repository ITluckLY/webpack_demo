package com.dcfs.esc.ftp.comm.helper;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esc.ftp.comm.chunk.ChunkConfig;
import com.dcfs.esc.ftp.comm.chunk.ChunkReqBytesCons;
import com.dcfs.esc.ftp.comm.chunk.util.ChunkBytesUtil;
import com.dcfs.esc.ftp.comm.chunk.util.ChunkStreamUtil;
import com.dcfs.esc.ftp.comm.chunk.util.HeartbeatUtil;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ExceptionDto;
import com.dcfs.esc.ftp.comm.dto.HeartbeatDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by mocg on 2017/6/2.
 */
public class DtoStreamChunkHelper {

    private DtoStreamChunkHelper() {
    }

    public static void writeAndFlushDto(OutputStream out, BaseDto dto) throws IOException {
        writeAndFlushDto(out, dto, new ChunkConfig());
    }

    public static void writeAndFlushDto(OutputStream out, BaseDto dto, ChunkConfig config) throws IOException {
        byte[] bytes = toChunkBytes(dto, config);
        out.write(bytes);
        out.flush();
    }

    public static <T extends BaseDto> T readDto(InputStream in, Class<T> tClass) throws IOException, FtpException {
        return chunkBytesToDto(readChunk(in), tClass);
    }

    private static byte[] toChunkBytes(BaseDto dto, ChunkConfig config) {
        return ChunkBytesUtil.toChunkBytes(dto, config);
    }

    /**
     * @param chunkBytes 不为null
     * @param tClass     要有不带参的构建函数
     * @param <T>        BaseDto的子对象
     * @return
     * @throws FtpException
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseDto> T chunkBytesToDto(byte[] chunkBytes, Class<T> tClass) throws FtpException {
        //是否为心跳
        if (HeartbeatUtil.isHeartbeatReq(chunkBytes)) return (T) HeartbeatDto.INSTANCE;
        return ChunkBytesUtil.chunkBytesToDto(chunkBytes, tClass);
    }

    private static byte[] readChunk(InputStream in) throws IOException {
        return ChunkStreamUtil.readChunk(in);
    }

    /**
     * 发送心跳
     *
     * @param socket
     */
    public static void sendHeartbeat(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(ChunkReqBytesCons.HEARTBEAT_REQ_BYTES);
        out.flush();
    }

    ///////////////////ByteBuf

    private static ByteBuf toByteBuf(BaseDto dto, ChunkConfig config) {
        return Unpooled.copiedBuffer(toChunkBytes(dto, config));
    }

    /**
     *   发生上下文
     * @param ctx
     * @param dto
     */
    public static void writeAndFlushDto(ChannelHandlerContext ctx, BaseDto dto) {
        CapabilityDebugHelper.markCurrTime("writeAndFlushDto-ctx-before");
        writeAndFlushDto(ctx, dto, new ChunkConfig());
        CapabilityDebugHelper.markCurrTime("writeAndFlushDto-ctx-after");
    }

    public static void writeAndFlushDto(ChannelHandlerContext ctx, BaseDto dto, ChunkConfig config) {
        if (dto == null) {
            ExceptionDto exceptionDto = new ExceptionDto();
            exceptionDto.setErrCode("9999");
            exceptionDto.setErrMsg("syserr#dto is null");
            dto = exceptionDto;//NOSONAR
        }
        ctx.writeAndFlush(toByteBuf(dto, config));
    }

}
