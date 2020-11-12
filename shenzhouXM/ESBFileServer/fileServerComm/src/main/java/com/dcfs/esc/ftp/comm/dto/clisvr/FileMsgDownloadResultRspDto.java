package com.dcfs.esc.ftp.comm.dto.clisvr;

import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBusiDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;

/**
 * Created by mocg on 2017/7/17.
 */
public class FileMsgDownloadResultRspDto extends BaseBusiDto implements RspDto {

    @Override
    public ChunkType getChunkType() {
        return ChunkType.FileMsgDownloadResultRsp;
    }

}
