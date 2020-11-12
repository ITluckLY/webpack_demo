package com.dcfs.esc.ftp.comm.chunk.util;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.chunk.*;
import com.dcfs.esc.ftp.comm.dto.*;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushRspDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatRspDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * 只处理dto对象与bytes间的转换，不涉及io操作
 * Created by mocg on 2017/7/20.
 */
public class ChunkBytesUtil {
    private static final Logger log = LoggerFactory.getLogger(ChunkBytesUtil.class);
    //json、xml 转成bytes的编码
    private static final Charset charset = Charset.forName("UTF-8");

    private ChunkBytesUtil() {
    }

    /**
     * @param chunkBytes 不为null
     * @param tClass     要有不带参的构建函数
     * @param <T>        BaseDto的子对象
     * @return
     * @throws FtpException
     */
    public static <T extends BaseDto> T chunkBytesToDto(byte[] chunkBytes, Class<T> tClass) throws FtpException {
        ChunkContentFormat contentFormat = StreamChunk.findContentFormat(chunkBytes);
        if (ChunkContentFormat.DCFS.equals(contentFormat)) {
            return fromChunkBytesByDcfs(chunkBytes, tClass);
        } else if (ChunkContentFormat.JSON.equals(contentFormat)) {
            return fromChunkBytesByJson(chunkBytes, tClass);
        } else if (ChunkContentFormat.XML.equals(contentFormat)) {
            //to-do
        }
        throw new FtpException(FtpErrCode.FAIL);
    }

    //dont del
    private static byte[] dtoToBytes(BaseDto dto, ChunkConfig config) {//NOSONAR
        ChunkContentFormat contentFormat = config.getChunkContentFormat();
        if (ChunkContentFormat.DCFS.equals(contentFormat) || contentFormat == null) {
            return dto.toBytes();
        } else if (ChunkContentFormat.JSON.equals(contentFormat)) {
            return GsonUtil.toJson(dto).getBytes(charset);
        } else if (ChunkContentFormat.XML.equals(contentFormat)) {
            //to-do
        }
        return null;//NOSONAR
    }

    private static BytesEntry dtoToBytesEntry(BaseDto dto, ChunkConfig config) {
        ChunkContentFormat contentFormat = config.getChunkContentFormat();
        if (ChunkContentFormat.DCFS.equals(contentFormat) || contentFormat == null) {
            return dto.toBytesEntry();
        } else if (ChunkContentFormat.JSON.equals(contentFormat)) {
            return new BytesEntry(GsonUtil.toJson(dto).getBytes(charset));
        } else if (ChunkContentFormat.XML.equals(contentFormat)) {
            //to-do
        }
        return null;//NOSONAR
    }

    public static byte[] toChunkBytes(BaseDto dto, ChunkConfig config) {
        CapabilityDebugHelper.markCurrTime("toChunkBytes-begin");
        config.setEnd(config.isEnd() || isEnd(dto));
        StreamChunk chunk = new StreamChunk(dto.getChunkType(), dtoToBytesEntry(dto, config), config);
        byte[] bytes = chunk.toBytes();
        CapabilityDebugHelper.markCurrTime("toChunkBytes-end");
        return bytes;
    }

    /**
     * @param chunkBytes 不为null
     * @param tClass     要有不带参的构建函数
     * @param <T>        BaseDto的子对象
     * @return
     * @throws FtpException
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseDto> T fromChunkBytesByDcfs(byte[] chunkBytes, Class<T> tClass) throws FtpException {
        CapabilityDebugHelper.markCurrTime("fromChunkBytesByDcfs-begin");
        BaseDto target = null;
        StreamChunk chunk = StreamChunk.fromBytes(chunkBytes);
        byte type = chunk.getType();
        ChunkType chunkType = ChunkType.find(type);
        switch (chunkType) {
            case Init:
                target = new InitDto();
                break;
            case DownloadAuthReq:
                target = new FileDownloadAuthReqDto();
                break;
            case DownloadAuthRsp:
                target = new FileDownloadAuthRspDto();
                break;
            case DownloadDataReq:
                target = new FileDownloadDataReqDto();
                break;
            case DownloadDataRsp:
                target = new FileDownloadDataRspDto();
                break;

            case UploadAuthReq:
                target = new FileUploadAuthReqDto();
                break;
            case UploadAuthRsp:
                target = new FileUploadAuthRspDto();
                break;
            case UploadDataReq:
                target = new FileUploadDataReqDto();
                break;
            case UploadDataRsp:
                target = new FileUploadDataRspDto();
                break;

            case NodeListReq:
                target = new NodeListReqDto();
                break;
            case NodeListRsp:
                target = new NodeListRspDto();
                break;

            case FileMsgPushReq:
                target = new FileMsgPushReqDto();
                break;
            case FileMsgPushRsp:
                target = new FileMsgPushRspDto();
                break;
            case StateHeartbeatReq:
                target = new StateHeartbeatReqDto();
                break;
            case StateHeartbeatRsp:
                target = new StateHeartbeatRspDto();
                break;
            case NoAuth://重要-不能去掉
                target = new NoAuthDto();
                break;
            case End://重要-不能去掉
                target = new EndDto();
                break;
            case Exception://重要-不能去掉
                target = new ExceptionDto();
                break;

            default:
                try {
                    target = tClass.newInstance();
                    //chunkType不匹配时
                    if (chunkType != target.getChunkType()) throw new FtpException(FtpErrCode.CHUNK_TYPE_DONT_MACTH);
                } catch (Exception e) {
                    log.error("由class创建目标BaseDto对象出错", e);
                }
                break;
        }
        if (target == null) {
            throw new FtpException(FtpErrCode.DONT_MACTH_DTO_TYPE);
        }
        target.setLastChunk(chunk.isLastChunk()); // 设置最后一片
        target.setEnd(chunk.isEnd()); // 设置最后一片
        try {
            target.fromBytes(chunk.getData());
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.DTO_PARSE_ERROR, target.getNano(), e);
        }
        CapabilityDebugHelper.markCurrTime("fromChunkBytesByDcfs-end");
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseDto> T fromChunkBytesByJson(byte[] chunkBytes, Class<T> tClass) throws FtpException {
        CapabilityDebugHelper.markCurrTime("fromChunkBytesByJson-begin");
        StreamChunk chunk = StreamChunk.fromBytes(chunkBytes);
        ChunkType chunkType = ChunkStreamUtil.findChunkType(chunkBytes);
        byte[] data = chunk.getData();
        BaseDto target;
        try {
            String dataStr = new String(data, charset);
            if (ChunkType.Exception.equals(chunkType)) {
                target = GsonUtil.fromJson(dataStr, ExceptionDto.class);
            } else if (ChunkType.NoAuth.equals(chunkType)) {
                target = GsonUtil.fromJson(dataStr, NoAuthDto.class);
            } else if (ChunkType.End.equals(chunkType)) {
                target = GsonUtil.fromJson(dataStr, EndDto.class);
            } else {
                target = GsonUtil.fromJson(dataStr, tClass);
            }
            //chunkType不匹配时
            if (chunkType != target.getChunkType()) throw new FtpException(FtpErrCode.CHUNK_TYPE_DONT_MACTH);
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.DTO_PARSE_ERROR, e);
        }
        CapabilityDebugHelper.markCurrTime("fromChunkBytesByJson-end");
        return (T) target;
    }

    private static boolean isEnd(BaseDto dto) {
        ChunkType chunkType = dto.getChunkType();
        return ChunkType.End.equals(chunkType)
                || ChunkType.Exception.equals(chunkType)
                || ChunkType.None.equals(chunkType);
    }

}
