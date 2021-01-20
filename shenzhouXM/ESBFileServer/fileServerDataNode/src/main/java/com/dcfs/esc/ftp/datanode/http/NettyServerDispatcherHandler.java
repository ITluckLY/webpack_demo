package com.dcfs.esc.ftp.datanode.http;

import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esc.ftp.datanode.constant.HttpGlobalCons;
import com.dcfs.esc.ftp.datanode.helper.ChannelHelper;
import com.dcfs.esc.ftp.datanode.helper.HttpHelper;
import com.dcfs.esc.ftp.datanode.http.httpimpl.TransferOpEnum;
import com.dcfs.esc.ftp.datanode.http.httpopfactory.DownloadFileWithGet;
import com.dcfs.esc.ftp.datanode.http.httpopfactory.DownloadFileWithPost;
import com.dcfs.esc.ftp.datanode.http.httpopfactory.UploadFileWithPost;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;


public class NettyServerDispatcherHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyServerDispatcherHandler.class);
    private static final String DEF_UPLOAD = "/upload";
    private static final String DEF_DOWNLOAD = "/download";

    /*
     * 收到消息时，返回信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String result = "开始处理请求";
        if (!(msg instanceof FullHttpRequest)) {
            result = "未知请求!";
            HttpHelper.send(ctx, result, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        String userIp = ChannelHelper.getRemoteAddress(ctx);

        try {
            HttpMethod method = httpRequest.method();//获取请求方法

            byte[] body = getBody(httpRequest);

            Integer opType = checkTransferType(httpRequest);
            switch (TransferOpEnum.getByValue(opType)) {
                case UPLOAD_POST:
                    ITransferOp uploadFileWithPost = new UploadFileWithPost(userIp, body);
                    uploadFileWithPost.doTransferFile(ctx, httpRequest);
                    break;
                case DOWNLOAD_POST:
                    ITransferOp downFileWithPost = new DownloadFileWithPost(userIp, body);
                    downFileWithPost.doTransferFile(ctx, httpRequest);
                    break;
                case UPLOAD_GET:
                    // TODO
                    break;
                case DOWNLOAD_GET:
                    ITransferOp downloadFileWithGet = new DownloadFileWithGet(userIp);
                    downloadFileWithGet.doTransferFile(ctx, httpRequest);

                    break;
                case PUT:
                    //接受到的消息，做业务逻辑处理...
                    result = "PUT请求";
                    HttpHelper.send(ctx, result, HttpResponseStatus.OK);
                case DELETE:
                    result = "DELETE请求";
                    HttpHelper.send(ctx, result, HttpResponseStatus.OK);
                case OPTIONS:
                    result = "OPTIONS请求";
                    HttpHelper.send(ctx, "", HttpResponseStatus.OK);
                default:
                    result = "请求[" + method + "]非法请求!";
                    HttpHelper.send(ctx, result, HttpResponseStatus.BAD_REQUEST);

                    break;
            }
            log.info(result);
        } catch (Exception e) {
            log.info("处理请求失败!");
            e.printStackTrace();
        } finally {
            //释放请求
            log.info("释放请求!");
            httpRequest.release();
        }
    }

    /**
     * 获取body参数
     *
     * @param request
     * @return
     */
    private byte[] getBody(FullHttpRequest request) {
        ByteBuf buf = request.content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        return req;
    }

    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
        super.channelActive(ctx);
    }

    /**
     * 校验传输类型
     *
     * @param httpRequest
     * @return
     */
    public Integer checkTransferType(FullHttpRequest httpRequest) {
        String path = httpRequest.uri();          //获取路径
        HttpMethod method = httpRequest.method();//获取请求方法
        //如果是POST请求
        if (HttpMethod.POST.equals(method)) {

            if (path.startsWith(DEF_UPLOAD)) {//上传

                return HttpGlobalCons.TYPE_UPLOAD_POST_VAL;

            } else if (path.startsWith(DEF_DOWNLOAD)) {//下载

                return HttpGlobalCons.TYPE_DOWNLOAD_POST_VAL;
            }

        } else if (HttpMethod.GET.equals(method)) {

            //如果是GET请求
            if (path.startsWith(DEF_DOWNLOAD) && null != path) {//get下載

                return HttpGlobalCons.TYPE_DOWNLOAD_GET_VAL;
            }

        } else if (HttpMethod.PUT.equals(method)) {//如果是PUT请求
            //接受到的消息，做业务逻辑处理...
            return HttpGlobalCons.TYPE_PUT_VAL;

        } else if (HttpMethod.DELETE.equals(method)) {//如果是DELETE请求
            //接受到的消息，做业务逻辑处理...
            return HttpGlobalCons.TYPE_DELETE_VAL;
        } else if (HttpMethod.OPTIONS.equals(method)) {
            return HttpGlobalCons.TYPE_OPTIONS_VAL;
        }
        return HttpGlobalCons.TYPE_UNKNOWN_VAL;
    }

    public static void main(String[] args) {
        String enPasswd = "q9HhbBPF/cgxZ+9Mbv4j/HTI9DAENK8KUgoOC4V1IVYk3sib7wL3rxTpWP5TF6VXIqoFhpjvGrhwCUELMkBzlNlCPBKGE34+XGVApNen35E=";
//        String dePasswd = ScrtUtil.decrypt3DES(enPasswd);

        String dePasswd = ScrtUtil.decryptEsb(enPasswd);
        System.out.println(dePasswd);
        String enpwd = "29718edb-f8ad-4695-a6c3-275a7f59";
        String en = ScrtUtil.encryptEsb(enpwd);
        System.out.println(en);

    }
}