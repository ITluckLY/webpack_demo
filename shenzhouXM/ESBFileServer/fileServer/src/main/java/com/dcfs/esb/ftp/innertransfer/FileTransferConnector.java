package com.dcfs.esb.ftp.innertransfer;

import com.dcfs.esb.ftp.utils.GsonUtil;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by mocg on 2016/7/25.
 */
public class FileTransferConnector {
    private static int timeOut = 300000;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public FileTransferConnector(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public FileTransferConnector(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void writeHead(FileTransferBean bean) throws IOException {
        String json = GsonUtil.toJson(bean);
        byte[] bytes = json.getBytes(Charset.forName("UTF-8"));
        out.writeInt(bytes.length);
        out.write(bytes);
        out.flush();
    }

    public FileTransferBean readHead() throws IOException {
        setSocketTimeOut();
        int len = in.readInt();
        byte[] bytes = new byte[len];
        IOUtils.read(in, bytes);
        String json = new String(bytes, "UTF-8");
        return GsonUtil.fromJson(json, FileTransferBean.class);
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

    public void writeFileEnd() throws IOException {
        out.writeInt(-999);
        out.flush();
    }

    public byte[] readFileContent() throws IOException {
        int len = in.readInt();
        //len=-999时表示读取文件完成
        if (len == -999) return null;//NOSONAR
        byte[] bytes = new byte[len];
        IOUtils.read(in, bytes);
        return bytes;
    }

    public void writeFinish() throws IOException {
        out.writeInt(0);
        out.flush();
    }

    public boolean readFinish() throws IOException {
        int i = in.readInt();
        return i == 0;
    }

    public void close() throws IOException {
        socket.close();
    }

    public void closeQuietly() {
        IOUtils.closeQuietly(socket);
    }


    /**
     * 设置socket的超时时间
     * <p>
     * param time 超时时间，单位是毫秒
     *
     * @throws IOException
     */
    public void setSocketTimeOut() throws IOException {
        socket.setKeepAlive(false);
        socket.setSoTimeout(timeOut);
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }


}
