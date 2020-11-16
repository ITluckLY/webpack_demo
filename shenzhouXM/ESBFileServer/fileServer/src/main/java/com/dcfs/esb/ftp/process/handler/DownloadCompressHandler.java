package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esb.ftp.common.compress.CompressFactory;
import com.dcfs.esc.ftp.comm.dto.FileDownloadDataReqDto;
import com.dcfs.esc.ftp.comm.dto.FileDownloadDataRspDto;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.DownloadProcessHandlerAdapter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by mocg on 2017/6/15.
 */
public class DownloadCompressHandler extends DownloadProcessHandlerAdapter {

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        FileDownloadDataReqDto reqDto = ctx.requestObj(FileDownloadDataReqDto.class);
        FileDownloadDataRspDto rspDto = ctx.responseObj(FileDownloadDataRspDto.class);
        if (StringUtils.isEmpty(reqDto.getCompressMode())) {
            rspDto.setCompressMode(null);
            return msg;
        }

        byte[] fileCont = rspDto.getFileCont();
        byte[] bytes = CompressFactory.compress(fileCont, reqDto.getCompressMode());
        rspDto.fileCont(bytes);
        rspDto.setCompressMode(reqDto.getCompressMode());

        return msg;
    }
}
