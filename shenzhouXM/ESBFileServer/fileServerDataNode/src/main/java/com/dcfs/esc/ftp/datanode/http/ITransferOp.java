package com.dcfs.esc.ftp.datanode.http;

import com.dcfs.esb.ftp.common.error.FtpException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by Tianyza on 2019/12/20.
 */
public interface ITransferOp {
    String doTransferFile(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws FtpException;
}
