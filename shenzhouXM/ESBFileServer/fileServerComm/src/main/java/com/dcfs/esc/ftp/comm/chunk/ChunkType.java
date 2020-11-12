package com.dcfs.esc.ftp.comm.chunk;

/**
 * Created by mocg on 2017/6/2.
 */
public enum ChunkType {
    None((byte) 0), End((byte) 1), Exception((byte) 2), NoAuth((byte) 3), Heartbeat((byte) 4),//NOSONAR
    Init((byte) 9),//NOSONAR
    //DataNode
    DownloadAuthReq((byte) 10), DownloadAuthRsp((byte) 11), DownloadDataReq((byte) 12), DownloadDataRsp((byte) 13), DownloadResult((byte) 14),//NOSONAR
    UploadAuthReq((byte) 20), UploadAuthRsp((byte) 21), UploadDataReq((byte) 22), UploadDataRsp((byte) 23), UploadResult((byte) 24),//NOSONAR
    NodeListReq((byte) 30), NodeListRsp((byte) 31),//NOSONAR
    //nameNode
    PutFilePathReq((byte) 60), PutFilePathRsp((byte) 61), GetFilePathReq((byte) 62), GetFilePathRsp((byte) 63), RemoveFilePathReq((byte) 64), RemoveFilePathRsp((byte) 65),//NOSONAR
    //cliserver
    FileMsgPushReq((byte) 90), FileMsgPushRsp((byte) 91), StateHeartbeatReq((byte) 92), StateHeartbeatRsp((byte) 93), FileMsgDownloadResultReq((byte) 94), FileMsgDownloadResultRsp((byte) 95),ClientVersionReq((byte) 96),ClientVersionRsp((byte) 97),FileSearchReq((byte)98),FileSearchRsp((byte)99),StopClientReq((byte)100),StopClientRsp((byte)101),//NOSONAR
    Tail((byte) 127);//NOSONAR

    byte val;

    ChunkType(byte val) {
        this.val = val;
    }

    public byte getVal() {
        return val;
    }

    public static ChunkType find(int typeVal) {
        for (ChunkType chunkType : values()) {
            if (chunkType.val == typeVal) return chunkType;
        }
        return None;
    }
}
