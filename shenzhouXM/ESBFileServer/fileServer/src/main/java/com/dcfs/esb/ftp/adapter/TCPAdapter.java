package com.dcfs.esb.ftp.adapter;


import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by vincentfxz on 16/3/2.
 */
public class TCPAdapter {
    private static final Logger log = LoggerFactory.getLogger(TCPAdapter.class);

    private static final int COMMAND_HEAD_LENGTH = 6;
    private static final int TIME_OUT = 60000;
    private String ipAddress;
    private int port;
    private Socket client;

    public TCPAdapter(String ipAddress, int port) throws IOException {
        this(ipAddress, port, TIME_OUT);
    }

    public TCPAdapter(String ipAddress, int port, int timeout) throws IOException {
        this.ipAddress = ipAddress;
        this.port = port;
        connect(timeout);
    }

    private void connect(int timeout) throws IOException {
        try {
            client = new Socket(ipAddress, port);
            client.setSoTimeout(timeout);//超时时间
            log.debug("适配器连接成功#[{}:{}]", ipAddress, port);
        } catch (IOException e) {
            log.error("适配器连接异常#[{}:{}]", ipAddress, port, e);
            throw e;
        }
    }

    public String invoke(String message) {
        byte[] bytes = getByteForTran(message);
        return invoke(bytes);
    }

    public <T> ResultDto<T> invoke(String message, Class<T> tClass) {
        byte[] bytes = getByteForTran(message);
        String json = invoke(bytes);
        if (json == null) return ResultDtoTool.buildError("505", "发送异常");
        return ResultDtoTool.fromJson(json, tClass);
    }

    public String invoke(byte[] msg) {
        BufferedOutputStream out = null;
        DataInputStream in = null;
        try {
            out = new BufferedOutputStream(client.getOutputStream());
            out.write(genLengthHeader(msg));
            out.write(msg);
            out.flush();
            client.shutdownOutput();
            byte[] lengthHeaderByte = new byte[COMMAND_HEAD_LENGTH];
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            in.readFully(lengthHeaderByte);
            String lengthHeaderStr = new String(lengthHeaderByte);
            int lengthHeader = Integer.parseInt(lengthHeaderStr);
            byte[] returnMsg = new byte[lengthHeader];
            in.readFully(returnMsg);
            return new String(returnMsg, CommGlobalCons.TRANSMIT_ENCODING);
        } catch (Exception e) {
            log.error("client[{}]指令发送异常！指令信息[{}]", client, new String(msg), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(client);
        }
        return null;
    }

    public File fileInvoke(String message) {
        byte[] msg = getByteForTran(message);
        return fileInvoke(msg);
    }

    public File fileInvoke(byte[] msg) {
        BufferedOutputStream out = null;
        DataInputStream in = null;
        FileOutputStream fos = null;
        try {
            out = new BufferedOutputStream(client.getOutputStream());
            out.write(genLengthHeader(msg));
            out.write(msg);
            out.flush();
            client.shutdownOutput();
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            byte[] inputByte = new byte[1024];//NOSONAR
            String tempFilePath = TCPAdapter.class.getResource("/").getPath() + "tmp" + new Date().getTime() + "_" + RandomUtils.nextLong();
            File file = new File(tempFilePath);
            fos = new FileOutputStream(file);
            log.debug("开始接收数据...");
            int length;
            while ((length = in.read(inputByte, 0, inputByte.length)) > 0) {
                fos.write(inputByte, 0, length);
                fos.flush();
            }
            log.debug("完成接收");
            fos.close();
            return file;
        } catch (Exception e) {
            log.error("client[{}]指令发送异常！指令信息[{}]", client, new String(msg), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(client);
        }
        return null;
    }


    private byte[] genLengthHeader(byte[] msg) {
        int length = msg.length;
        StringBuilder lengthStr = new StringBuilder(String.valueOf(length));
        int len = lengthStr.length();
        for (int i = 0; i < COMMAND_HEAD_LENGTH - len; i++) {
            lengthStr.insert(0, "0");
        }
        return lengthStr.toString().getBytes();
    }

    private byte[] getByteForTran(String message) {
        return message.getBytes(Charset.forName(CommGlobalCons.TRANSMIT_ENCODING));
    }

}
