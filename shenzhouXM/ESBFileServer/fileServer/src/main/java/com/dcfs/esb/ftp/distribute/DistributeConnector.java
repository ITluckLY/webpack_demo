package com.dcfs.esb.ftp.distribute;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.utils.GsonUtil;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by mocg on 2016/7/14.
 */
public class DistributeConnector {
    private static final int TIME_OUT = 300000;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public DistributeConnector(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public DistributeConnector(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void writeHead(DistributeFileMsgBean bean) throws IOException {
        String json = GsonUtil.toJson(bean);
        byte[] bytes = json.getBytes(Charset.forName("UTF-8"));
        out.writeInt(bytes.length);
        out.write(bytes);
        out.flush();
    }

    public DistributeFileMsgBean readHead() throws IOException {
        setSocketTimeOut();
        int len = in.readInt();
        byte[] bytes = new byte[len];
        IOUtils.read(in, bytes);
        String json = new String(bytes, "UTF-8");
        return GsonUtil.fromJson(json, DistributeFileMsgBean.class);
    }

    public void writeFileContent(byte[] bytes) throws IOException {
        out.writeInt(bytes.length);
        out.write(bytes);
        out.flush();
    }

    public void writeFileContent(byte[] bytes, int off, int len) throws IOException {
        out.writeInt(len - off);
        out.write(bytes, off, len);
        out.flush();
    }

    public byte[] readFileContent() throws IOException {
        int len = in.readInt();
        //len=0时表示读取文件完成
        if (len == 0) return null;//NOSONAR
        byte[] bytes = new byte[len];
        IOUtils.read(in, bytes);
        return bytes;
    }

    public void close() throws IOException {
        socket.close();
    }

    /**
     * 设置socket的超时时间
     * <p>
     * param time 超时时间，单位是毫秒
     *
     * @throws FtpException
     */
    public void setSocketTimeOut() throws IOException {
        socket.setKeepAlive(false);
        socket.setSoTimeout(TIME_OUT);
    }
}
