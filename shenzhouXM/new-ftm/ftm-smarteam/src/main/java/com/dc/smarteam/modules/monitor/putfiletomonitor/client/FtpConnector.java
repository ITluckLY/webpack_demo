package com.dc.smarteam.modules.monitor.putfiletomonitor.client;

import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpErrCode;
import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpException;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgBean;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgBeanHelper;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgType;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.Des;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * 文件服务器的连接对象，完成客户端和服务端的数据交换
 */
public class FtpConnector {
    private static final Logger log = LoggerFactory.getLogger(FtpConnector.class);

    private static final String DETECT = "DETE";
    private static int timeOut = 300000;

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    /**
     * 构造函数，客户端向服务器建立连接使用
     *
     * @param ip   - 服务器IP地址
     * @param port - 服务器IP端口
     * @throws FtpException
     */
    public FtpConnector(String ip, int port) throws FtpException {
        log.info("开始连接文件服务器...{}:{}", ip, port);
        try {
            socket = new Socket(ip, port);
            log.info("建立文件服务器连接成功 --> {}:{}", ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            log.info("建立文件服务器连接异常 --> {}:{}", ip, port);
            throw new FtpException(FtpErrCode.SOCKET_CONNECT_ERROR, e);
        }
    }

    /**
     * 构造函数，服务器收到客户端的socket连接后使用
     *
     * @param socket socket
     * @throws FtpException
     */
    public FtpConnector(Socket socket) throws FtpException {
        try {
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.SOCKET_CONNECT_ERROR, e);
        }
    }

    public static void setTimeOut(int time) {
        timeOut = time;
    }

    /**
     * close方法，是否socket，输入流和输出流
     *
     * @throws FtpException
     */
    public void close() throws FtpException {
        log.info("Close Socket.");
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error("关闭socket输入流出错", e);
                throw new FtpException(FtpErrCode.SOCKET_CLOSE_ERROR, e);
            }
            in = null;
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                log.error("关闭socket输出流出错", e);
                throw new FtpException(FtpErrCode.SOCKET_CLOSE_ERROR, e);
            }
            out = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("关闭socket信息出错", e);
                throw new FtpException(FtpErrCode.SOCKET_CLOSE_ERROR, e);
            }
            socket = null;
        }
    }

    /**
     * 获取socket的输入流
     *
     * @return socket的输入流
     */
    public DataInputStream getIn() {
        return in;
    }

    /**
     * 获取socket的输出流
     *
     * @return socket的输出流
     */
    public DataOutputStream getOut() {
        return out;
    }

    /**
     * 获取socket连接对象
     *
     * @return socket的对象
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * 写入文件传输的消息头
     *
     * @param bean 文件消息对象
     *             param isRenm 是否是重命名指令，是：true 否：false
     * @throws FtpException
     */
    public void writeHead(FileMsgBean bean) throws FtpException {
        this.writeHead(bean, false);
    }

    /**
     * 写入文件传输的消息头
     *
     * @param bean   文件消息对象
     * @param isRenm 是否是重命名指令，是：true 否：false
     * @throws FtpException
     */
    public void writeHead(FileMsgBean bean, boolean isRenm) throws FtpException {
        try {
            byte[] head = null;
            if (isRenm) {
                if (bean.getFileRetMsg() == null) {
                    head = (bean.getFileMsgFlag()).getBytes("UTF-8");
                } else {
                    head = (bean.getFileMsgFlag() + this.getEnoughString(bean.getFileRetMsg(), 60, " ")).getBytes("UTF-8");
                    out.writeInt(head.length);
                }

                if (log.isDebugEnabled()) log.debug("待返回请求系统数据为[{}]", new String(head, "UTF-8"));
            } else {
                // 针对AS400进行转换控制
                if (bean.isEbcdicFlag())
                    //head = bean.convertHeadToXml().getBytes("CP935");
                    head = FileMsgBeanHelper.convertHeadToXml(bean).getBytes("CP935");
                else
                    //head = bean.convertHeadToXml().getBytes("UTF-8");
                    head = FileMsgBeanHelper.convertHeadToXml(bean).getBytes("UTF-8");

                out.writeInt(head.length);
            }
            out.write(head);
            out.flush();

        } catch (IOException e) {
            log.error("XML文件格式错误", e);
            throw new FtpException(FtpErrCode.HEAD_XML_ERROR, e);
        }
    }

    /**
     * 获取足够的信息长度
     *
     * @param inStr  源字符串
     * @param num    需要的位数
     * @param addStr 补位字符串
     * @return
     */
    private String getEnoughString(String inStr, int num, String addStr) {
        StringBuilder sb_str = new StringBuilder(inStr);
        int len = num - inStr.length();
        for (int i = 0; i < len; i++)
            sb_str.append(addStr);

        return sb_str.toString();
    }

    /**
     * 读取文件传输的消息头
     *
     * @param bean 文件消息对象，输出结果存放在该对象中返回
     * @throws FtpException
     */
    public void readHead(FileMsgBean bean) throws FtpException {
        this.setSocketTimeOut();
        try {
            int len = readInt();
            if (len == -1) {
                //负载设备监控探测
                bean.setFileMsgFlag(FileMsgType.F5);
                bean.setLastPiece(true);
                return;
            }
            byte[] head = this.readNum(len);
            String headStr = null;
            byte[] typeBts = new byte[3];
            System.arraycopy(head, 0, typeBts, 0, 3);
            String type = new String(typeBts, "UTF-8");
            if (FileMsgType.RNAM.equals(type)) {
                // 处理来自于ESB的请求
                String req = new String(head, "UTF-8");
                log.debug("收到的ESB请求报文为[{}]", req);
                byte[] orgi = new byte[100];
                byte[] newn = new byte[100];
                System.arraycopy(head, 3, orgi, 0, 100);
                System.arraycopy(head, 103, newn, 0, 100);

                bean.setFileMsgFlag(type);
                bean.setClientFileName(new String(newn, "UTF-8").trim());
                bean.setFileName(new String(orgi, "UTF-8").trim());
            } else if (FileMsgType.F5_RUN.equals(type)) {//目前没有使用到该功能
                bean.setFileMsgFlag(FileMsgType.F5_RUN);
                bean.setLastPiece(true);
            } else if (FileMsgType.F5_STOP.equals(type)) {//目前没有使用到该功能
                bean.setFileMsgFlag(FileMsgType.F5_STOP);
                bean.setLastPiece(true);
            } else {
                // 针对AS400进行转码控制
                if (head[0] == '<')
                    headStr = new String(head, "UTF-8");
                else {
                    bean.setEbcdicFlag(true);
                    headStr = new String(head, "CP935");
                }

                //bean.convertXmlToHead(headStr);
                FileMsgBeanHelper.convertXmlToHead(bean, headStr);
            }
        } catch (Exception e) {
            if (e instanceof FtpException) {
                throw (FtpException) e;
            } else throw new FtpException(FtpErrCode.SOCKET_CONNECT_ERROR, e);
        }
        this.setKeepAlive();
        bean.setClientIp(socket.getInetAddress().getHostAddress());
        bean.setServerIp(socket.getLocalAddress().getHostAddress());
    }

    /**
     * 设置socket的超时时间
     * <p>
     * param time 超时时间，单位是毫秒
     *
     * @throws FtpException
     */
    public void setSocketTimeOut() throws FtpException {
        try {
            socket.setKeepAlive(false);
            socket.setSoTimeout(timeOut);
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_TIME_OUT_ERROR, e);
        }
    }

    /**
     * 设置socket一直保持
     * <p>
     * param on true-不超时 false-超时
     *
     * @throws FtpException
     */
    public void setKeepAlive() throws FtpException {
        try {
            socket.setKeepAlive(true);
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_TIME_OUT_ERROR, e);
        }
    }

    /**
     * 写入文件的某一个分片，根据bean中的isScrtFlag确定是否进行数据的加密
     *
     * @param bean 文件消息对象
     * @throws FtpException
     */
    public void writeFileContent(FileMsgBean bean) throws FtpException {
        try {
            byte[] scrtCont = null;
            int len = 0;
            if (bean.isScrtFlag()) {
                byte[] cont = new byte[bean.getContLen()];
                System.arraycopy(bean.getFileCont(), 0, cont, 0, cont.length);
                scrtCont = Des.encrypt3DES(cont, bean.getDesKey());
                len = scrtCont.length;
            } else {
                scrtCont = bean.getFileCont();
                len = bean.getContLen();
            }
            out.writeInt(len);
            out.write(scrtCont, 0, len);
            out.flush();
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.FILE_UP_ERROR, e);
        }
    }

    /**
     * 读取文件的某一个分片，根据bean中的isScrtFlag确定是否进行数据的加密
     *
     * @param bean 文件消息对象，输出结果存放在该对象中返回
     * @throws FtpException
     */
    public void readCont(FileMsgBean bean) throws FtpException {
        this.setSocketTimeOut();
        byte[] cont = null;
        int len = this.readInt();
        byte[] scrtCont = this.readNum(len);
        if (bean.isScrtFlag())
            cont = Des.decrypt3DES(scrtCont, bean.getDesKey());
        else
            cont = scrtCont;
        bean.setFileCont(cont);
        bean.setContLen(cont.length);
        this.setKeepAlive();
        log.info("end read cont");
    }

    /**
     * 读取int的一个数值，表明后续报文的长度
     *
     * @return 返回后续报文的长度
     * @throws FtpException
     */
    private int readInt() throws FtpException {
        try {
            int ch1 = in.read();
            int ch2 = in.read();
            int ch3 = in.read();
            int ch4 = in.read();
            if ((ch1 | ch2 | ch3 | ch4) < 0)
                throw new EOFException();
            return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4));

        } catch (IOException e) {
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        }
    }

    /**
     * 读取一定长度的socket输入数据
     *
     * @param num socket输入数据的长度
     * @throws FtpException
     */
    private byte[] readNum(int num) throws FtpException {
        // 最大读取请求数据长度1M
        if (num > 1024 * 1024) {
            log.error("请求数据长度过长，length=[{}]", num);
            throw new FtpException(FtpErrCode.READ_REQ_LENGTH_ERROR);
        }

        byte[] data = new byte[num];
        int offset = 0;
        int count = -1;
        try {
            while (-1 != (count = in.read(data, offset, num - offset))) {
                offset += count;
                if (offset == num) break;
            }
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        }
        return data;
    }
}
