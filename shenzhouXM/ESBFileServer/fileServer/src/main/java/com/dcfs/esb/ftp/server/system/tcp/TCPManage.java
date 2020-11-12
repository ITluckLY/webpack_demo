package com.dcfs.esb.ftp.server.system.tcp;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.system.BaseProtocol;
import com.dcfs.esb.ftp.server.system.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class TCPManage extends BaseProtocol {
    private static final Logger log = LoggerFactory.getLogger(TCPManage.class);
    private static int timeout = 120000;
    private SystemInfo systemInfo;
    private String localFileName;
    private String remoteFileName;

    public TCPManage(SystemInfo systemInfo, String localFileName, String remoteFileName) {
        this.systemInfo = systemInfo;
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
    }

    /**
     * 文件上传
     *
     * @throws FtpException
     */

    @Override
    public boolean uploadBySync() throws FtpException {
        String reqDatas = addLen("2" + "|" + localFileName + "|" + remoteFileName);
        if (log.isDebugEnabled()) {
            log.debug("Tuxedo FTP请求报文：" + reqDatas);
        }
        String rspDatas = null;
        try {
            rspDatas = doComm(reqDatas);
        } catch (IOException e) {
            log.error("", e);
            throw new FtpException(FtpErrCode.FILE_UP_ERROR, new Exception("Tuxedo FTP上传文件失败"));
        }
        if (log.isDebugEnabled()) {
            log.debug("Tuxedo FTP响应报文：" + rspDatas);
        }
        if (rspDatas == null || "F".equalsIgnoreCase(rspDatas)) {
            throw new FtpException(FtpErrCode.FILE_UP_ERROR, new Exception("Tuxedo FTP上传文件失败"));
        }
        return true;
    }

    @Override
    public boolean uploadByAsync() throws FtpException {
        return uploadBySync();
    }

    /**
     * 文件下载
     */
    @Override
    public boolean download() throws FtpException {
        //先创建文件路径
        int index = localFileName.lastIndexOf('/');
        if (index > 0) {
            File file = new File(localFileName.substring(0, index));
            if (!file.exists())
                file.mkdirs();
        }
        String reqDatas = addLen("1" + "|" + localFileName + "|" + remoteFileName);
        if (log.isDebugEnabled()) {
            log.debug("Tuxedo FTP请求报文：" + reqDatas);
        }
        String rspDatas = null;
        try {
            rspDatas = doComm(reqDatas);
        } catch (IOException e) {
            log.error("", e);
            throw new FtpException(FtpErrCode.FILE_DOWN_ERROR, new Exception("Tuxedo FTP下载文件失败"));
        }
        if (log.isDebugEnabled()) {
            log.debug("Tuxedo FTP响应报文：" + rspDatas);
        }
        if (rspDatas == null || "F".equalsIgnoreCase(rspDatas)) {
            throw new FtpException(FtpErrCode.FILE_DOWN_ERROR, new Exception("Tuxedo FTP下载文件失败"));
        }
        return true;
    }

    private String addLen(String data) {
        String len = Integer.toString(data.getBytes().length);
        StringBuilder dataBuilder = new StringBuilder(len + data);
        for (int i = 0; i < 6 - len.length(); i++) {
            dataBuilder.insert(0, "0");
        }
        return dataBuilder.toString();
    }

    private String doComm(String reqDatas) throws IOException {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        String rspDatas = "F";
        try {
            String ip = systemInfo.getIp();
            int port = systemInfo.getPort();
            socket = new Socket(ip, port);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            socket.setSoTimeout(timeout);

            byte[] reqBytes = reqDatas.getBytes();
            os = new BufferedOutputStream(socket.getOutputStream());
            os.write(reqBytes);
            os.flush();

            is = socket.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[10];
            int availableLength = 0;
            while ((availableLength = is.read(buffer)) != -1) {
                baos.write(buffer, 0, availableLength);
            }
            baos.close();
            byte[] rspBytes = baos.toByteArray();
            rspDatas = new String(rspBytes);
        } finally {
            if (os != null) { // 关闭输出流
                try {
                    os.close();
                } catch (IOException e) {
                    if (log.isErrorEnabled())
                        log.error("关闭输出流时捕获IO错误，抛出通讯接出异常！", e);
                }
            }
            if (is != null) { // 关闭输入流
                try {
                    is.close();
                } catch (IOException e) {
                    if (log.isErrorEnabled())
                        log.error("关闭输入流时捕获IO错误，抛出通讯接出异常！", e);
                }
            }
            if (socket != null) { // 关闭Socket套接字
                try {
                    socket.close();
                } catch (IOException e) {
                    if (log.isErrorEnabled())
                        log.error("关闭Socket连接时捕获IO错误，抛出通讯接出异常！", e);
                }
            }
        }
        return rspDatas;
    }
}
