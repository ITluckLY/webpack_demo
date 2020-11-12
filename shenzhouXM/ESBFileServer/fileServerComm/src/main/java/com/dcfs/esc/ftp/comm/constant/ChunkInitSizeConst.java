package com.dcfs.esc.ftp.comm.constant;

/**
 * Created by mocg on 2017/6/3.
 */
public class ChunkInitSizeConst {

    public static final int DOWNLOAD_DATA_CHUNK_SIZE = SysConst.DEF_PIECE_NUM + 500;
    public static final int UPLOAD_DATA_CHUNK_SIZE = SysConst.DEF_PIECE_NUM + 500;
    public static final int FILE_DATA_CHUNK_SIZE = SysConst.DEF_PIECE_NUM + 500;

    private ChunkInitSizeConst() {
    }
}
