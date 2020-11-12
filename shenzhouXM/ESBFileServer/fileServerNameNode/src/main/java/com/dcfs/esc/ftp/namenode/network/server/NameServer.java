package com.dcfs.esc.ftp.namenode.network.server;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.utils.InetAddressUtil;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;

/**
 * 目录服务
 * Server that accept the path of a file an echo back its content.
 */
public final class NameServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(NameServer.class);
    private static final int BACKLOG = 100;
    private String ip;
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    public NameServer(String ip, int port) throws FtpException {
        this.ip = ip;
        this.port = port;
        try {
            initAndBind();
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.IO_EXCEPTION, e);
        } catch (CertificateException e) {
            throw new FtpException(FtpErrCode.CERTIFICATE_EXCEPTION, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FtpException(FtpErrCode.INTERRUPTED_EXCEPTION, e);
        }
    }

    @Override
    public void run() {
        try {
            run0();
        } catch (Exception e) {
            log.error("start NameServer err", e);
            throw new NestedRuntimeException(e);
        }
    }

    private void initAndBind() throws SSLException, CertificateException, InterruptedException, UnknownHostException {
        log.info("starting NameServer#ip:{},port:{}", ip, port);
        // Configure SSL.
        final SslContext sslCtx;
        final boolean ssl = System.getProperty("ssl") != null;
        if (ssl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        boolean bindSucc = false;
        // Configure the server.
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, BACKLOG)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NameServerChannelInitializer(sslCtx));

            // Start the server.
            InetAddress addr = InetAddress.getByAddress(InetAddressUtil.ip4ToBytes(ip));
            InetSocketAddress address = new InetSocketAddress(addr, port);
            channelFuture = serverBootstrap.bind(address).sync();
            bindSucc = true;
            log.info("start NameServer successfully#ip:{},port:{}", ip, port);
        } finally {
            if (!bindSucc) {
                // Shut down all event loops to terminate all threads.
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }

    private void run0() throws InterruptedException {
        try {
            // Wait until the server socket is closed.
            channelFuture.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
