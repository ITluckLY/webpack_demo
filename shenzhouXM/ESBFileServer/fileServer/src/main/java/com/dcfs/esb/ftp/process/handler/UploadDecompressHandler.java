package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esb.ftp.common.compress.CompressFactory;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataReqDto;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;

/**
 * Created by mocg on 2017/6/15.
 */
public class UploadDecompressHandler extends UploadProcessHandlerAdapter {

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        FileUploadDataReqDto reqDto = ctx.requestObj(FileUploadDataReqDto.class);
        String compressMode = reqDto.getCompressMode();
        byte[] fileCont = reqDto.getFileCont();
        if (compressMode == null || fileCont == null) return msg;

        byte[] bytes = CompressFactory.decompress(fileCont, compressMode);
        reqDto.fileCont(bytes);

        return msg;
    }
}
