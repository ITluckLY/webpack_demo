package com.dcfs.esb.ftp.impls.component;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.helper.FileVersionHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.cmd.DoSend;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.helper.NetworkSpeedCtrlHelper;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.ThreadSleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2016/10/21.
 */
public class DownloadMountFileComponent implements IFileComponent {
    private static final Logger log = LoggerFactory.getLogger(DownloadMountFileComponent.class);
    public static boolean forTest = false;//NOSONAR
    public static int sleepSeconds = 5;//NOSONAR
    private CachedContext context;
    private FileMsgBean bean;
    private FtpConnector conn;
    private EsbFile esbFile;
    private long nano;

    @Override
    public EsbFile create(CachedContext context, FileMsgBean bean, FtpConnector conn) throws FtpException {
        this.context = context;
        this.bean = bean;
        this.conn = conn;
        nano = context.getCxtBean().getNano();
        // 文件下载处理
        EsbFile esbFile;//NOSONAR
        log.debug("nano:{}#下载文件：{}", nano, bean.getFileName());
        esbFile = new EsbFile(bean.getFileName(), EsbFile.MOUNT);
        this.esbFile = esbFile;
        context.getCxtBean().setEsbFile(esbFile);
        return esbFile;
    }

    @Override
    public void preProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        if (file != null) file.openForRead(bean.getOffset());
    }

    @Override
    public void process(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        long fileVersion = FileVersionHelper.getFileVersion(file.getRealFileName());
        context.getCxtBean().setFileVersion(fileVersion);
        download();
    }

    @Override
    public void afterProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) {
        //empty
    }

    @Override
    public void finish(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) {
        SysContent.getInstance().minusNetworkSpeed(context);
        if (file != null) {
            try {
                file.close();
            } catch (Exception e) {
                log.error("nano:{}#资源回收失败", nano, e);
            }
        }
    }

    private void download() throws FtpException {
        // 在这里循环读取文件分片大小，这可以在设置当前渠道睡眠以及获取当前时间速度
        //实时保护,第一次和每隔distance次做一次网速计算
        long contLenSum = 0;
        long maxFileSize = context.getCxtBean().getMaxFileSize();
        boolean first = true;
        SysContent sysContent = SysContent.getInstance();
        long index = 0;
        long index2 = 0;
        final int distance = 5;
        long startTime = System.currentTimeMillis();
        long contLenSum2 = 0;//间隔内数据总大小
        while (true) {
            if (contLenSum >= maxFileSize) { //超过最大文件大小
                throw new FtpException(FtpErrCode.OUT_OF_SIZE_ERROR);
            }
            //实时保护
            NetworkSpeedCtrlHelper.sleep(context);
            if (index - index2 == distance) sysContent.minusNetworkSpeed(context);

            // 读取请求的头信息，第一次readHead()在上面已经做了
            if (!first) conn.readHead(bean);
            // 根据头信息进行相关的处理
            downloadProcess(context, conn, bean, esbFile);
            contLenSum += bean.getContLen();
            contLenSum2 += bean.getContLen();

            //第一次和每隔distance次做一次网速计算
            if (index == 0 || index - index2 == distance) {
                long now = System.currentTimeMillis();
                long usedTime = now - startTime + 1;
                long speed = contLenSum2 * 1000 / usedTime;//byte/s //NOSONAR
                //当前请求结束后会自动减去speed
                sysContent.addNetworkSpeed(context, speed);
                index2 = index;
                startTime = now;
                contLenSum2 = 0;
            }

            //for test
            if (forTest) ThreadSleepUtil.sleepSecondIngoreEx(sleepSeconds);
            // 最后一个分片，则退出
            if (BooleanTool.toBoolean(bean.isLastPiece())) {
                log.debug("nano:{}#处理到最后一个分片，结束处理", nano);
                break;
            }
            first = false;
            index++;
        }
    }

    private void downloadProcess(CachedContext context, FtpConnector conn, FileMsgBean bean, EsbFile file) throws FtpException {//NOSONAR
        DoSend send = new DoSend();
        send.doCommand(bean, file);
        conn.writeHead(bean, false);
        conn.writeFileContent(bean);
        if (BooleanTool.toBoolean(bean.isLastPiece())) file.close();

    }
}
