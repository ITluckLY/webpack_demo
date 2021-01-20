package com.dcfs.esc.ftp.datanode.nework.server;

import com.dcfs.esc.ftp.datanode.http.NettyServerDispatcherHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServerChannelInitializer extends ChannelInitializer {
    private String uploadPath;
    public HttpServerChannelInitializer(String uploadPath){
        this.uploadPath = uploadPath;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();
        ph.addLast("encoder",new HttpResponseEncoder());
        ph.addLast("decoder",new HttpRequestDecoder());
        ph.addLast("aggregator", new HttpObjectAggregator(10*1024*1024));//把单个http请求转为FullHttpReuest或FullHttpResponse
//        ph.addLast("handler", new NettyServerHandler(uploadPath));// 服务端业务逻辑
        ph.addLast("handler", new NettyServerDispatcherHandler());// 服务端业务逻辑

    }

}
