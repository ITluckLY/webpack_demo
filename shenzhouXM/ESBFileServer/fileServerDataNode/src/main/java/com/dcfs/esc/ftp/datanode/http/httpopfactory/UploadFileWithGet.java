package com.dcfs.esc.ftp.datanode.http.httpopfactory;

import com.dcfs.esc.ftp.datanode.http.ITransferOp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by Tianyza on 2019/12/20.
 */
public class UploadFileWithGet implements ITransferOp {

    @Override
    public String doTransferFile(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        return null;
    }
}
