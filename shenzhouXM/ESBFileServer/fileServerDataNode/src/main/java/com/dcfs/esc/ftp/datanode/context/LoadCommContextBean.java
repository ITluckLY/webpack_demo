package com.dcfs.esc.ftp.datanode.context;

import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.scrt.Md5Alg;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esc.ftp.datanode.file.EscFile;
import com.dcfs.esc.ftp.datanode.process.ProcessExecutor;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;

/**
 * Created by mocg on 2017/6/3.
 */
public class LoadCommContextBean extends ContextBean {
    private static final Logger log = LoggerFactory.getLogger(LoadCommContextBean.class);
    /* 服务端上的文件的绝对路径 */
    protected String absFilePath;
    protected RandomAccessFile raf;
    protected long fileSize;
    protected long position;
    protected int readLen;
    protected boolean sleep = false;
    /*交易码 */
    protected String tranCode;
    /*目标系统名称 ref system.xml#systems.system.name*/
    protected String targetSysname;
    /*目标系统文件路径*/
    protected String targetFileName;
    protected long fileVersion;
    protected boolean pack;//是否是打包

    protected ProcessExecutor processExecutor;  //这个啥意思？？？  好像是流程信息
    protected ProcessHandlerContext processHandlerContext;
    protected long sumContLen;

    protected EscFile escFile;

    //对接 暂时使用 后面优化去掉
    protected EsbFile esbFile;
    protected FileMsgBean fileMsgBean;
    protected CachedContext esbContext;
    /* 文件的Md5计算对象 */
    protected Md5Alg md5Alm = new Md5Alg();

    @Override
    public void clean() {
        //回收令牌资源
        if (esbContext != null) {
            try {
                SysContent sysContent = SysContent.getInstance();
                sysContent.decrementTaskCount(esbContext);
                sysContent.decrementTaskPriorityTokenCount(esbContext);
                sysContent.minusNetworkSpeed(esbContext);
            } catch (Exception e) {
                log.error("nano:{}#回收令牌资源出错", nano, e);
            }
        }

        if (processExecutor != null) {
            try {
                processExecutor.clean(processHandlerContext);
            } catch (Exception e) {
                log.error("nano:{}#释放回收processExecutor资源出错", nano, e);
            }
        }
        if (processHandlerContext != null) {
            try {
                processHandlerContext.clean();
            } catch (Exception e) {
                log.error("nano:{}#释放回收processHandlerContext资源出错", nano, e);
            }
        }

        if (raf != null) {
            try {
                raf.close();
            } catch (Exception e) {
                log.error("nano:{}#释放回收raf资源出错", nano, e);
            }
        }
        if (esbFile != null) {
            try {
                esbFile.close();
            } catch (Exception e) {
                log.error("nano:{}#释放回收esbFile资源出错", nano, e);
            }
        }
        if (escFile != null) {
            try {
                escFile.close();
            } catch (Exception e) {
                log.error("nano:{}#释放回收escFile资源出错", nano, e);
            }
        }

        processExecutor = null;
        processHandlerContext = null;
        esbFile = null;
        escFile = null;
        raf = null;
        md5Alm = null;
    }

    public void sumContLen(long len) {
        sumContLen += len;
    }

    //getter setter


    public long getSumContLen() {
        return sumContLen;
    }

    public void setSumContLen(long sumContLen) {
        this.sumContLen = sumContLen;
    }

    public int getReadLen() {
        return readLen;
    }

    public void setReadLen(int readLen) {
        this.readLen = readLen;
    }

    public ProcessHandlerContext getProcessHandlerContext() {
        return processHandlerContext;
    }

    public void setProcessHandlerContext(ProcessHandlerContext processHandlerContext) {
        this.processHandlerContext = processHandlerContext;
    }

    public String getAbsFilePath() {
        return absFilePath;
    }

    public void setAbsFilePath(String absFilePath) {
        this.absFilePath = absFilePath;
    }

    public EscFile getEscFile() {
        return escFile;
    }

    public void setEscFile(EscFile escFile) {
        this.escFile = escFile;
    }

    public EsbFile getEsbFile() {
        return esbFile;
    }

    public void setEsbFile(EsbFile esbFile) {
        this.esbFile = esbFile;
    }

    public FileMsgBean getFileMsgBean() {
        return fileMsgBean;
    }

    public void setFileMsgBean(FileMsgBean fileMsgBean) {
        this.fileMsgBean = fileMsgBean;
    }

    public CachedContext getEsbContext() {
        return esbContext;
    }

    public void setEsbContext(CachedContext esbContext) {
        this.esbContext = esbContext;
    }

    public ProcessExecutor getProcessExecutor() {
        return processExecutor;
    }

    public void setProcessExecutor(ProcessExecutor processExecutor) {
        this.processExecutor = processExecutor;
    }

    public RandomAccessFile getRaf() {
        return raf;
    }

    public void setRaf(RandomAccessFile raf) {
        this.raf = raf;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getTargetSysname() {
        return targetSysname;
    }

    public void setTargetSysname(String targetSysname) {
        this.targetSysname = targetSysname;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public Md5Alg getMd5Alm() {
        return md5Alm;
    }

    public void setMd5Alm(Md5Alg md5Alm) {
        this.md5Alm = md5Alm;
    }

    public long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(long fileVersion) {
        this.fileVersion = fileVersion;
    }

    public boolean isPack() {
        return pack;
    }

    public void setPack(boolean pack) {
        this.pack = pack;
    }
}
