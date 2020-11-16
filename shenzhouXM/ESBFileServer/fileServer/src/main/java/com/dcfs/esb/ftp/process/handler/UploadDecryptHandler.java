package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esc.ftp.comm.dto.FileUploadDataReqDto;
import com.dcfs.esc.ftp.comm.util.ScrtUtil;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;

/**
 * Created by mocg on 2017/6/15.
 */
public class UploadDecryptHandler extends UploadProcessHandlerAdapter {

    private byte[] desKey;

    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        desKey = ctx.getChannelContext().desKey();
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        FileUploadDataReqDto reqDto = ctx.requestObj(FileUploadDataReqDto.class);
        byte[] fileCont = reqDto.getFileCont();
        if (!reqDto.isScrt() || fileCont == null) return msg;

        byte[] decryptBytes = ScrtUtil.decrypt(fileCont, desKey);
        reqDto.fileCont(decryptBytes);

        return msg;
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        desKey = null;
    }
}
