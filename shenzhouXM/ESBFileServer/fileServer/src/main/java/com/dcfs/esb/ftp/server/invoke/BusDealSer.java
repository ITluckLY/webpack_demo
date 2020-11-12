package com.dcfs.esb.ftp.server.invoke;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.utils.InetAddressUtil;
import com.dcfs.esc.ftp.comm.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BusDealSer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(BusDealSer.class);
    private ServerSocket server;
    private static final int TIME_OUT = 5000;
    private boolean running = true;

    public BusDealSer(int port) throws FtpException {
        try {
            server = new ServerSocket(port);
        } catch (Exception e) {
            FtpException fe = new FtpException(FtpErrCode.SOCKET_ERROR);
            log.error("BusDealSer启动Socket失败#[:{}]", port, fe);
            throw fe;
        }
        log.info("BusDealSer#[{}] is Listening...", port);
    }

    public BusDealSer(String ip, int port) throws FtpException {
        try {
            server = new ServerSocket();
            //InetAddress addr = InetAddress.getByName(ip);//NOSONAR
            InetAddress addr = InetAddress.getByAddress(InetAddressUtil.ip4ToBytes(ip));
            InetSocketAddress address = new InetSocketAddress(addr, port);
            server.bind(address);
        } catch (Exception e) {
            FtpException fe = new FtpException(FtpErrCode.SOCKET_ERROR);
            log.error("BusDealSer启动Socket失败#[{}:{}]", ip, port, fe);
            throw fe;
        }
        log.info("BusDealSer#[{}:{}] is Listening...", ip, port);
    }

    @Override
    public void run() {
        while (running) {//NOSONAR
            Socket socket = null;
            try {
                socket = server.accept();
                socket.setKeepAlive(false);
                socket.setSoTimeout(TIME_OUT);
                BusDeal bd = new BusDeal(socket);
                new Thread(bd).start();
            } catch (Exception e) {
                IOUtil.closeQuietly(socket);
                log.error("BusDealSer服务出错", e);
                MessDealTool.sendBackMes(ResultDtoTool.buildError("服务出错"), socket);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
