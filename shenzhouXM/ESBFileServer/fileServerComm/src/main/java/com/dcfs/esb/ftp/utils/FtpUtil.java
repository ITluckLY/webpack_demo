package com.dcfs.esb.ftp.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by cgmo on 2015/7/9.
 */
public class FtpUtil {
    private static final Logger log = LoggerFactory.getLogger(FtpUtil.class);
    private static final int BUFFER_SIZE = 16 * 1024;
    private static final String LOCAL_CHARSET = "GB18030";

    private FtpUtil() {
    }

    public static FTPClient getFtpClient(String host, int port, String username, String password, String baseWorkDir) throws IOException {
        return getFtpClient(host, port, username, password, baseWorkDir, BUFFER_SIZE, LOCAL_CHARSET, null);
    }

    public static FTPClient getFtpClient(String host, int port, String username, String password, String baseWorkDir, Class logClass) throws IOException {
        return getFtpClient(host, port, username, password, baseWorkDir, BUFFER_SIZE, LOCAL_CHARSET, logClass);
    }

    public static FTPClient getFtpClient(String host, int port, String username, String password, String baseWorkDir, int bufferSize, String localCharset) throws IOException {
        return getFtpClient(host, port, username, password, baseWorkDir, bufferSize, localCharset, null);
    }

    public static FTPSClient getFtpsClient(String host, int port, String username, String password, String baseWorkDir, Class logClass) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        return getFtpsClient(host, port, username, password, baseWorkDir, BUFFER_SIZE, LOCAL_CHARSET, logClass);
    }


    public static FTPSClient getFtpsClient(String host, int port, String username, String password, String baseWorkDir, int bufferSize, String localCharset, Class logClass) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Logger log = getLog(logClass);
        log.debug("开始连接ftps...");
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
        FTPSClient ftpsClient = new FTPSClient(true, sc);
        ftpsClient.connect(host, port);
        // 检测服务端是否支持UTF-8编码，如果支持就用UTF-8编码，否则就使用本地编码GB18030
        if (FTPReply.isPositiveCompletion(ftpsClient.sendCommand("OPTS UTF8", "ON"))) {
            ftpsClient.setControlEncoding("UTF-8");
        } else {
            ftpsClient.setControlEncoding(localCharset);
        }
        boolean isLogined = false;
        int retryCount = 5;
        while (!isLogined && --retryCount >= 0) {
            isLogined = ftpsClient.login(username, password);
            log.debug("登录ftp结果是{},index:{}", isLogined, retryCount);
        }
        if (!isLogined) throw new IOException("login ftp failed.");

        ftpsClient.setBufferSize(bufferSize);
        ftpsClient.enterLocalPassiveMode();
        ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpsClient.setControlKeepAliveTimeout(60);
        try {
            int replyCode = ftpsClient.sendCommand("site", "umask 111");
            log.info("更改权限返回的结果值:{}#site umask 111", replyCode);
        } catch (IOException e) {
            log.error("更改权限失败", e);
        }
        log.debug("连接ftp成功");
        boolean isChangeSucc = ftpsClient.changeWorkingDirectory(baseWorkDir);
        log.debug("更改目录:{}", isChangeSucc);
        return ftpsClient;
    }

    public static FTPClient getFtpClient(String host, int port, String username, String password, String baseWorkDir, int bufferSize, String localCharset, Class logClass) throws IOException {
        Logger log = getLog(logClass);
        log.debug("开始连接ftp...");
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host, port);
        // 检测服务器是否支持UTF-8编码，如果支持就用UTF-8编码，否则就使用本地编码GB18030
        if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
            ftpClient.setControlEncoding("UTF-8");
        } else {
            ftpClient.setControlEncoding(localCharset);
        }
        boolean isLogined = false;
        int retryCount = 5;
        while (!isLogined && --retryCount >= 0) {
            isLogined = ftpClient.login(username, password);
            log.debug("登录ftp结果是{},index:{}", isLogined, retryCount);
        }
        if (!isLogined) throw new IOException("login ftp failed.");

        ftpClient.setBufferSize(bufferSize);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlKeepAliveTimeout(60);
        try {
            int replyCode = ftpClient.sendCommand("site", "umask 111");
            log.info("更改权限返回的结果值:{}#site umask 111", replyCode);
        } catch (IOException e) {
            log.error("更改权限失败", e);
        }
        log.debug("连接ftp成功");
        boolean isChangeSucc = ftpClient.changeWorkingDirectory(baseWorkDir);
        log.debug("更改目录:{}", isChangeSucc);
        return ftpClient;
    }

    public static boolean changeWorkingDirectory(FTPClient ftpClient, String baseWorkDir) throws IOException {
        return ftpClient.changeWorkingDirectory(baseWorkDir);
    }

    public static boolean uploadFile(FTPClient ftpClient, String fileName, InputStream input) {
        return uploadFile(ftpClient, fileName, input, null);
    }

    public static boolean uploadFile(FTPClient ftpClient, String fileName, InputStream input, Class logClass) {
        Logger log = getLog(logClass);
        log.info("开始上传文件到ftp...");
        boolean success = false;
        try {
            success = ftpClient.storeFile(fileName, input);
            input.close();
            log.info("上传完成#{}-{}", success, ftpClient.getReplyCode());
        } catch (IOException e) {
            log.error("上传失败", e);
        }
        return success;
    }

    public static boolean download(FTPClient ftpClient, String remote, OutputStream localOs, Class logClass) {
        Logger log = getLog(logClass);
        log.info("开始下载文件...");
        boolean success = false;
        try {
            success = ftpClient.retrieveFile(remote, localOs);
            localOs.close();
            log.info("下载完成#{}-{}", success, ftpClient.getReplyCode());
        } catch (IOException e) {
            log.error("下载失败", e);
        }
        return success;
    }

    /**
     * 关闭FTP客户端连接
     *
     * @param ftpClient
     */
    public static void close(FTPClient ftpClient) {
        close(ftpClient, null);
    }

    /**
     * 关闭FTP客户端连接
     *
     * @param ftpClient
     */
    public static void close(FTPClient ftpClient, Class logClass) {
        if (ftpClient == null) return;
        Logger log = getLog(logClass);
        if (ftpClient.isConnected()) {
            try {
                boolean isLogOut = ftpClient.logout();
                if (isLogOut) log.debug("关闭ftp连接成功");
                else log.debug("关闭ftp连接失败");
            } catch (IOException e) {
                log.warn("关闭FTP服务异常", e);
            } finally {
                try {
                    ftpClient.disconnect();
                    log.debug("断开ftp连接");
                } catch (IOException e) {
                    log.error("关闭服务连接异常", e);
                }
            }
        }
    }

    private static Logger getLog(Class logClass) {
        return logClass == null ? FtpUtil.log : LoggerFactory.getLogger(logClass);
    }
}
