package com.dc.smarteam.common.adapter;

import com.dc.smarteam.common.adapter.processor.IProcessor;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
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
    private static final String PRE_COMMAND_HEAD = "000000";
    private static final int CMD_PORT = 19100;
    private static final int TIME_OUT = 60000;
    private static final int TIME_OUT2 = 300000;
    private Socket client;

    private void connect(FtServiceNode node, boolean isKeepAlive) throws IOException {
        String ipAddress = node.getIpAddress();
        if (null == ipAddress) {
            log.error("ip地址为空！");
        } else {
            int port = StringUtils.isEmpty(node.getCmdPort()) ? CMD_PORT : Integer.parseInt(node.getCmdPort());
            try {
                client = new Socket(ipAddress, port);
                if (isKeepAlive) {
                    client.setKeepAlive(true);
                } else {
                    client.setSoTimeout(TIME_OUT2);//超时时间
                }
                log.debug("适配器连接成功#host[{}]port[{}]", ipAddress, port);
            } catch (IOException e) {
                log.error("适配器连接异常！host[" + ipAddress + "]port[" + port + "]", e);
                throw e;
            }
        }
    }

    private void connect(ClientSyn clientSyn, boolean isKeepAlive) throws IOException {
        String ipAddress = clientSyn.getIp();
        if (null == ipAddress) {
            log.error("ip地址为空！");
        } else {
            int port =  clientSyn.getCmdPort() != null ?Integer.parseInt(clientSyn.getCmdPort()) :CMD_PORT ;
            try {
                client = new Socket(ipAddress, port);
                if (isKeepAlive) {
                    client.setKeepAlive(true);
                } else {
                    client.setSoTimeout(TIME_OUT);//超时时间
                }
                log.debug("适配器连接成功#host[{}]port[{}]", ipAddress, port);
            } catch (IOException e) {
                log.error("适配器连接异常！host[" + ipAddress + "]port[" + port + "]", e);
                throw e;
            }
        }
    }

    public void invoke(String message, FtServiceNode node, IProcessor callBack) {
        byte[] bytes = getByteForTran(message);
        try {
            invoke(bytes, node, callBack);
        } catch (IOException ignored) {
            //nothing
        }
    }

    private void invoke(byte[] msg, FtServiceNode node, IProcessor callBack) throws IOException {
        connect(node, false);
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
            callBack.process(returnMsg);
        } catch (IOException e) {
            log.error("client[" + client + "]指令发送异常！指令信息[" + new String(msg) + "]", e);//NOSONAR
            throw e;
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                log.error("", e);
            }
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                log.error("", e);
            }
            try {
                client.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public String invoke(String message, FtServiceNode node) {
        byte[] bytes = getByteForTran(message);
        try {
            return invoke(bytes, node);
        } catch (IOException e) {
            return null;
        }
    }

    public <T> ResultDto<T> invoke(String message, FtServiceNode node, Class<T> tClass) {
        byte[] bytes = getByteForTran(message);
        String json = null;
        try {
            json = invoke(bytes, node);
            log.info("请求地址:[{}:{}],请求报文:{},响应报文:{}", node.getIpAddress(), node.getCmdPort(), message, json);
        } catch (IOException e) {
            return ResultDtoTool.buildError("0303", "连接datanode节点出错");
        }
        return ResultDtoTool.fromJson(json, tClass);
    }

    public <T> ResultDto<T> invoke(String message, FtServiceNode node, Class<T> tClass, boolean isKeepAlive) {
        byte[] bytes = getByteForTran(message);
        String json = null;
        try {
            json = invoke(bytes, node, isKeepAlive);
        } catch (IOException e) {
            return ResultDtoTool.buildError("0304", "连接监控端monitor出错");
        }
        return ResultDtoTool.fromJson(json, tClass);
    }
    public <T> ResultDto<T> invoke(String message, ClientSyn clientSyn, Class<T> tClass, boolean isKeepAlive) {
        byte[] bytes = getByteForTran(message);
        String json = null;
        try {
            json = invoke(bytes, clientSyn, isKeepAlive);
        } catch (IOException e) {
            return ResultDtoTool.buildError("0304", "monitor连通失败");
        }
        return ResultDtoTool.fromJson(json, tClass);
    }

    private String invoke(byte[] msg, FtServiceNode node, boolean isKeepAlive) throws IOException {
        connect(node, isKeepAlive);
        if (null == client) {
            return null;
        }
        BufferedOutputStream out = null;
        DataInputStream in = null;
        try {
            out = new BufferedOutputStream(client.getOutputStream());
            out.write(genLengthHeader(msg));
            out.write(msg);
            out.flush();
            byte[] lengthHeaderByte = new byte[COMMAND_HEAD_LENGTH];
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            in.readFully(lengthHeaderByte);
            String lengthHeaderStr = new String(lengthHeaderByte);
            int lengthHeader = Integer.parseInt(lengthHeaderStr);
            byte[] returnMsg = new byte[lengthHeader];
            in.readFully(returnMsg);
            return new String(returnMsg, Global.TRANSMIT_ENCODING);
        } catch (IOException e) {
            log.error("client[" + client + "]指令发送异常！指令信息[" + new String(msg) + "]", e);
            throw e;
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                if (in != null) in.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                client.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private String invoke(byte[] msg, ClientSyn node, boolean isKeepAlive) throws IOException {
        connect(node, isKeepAlive);
        if (null == client) {
            return null;
        }
        BufferedOutputStream out = null;
        DataInputStream in = null;
        try {
            out = new BufferedOutputStream(client.getOutputStream());
            out.write(genLengthHeader(msg));
            out.write(msg);
            out.flush();
            byte[] lengthHeaderByte = new byte[COMMAND_HEAD_LENGTH];
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            in.readFully(lengthHeaderByte);
            String lengthHeaderStr = new String(lengthHeaderByte);
            int lengthHeader = Integer.parseInt(lengthHeaderStr);
            byte[] returnMsg = new byte[lengthHeader];
            in.readFully(returnMsg);
            return new String(returnMsg, Global.TRANSMIT_ENCODING);
        } catch (IOException e) {
            log.error("client[" + client + "]指令发送异常！指令信息[" + new String(msg) + "]", e);
            throw e;
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                if (in != null) in.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                client.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public <T> ResultDto<T> invoke(String message, ClientSyn clientSyn, Class<T> tClass) {
        byte[] bytes = getByteForTran(message);
        String json = null;
        try {
            json = invoke(bytes, clientSyn);
            log.info("请求地址:[{}:{}],请求报文:{},响应报文:{}", clientSyn.getIp(), CMD_PORT, message, json);
        } catch (IOException e) {
            return ResultDtoTool.buildError("0303", "连接客户端出错");
        }
        return ResultDtoTool.fromJson(json, tClass);
    }

    private String invoke(byte[] msg, FtServiceNode node) throws IOException {
        connect(node, false);
        if (null == client) {
            return null;
        }
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
            return new String(returnMsg, Global.TRANSMIT_ENCODING);
        } catch (IOException e) {
            log.error("client[" + client + "]指令发送异常！指令信息[" + new String(msg) + "]", e);
            throw e;
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                if (in != null) in.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                client.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private String invoke(byte[] msg, ClientSyn clientSyn) throws IOException {
        connect(clientSyn, false);
        if (null == client) {
            return null;
        }
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
            return new String(returnMsg, Global.TRANSMIT_ENCODING);
        } catch (IOException e) {
            log.error("client[" + client + "]指令发送异常！指令信息[" + new String(msg) + "]", e);
            throw e;
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                if (in != null) in.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                client.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
    public File fileInvoke(String message, FtServiceNode node) {
        byte[] bytes = getByteForTran(message);
        try {
            return fileInvoke(bytes, node);
        } catch (IOException e) {
            return null;
        }
    }

    private File fileInvoke(byte[] msg, FtServiceNode node) throws IOException {
        connect(node, false);
        if (null == client) {
            return null;
        }
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
            byte[] inputByte = new byte[1024];
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
        String lengthStr = String.valueOf(length);
        int len = lengthStr.length();
        if (len < 6) {
            lengthStr = PRE_COMMAND_HEAD.substring(len) + lengthStr;
        }
        return lengthStr.getBytes();
    }

    private byte[] getByteForTran(String message) {
        return message.getBytes(Charset.forName(Global.TRANSMIT_ENCODING));
    }
}
