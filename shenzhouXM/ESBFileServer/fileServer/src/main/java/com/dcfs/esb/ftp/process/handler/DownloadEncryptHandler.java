package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esc.ftp.comm.dto.FileDownloadDataReqDto;
import com.dcfs.esc.ftp.comm.dto.FileDownloadDataRspDto;
import com.dcfs.esc.ftp.comm.util.ScrtUtil;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.DownloadProcessHandlerAdapter;

/**
 * Created by mocg on 2017/6/15.
 */
public class DownloadEncryptHandler extends DownloadProcessHandlerAdapter {

    private byte[] desKey;

    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        desKey = ctx.getChannelContext().desKey();
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        FileDownloadDataReqDto reqDto = ctx.requestObj(FileDownloadDataReqDto.class);
        FileDownloadDataRspDto rspDto = ctx.responseObj(FileDownloadDataRspDto.class);
        if (!reqDto.isScrt()) {
            rspDto.setScrt(false);
            return msg;
        }

        byte[] fileCont = rspDto.getFileCont();
        if (fileCont != null) {
            byte[] decryptBytes = ScrtUtil.encrypt(fileCont, desKey);
            rspDto.fileCont(decryptBytes);
            rspDto.setScrt(true);
        } else rspDto.setScrt(false);
        return msg;
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        desKey = null;
    }
}
