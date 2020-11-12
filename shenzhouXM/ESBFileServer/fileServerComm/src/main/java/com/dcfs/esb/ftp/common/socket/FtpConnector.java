package com.dcfs.esb.ftp.common.socket;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.helper.FileMsgBeanHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.common.scrt.Des;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esc.ftp.comm.constant.UnitCons;
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
    private static int commTimeOut = 300000;//NOSONAR
    private int timeOut = commTimeOut;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Long nano;
    private boolean inited;

    private static final String CHARSET_NAME_UTF8 = "UTF-8";
    private static final String CHARSET_NAME_CP935 = "CP935";

    /**
     * 构造函数，客户端向服务器建立连接使用
     *
     * @param ip   - 服务器IP地址
     * @param port - 服务器IP端口
     * @throws FtpException
     */
    public FtpConnector(String ip, int port) throws FtpException {
        log.debug("开始连接文件服务器...{}:{}", ip, port);
        try {
            socket = new Socket(ip, port);
            log.debug("建立文件服务器连接成功 --> {}:{}", ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            log.error("建立文件服务器连接异常 --> {}:{}", ip, port);
            throw new FtpException(FtpErrCode.SOCKET_CONNECT_ERROR, nano, e);
        }
        inited = true;
    }

    /**
     * 构造函数，服务器收到客户端的socket连接后使用
     *
     * @param socket socket
     * @throws FtpException
     */
    public FtpConnector(Socket socket) throws FtpException {//NOSONAR
        this.socket = socket;
    }

    public static int getCommTimeOut() {
        return commTimeOut;
    }

    public static void setCommTimeOut(int commTimeOut) {
        FtpConnector.commTimeOut = commTimeOut;
    }

    public void init() throws FtpException {
        if (inited) return;
        inited = true;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.SOCKET_CONNECT_ERROR, nano, e);
        }
    }

    /**
     * close方法，是否socket，输入流和输出流
     *
     * @throws FtpException
     */
    public void close() throws FtpException {
        log.debug("Close Socket...");
        IOException ioException = null;
        if (in != null) {
            try {
                in.close();
                in = null;
            } catch (IOException e) {
                ioException = e;
                log.error("关闭socket输入流出错", e);
            }
        }
        if (out != null) {
            try {
                out.close();
                out = null;
            } catch (IOException e) {
                ioException = e;
                log.error("关闭socket输出流出错", e);
            }
        }
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                ioException = e;
                log.error("关闭socket信息出错", e);
            }
        }
        if (ioException != null) {
            throw new FtpException(FtpErrCode.SOCKET_CLOSE_ERROR, nano, ioException);
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
        if (CapabilityDebugHelper.isOutBean()) {
            CapabilityDebugHelper.markCurrTime("writeHead-FileMsgBeanXML:" + FileMsgBeanHelper.toStringIgnoreEx(bean));
        }
        setNano(bean.getNano());
        try {
            byte[] head = null;

            if (isRenm) {
                if (bean.getFileRetMsg() == null) {
                    head = (bean.getFileMsgFlag()).getBytes(CHARSET_NAME_UTF8);
                } else {
                    head = (bean.getFileMsgFlag() + this.getEnoughString(bean.getFileRetMsg(), 60, " ")).getBytes(CHARSET_NAME_UTF8);//NOSONAR
                    out.writeInt(head.length);
                }

                if (log.isDebugEnabled()) log.debug("nano:{}#待返回请求系统数据为[{}]", nano, new String(head, CHARSET_NAME_UTF8));
            } else {
                // 针对AS400进行转换控制
                if (BooleanTool.toBoolean(bean.isEbcdicFlag()))
                    head = FileMsgBeanHelper.convertHeadToXml(bean).getBytes(CHARSET_NAME_CP935);
                else
                    head = FileMsgBeanHelper.convertHeadToXml(bean).getBytes(CHARSET_NAME_UTF8);

                out.writeInt(head.length);
            }
            CapabilityDebugHelper.markCurrTime("FtpConn-writeHead-bytes");
            out.write(head);
            out.flush();

        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_ERROR, nano, e);
        } catch (IOException e) {
            log.error("nano:{}#XML文件格式错误", nano, e);
            throw new FtpException(FtpErrCode.HEAD_XML_ERROR, nano, e);
        }
        CapabilityDebugHelper.markCurrTime("FtpConn-writeHeadEnd");
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
        StringBuilder builder = new StringBuilder(inStr);
        int len = num - inStr.length();
        for (int i = 0; i < len; i++)
            builder.append(addStr);

        return builder.toString();
    }

    /**
     * 读取文件传输的消息头
     *
     * @param bean 文件消息对象，输出结果存放在该对象中返回
     * @throws FtpException
     */
    public void readHead(FileMsgBean bean) throws FtpException {
        if (CapabilityDebugHelper.isOutBean()) {
            CapabilityDebugHelper.markCurrTime("before-readHead-FileMsgBeanXML:" + FileMsgBeanHelper.toStringIgnoreEx(bean));
        }
        CapabilityDebugHelper.markCurrTime("FtpConn-readHeadBegin");
        setNano(bean.getNano());
        setSocketTimeOut();
        try {
            int len = readInt();
            byte[] head = this.readNum(len);
            CapabilityDebugHelper.markCurrTime("FtpConn-readHead-bytes");
            String headStr;
            String type = new String(head, 0, 3, CHARSET_NAME_UTF8);//NOSONAR
            if (FileMsgType.RNAM.equals(type)) {
                // 处理来自于ESB的请求
                String req = new String(head, CHARSET_NAME_UTF8);
                log.debug("nano:{}#收到的请求报文为[{}]", nano, req);
                byte[] orgi = new byte[100];//NOSONAR
                byte[] newn = new byte[100];//NOSONAR
                System.arraycopy(head, 3, orgi, 0, 100);//NOSONAR
                System.arraycopy(head, 103, newn, 0, 100);//NOSONAR

                bean.setFileMsgFlag(type);
                bean.setClientFileName(new String(newn, CHARSET_NAME_UTF8).trim());
                bean.setFileName(new String(orgi, CHARSET_NAME_UTF8).trim());
            } else {
                // 针对AS400进行转码控制
                if (head[0] == '{') {//json
                    headStr = new String(head, CHARSET_NAME_UTF8);
                    FileMsgBeanHelper.convertXmlToHead(bean, headStr);
                } else if (head[0] == '<') {//xml
                    headStr = new String(head, CHARSET_NAME_UTF8);
                    FileMsgBeanHelper.convertXmlToHead0(bean, headStr);
                } else {
                    bean.setEbcdicFlag(true);
                    headStr = new String(head, CHARSET_NAME_CP935);
                    FileMsgBeanHelper.convertXmlToHead(bean, headStr);
                }
            }
        } catch (FtpException e) {
            e.setNano(nano);
            throw e;
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.SOCKET_CONNECT_ERROR, nano, e);
        }
        bean.setClientIp(socket.getInetAddress().getHostAddress());
        bean.setServerIp(socket.getLocalAddress().getHostAddress());
        if (CapabilityDebugHelper.isOutBean()) {
            CapabilityDebugHelper.markCurrTime("after-readHead-FileMsgBeanXML:" + FileMsgBeanHelper.toStringIgnoreEx(bean));
        }
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
            socket.setSoTimeout(timeOut);
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_TIME_OUT_ERROR, nano, e);
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
            throw new FtpException(FtpErrCode.SOCKET_TIME_OUT_ERROR, nano, e);
        }
    }

    public void setTcpNoDelay(boolean on) throws FtpException {
        try {
            socket.setTcpNoDelay(on);
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_ERROR, nano, e);
        }
    }

    public void setTrafficClass(int tc) throws FtpException {
        try {
            socket.setTrafficClass(tc);
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_ERROR, nano, e);
        }
    }

    /**
     * 写入文件的某一个分片，根据bean中的isScrtFlag确定是否进行数据的加密
     *
     * @param bean 文件消息对象
     * @throws FtpException
     */
    public void writeFileContent(FileMsgBean bean) throws FtpException {
        setNano(bean.getNano());
        try {
            byte[] scrtCont = null;
            int len = 0;
            if (BooleanTool.toBoolean(bean.isScrtFlag())) {
                byte[] cont = new byte[bean.getContLen()];
                System.arraycopy(bean.getFileCont(), 0, cont, 0, cont.length);
                scrtCont = Des.decrypt3DES(cont, bean.getDesKey());
                len = scrtCont.length;
            } else {
                scrtCont = bean.getFileCont();
                len = bean.getContLen();
            }
            out.writeInt(len);
            out.write(scrtCont, 0, len);
            out.flush();
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, nano, e);
        }
    }

    /**
     * 读取文件的某一个分片，根据bean中的isScrtFlag确定是否进行数据的加密
     *
     * @param bean 文件消息对象，输出结果存放在该对象中返回
     * @throws FtpException
     */
    public void readCont(FileMsgBean bean) throws FtpException {
        setNano(bean.getNano());
        this.setSocketTimeOut();
        byte[] cont = null;
        int len = this.readInt();
        byte[] scrtCont = this.readNum(len);
        if (BooleanTool.toBoolean(bean.isScrtFlag()))
            cont = Des.decrypt3DES(scrtCont, bean.getDesKey());
        else
            cont = scrtCont;
        bean.setFileCont(cont);
        bean.setContLen(cont.length);
        log.debug("nano:{}#readContEnd", nano);
    }

    public void skipCont(FileMsgBean bean) throws FtpException {
        setNano(bean.getNano());
        this.setSocketTimeOut();
        int len = readInt();
        try {
            in.skipBytes(len);
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, nano, e);
        }
        log.debug("nano:{}#skipContEnd", nano);
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
            if ((ch1 | ch2 | ch3 | ch4) < 0) throw new EOFException();
            return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4)); //NOSONAR
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, nano, e);
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
        if (num > UnitCons.ONE_MB) {
            log.error("请求数据长度过长，length=[{}]", num);
            throw new FtpException(FtpErrCode.READ_REQ_LENGTH_ERROR, nano);
        }

        byte[] data = new byte[num];
        int offset = 0;
        int count = -1;
        try {
            while (-1 != (count = in.read(data, offset, num - offset))) {
                offset += count;
                if (offset == num) break;
            }
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_ERROR, nano, e);
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, nano, e);
        }
        return data;
    }

    public void writeHeadAndContent(FileMsgBean bean) throws FtpException {
        setNano(bean.getNano());
        try {
            byte[] head = null;
            // 针对AS400进行转换控制
            if (BooleanTool.toBoolean(bean.isEbcdicFlag())) {
                head = FileMsgBeanHelper.convertHeadToXml(bean).getBytes(CHARSET_NAME_CP935);
            } else {
                head = FileMsgBeanHelper.convertHeadToXml(bean).getBytes(CHARSET_NAME_UTF8);
            }
            CapabilityDebugHelper.markCurrTime("writeHeadAndContent-OutWriteBegin");
            out.writeInt(head.length);
            out.write(head);
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_ERROR, nano, e);
        } catch (IOException e) {
            log.error("nano:{}#XML文件格式错误", nano, e);
            throw new FtpException(FtpErrCode.HEAD_XML_ERROR, nano, e);
        }
        try {
            byte[] scrtCont = null;
            int len = 0;
            if (BooleanTool.toBoolean(bean.isScrtFlag())) {
                byte[] cont = new byte[bean.getContLen()];
                System.arraycopy(bean.getFileCont(), 0, cont, 0, cont.length);
                scrtCont = Des.decrypt3DES(cont, bean.getDesKey());
                len = scrtCont.length;
            } else {
                scrtCont = bean.getFileCont();
                len = bean.getContLen();
            }
            out.writeInt(len);
            out.write(scrtCont, 0, len);
            out.flush();
        } catch (SocketException e) {
            throw new FtpException(FtpErrCode.SOCKET_ERROR, nano, e);
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, nano, e);
        }
        CapabilityDebugHelper.markCurrTime("writeHeadAndContent-OutWriteEnd");
    }

    public void writeAck() throws FtpException {
        try {
            setTcpNoDelay(true);
            out.write(1);
            out.flush();
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.WRITE_ACK_ERROR, nano, e);
        }
    }

    public int readAck() throws FtpException {
        try {
            setSocketTimeOut();
            return in.read();
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.READ_ACK_ERROR, nano, e);
        }
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public Long getNano() {
        return nano;
    }

    public void setNano(Long nano) {
        this.nano = nano;
    }
}
