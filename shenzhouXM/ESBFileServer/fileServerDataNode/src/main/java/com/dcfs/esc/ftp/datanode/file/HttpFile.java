package com.dcfs.esc.ftp.datanode.file;

import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 文件服务器的文件处理类，处理的功能包括：
 * 实现服务端文件的上传和下载；
 */
public class HttpFile extends SDKRequestHead {
    private static final Logger log = LoggerFactory.getLogger(HttpFile.class);

    /* 当前传输内容的大小  */
    private int contLen = 0;
    /* 文件的内容 */
    private byte[] fileCont;
    /* 是否是最后一个分片 */


    /**
     * 获取body参数
     * @param request
     * @return
     */
    private HttpFile getHeader(FullHttpRequest request){
        HttpHeaders headers = request.headers();
        List<Map.Entry<String, String>> headerMap =  headers.entries();
        HttpFile httpFile = new HttpFile();
        for(Map.Entry<String, String>header:headerMap) {
            if (header.getKey().equalsIgnoreCase("key")) {
                httpFile.setKey(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("offset")) {
                httpFile.setOffset(Integer.valueOf(header.getValue()));
                continue;
            }

            if (header.getKey().equalsIgnoreCase("sign")) {
                httpFile.setSign(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("uid")) {
                httpFile.setUid(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("passwdid")) {
                httpFile.setPasswdId(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("fileSize")) {
                httpFile.setFileSize(Integer.valueOf(header.getValue()));
                continue;
            }

            if (header.getKey().equalsIgnoreCase("RqsSrlNo")) {
                httpFile.setRqsSrlNo(header.getValue());
                continue;

            }

            if (header.getKey().equalsIgnoreCase("System")) {
                httpFile.setSystemGroup(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("fileName")){
                httpFile.setFileName(header.getValue());
                continue;
            }

            if (header.getKey().equalsIgnoreCase("tranCode")) {
                httpFile.setTranCode(header.getValue());
                continue;

            }



        }
        return httpFile;
    }

    /**
     * 获取body参数
     * @param request
     * @return
     */
    private ByteBuf getBodyByteBuf(FullHttpRequest request){
        ByteBuf buf = request.content();
        return buf;
    }
    /**
     * 获取body参数
     * @param request
     * @return
     */
    private String printBodyByCharset(FullHttpRequest request, Charset charset){
        ByteBuf buf = request.content();
        return buf.toString(charset);
    }
    /**
     * 获取body参数
     * @return
     */
    private String printBodyByCharset(ByteBuf buf, Charset charset){
        return buf.toString(charset);
    }
    /**
     * 获取body参数
     * @param request
     * @return
     */
    private String printBodyByU8(FullHttpRequest request, Charset charset){
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }
    /**
     * 获取body参数
     * @return
     */
    private String printBodyByU8(ByteBuf buf,  Charset charset){
        return buf.toString(CharsetUtil.UTF_8);
    }

    public int getContLen() {
        return contLen;
    }

    public void setContLen(int contLen) {
        this.contLen = contLen;
    }

    public byte[] getFileCont() {
        return fileCont;
    }

    public void setFileCont(byte[] fileCont) {
        this.fileCont = fileCont;
    }
}
