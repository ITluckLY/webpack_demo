package com.dcfs.esb.ftp.impls.filetail;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 字节读取
 * Created by mocg on 2016/8/26.
 */
public class FileTailer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(FileTailer.class);
    private static final long DEF_SLEEP_INTERVAL = 9000;//单位:毫秒
    private long sleepInterval = DEF_SLEEP_INTERVAL;
    private volatile boolean tailing = true;
    private volatile boolean continueReadFile = true;
    private boolean tailFinish;
    private boolean waitingFinish = false;//是否等待外部任务完成
    private int buffLen = 524288;//NOSONAR
    private volatile File file;
    private volatile RandomAccessFile rfile;
    private volatile long filePointer = 0;
    private long maxFilePointer = Long.MAX_VALUE;
    private long lastFilePointer = -1;
    private boolean stop4ReplaceFile = false;
    private int sameFilePointerTime;
    private int maxSameFilePointerTime = 500;//NOSONAR
    private int maxWaitingFinishSleepTime = 500;//NOSONAR
    private long nano;
    private boolean haveErr;//业务出错
    private String flowNo;

    private Set<TailListener> listeners = new HashSet<>();
    private FinishListener finishListener;
    private List<RandomAccessFile> rafList = new LinkedList<>();

    public FileTailer(File file, long sleepInterval, boolean startAtBeginning) {
        this.file = file;
        this.sleepInterval = sleepInterval;
        filePointer = startAtBeginning ? 0 : file.length();
        //setName("FileTailer");//NOSONAR
    }

    protected void fireMoreTail(byte[] bytes, int start, int len) {
        for (TailListener l : listeners) {
            l.fireMoreTail(bytes, start, len);
        }
    }

    public void run() {//NOSONAR
        log.debug("nano:{}#flowNo:{}#FileTailer starting...", nano, flowNo);
        byte[] buff = new byte[buffLen];
        try {
            rfile = newRandomAccessFile(file, "r");
            while (tailing) {
                long fileLength = file.length();
                if (fileLength < filePointer) {
                    closeRafListQuietly();
                    rfile = newRandomAccessFile(file, "r");
                    filePointer = 0;
                }
                if (fileLength > filePointer) {
                    seek(filePointer);
                    int readLen;
                    while (continueReadFile && !haveErr && (readLen = read(buff)) > 0) {
                        filePointer += readLen;
                        fireMoreTail(buff, 0, readLen);
                    }
                }
                //读取到指定的位置则中止读取
                if (filePointer == maxFilePointer) {
                    log.debug("nano:{}#flowNo:{}#读取到指定的位置则中止读取#filePointer:{},maxFilePointer:{}#{}"
                            , nano, flowNo, filePointer, maxFilePointer, file.getPath());
                    break;
                }
                //读取到超过指定的位置则中止读取
                if (filePointer > maxFilePointer) {
                    log.debug("nano:{}#flowNo:{}#读取到超过指定的位置则中止读取#filePointer:{},maxFilePointer:{}#{}"
                            , nano, flowNo, filePointer, maxFilePointer, file.getPath());
                    break;
                }
                //超过一定次数没有新的输入则中止读取
                if (lastFilePointer != filePointer) {
                    sameFilePointerTime = 0;
                    lastFilePointer = filePointer;
                } else sameFilePointerTime++;
                if (tailing && sameFilePointerTime >= maxSameFilePointerTime) {
                    log.debug("nano:{}#flowNo:{}#超过一定次数没有新的输入中止读取#sameFilePointerTime:{}, maxSameFilePointerTime:{}"
                            , nano, flowNo, sameFilePointerTime, maxSameFilePointerTime);
                    break;
                }

                log.debug("nano:{}#flowNo:{}#FileTailer-tail#filePointer:{},sameFilePointerTime:{}#path:{}"
                        , nano, flowNo, filePointer, sameFilePointerTime, file.getPath());
                if (tailing) Thread.sleep(sleepInterval);
            }
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#FileTailer err", nano, flowNo, e);
        } finally {
            log.debug("nano:{}#flowNo:{}#FileTailer finally", nano, flowNo);
            closeRafListQuietly();
            if (!stop4ReplaceFile) finish();
        }
        log.debug("nano:{}#flowNo:{}#FileTailer finish", nano, flowNo);
    }

    private RandomAccessFile newRandomAccessFile(File file, String mode) throws FileNotFoundException {
        log.debug("nano:{}#flowNo:{}#newRandomAccessFile:{},mode:{}", nano, flowNo, file.getPath(), mode);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, mode);
        rafList.add(randomAccessFile);
        return randomAccessFile;
    }


    private synchronized void seek(long filePointer) throws IOException {
        rfile.seek(filePointer);
    }

    private synchronized int read(byte[] buff) throws IOException {
        return rfile.read(buff);
    }

    public void stopTailing() {
        tailing = false;
        continueReadFile = false;
        maxSameFilePointerTime = 0;
        stop4ReplaceFile = false;
        waitingFinish = false;
        log.debug("nano:{}#flowNo:{}#tail stop", nano, flowNo);
    }

    public void stopTailingAndWait() {
        continueReadFile = true;
        tailing = true;
        maxSameFilePointerTime = 5;
        stop4ReplaceFile = false;
        waitingFinish = false;
        log.debug("nano:{}#flowNo:{}#tail stop and wait a minute", nano, flowNo);
    }

    public void stopTailing4ReplaceFile() {
        tailing = false;
        continueReadFile = false;
        stop4ReplaceFile = true;
        waitingFinish = false;
        log.debug("nano:{}#flowNo:{}#tail stop", nano, flowNo);
    }

    public void reopenFile(File file) throws IOException {
        if (tailFinish) return;
        stop4ReplaceFile = true;
        synchronized (this) {
            closeRafListQuietly();
            this.file = file;
            rfile = newRandomAccessFile(file, "r");
        }
        stop4ReplaceFile = false;
        sameFilePointerTime = 0;
    }

    public void closeFileAndPause() throws IOException {
        stop4ReplaceFile = true;
        if (rfile != null) {
            synchronized (this) {
                rfile.close();
            }
        }
    }

    private void finish() {
        tailFinish = true;
        if (finishListener != null) {
            int count = 0;
            while (waitingFinish) {
                if (++count >= maxWaitingFinishSleepTime) break;
                try {
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("nano:{}#flowNo:{}#waitingFinish sleep Interrupted", nano, flowNo, e);
                }
            }
            finishListener.finish();
        }
    }

    public void addTailListener(TailListener l) {
        listeners.add(l);
    }

    public void removeTailListener(TailListener l) {
        listeners.remove(l);
    }

    public String state() {
        return String.format("nano:%s#flowNo:%s#tailing:%s,stop4ReplaceFile:%s,sleepInterval:%s,filePointer:%s"
                , nano, flowNo, tailing, stop4ReplaceFile, sleepInterval, filePointer);
    }

    private void closeRafListQuietly() {
        closeQuietly(rfile);
        if (log.isDebugEnabled()) log.debug("nano:{}#flowNo:{}#rafList.size:{}", nano, flowNo, rafList.size());
        for (RandomAccessFile raf : rafList) {
            closeQuietly(raf);
        }
    }

    private void closeQuietly(RandomAccessFile rfile) {
        if (rfile != null) {
            log.debug("nano:{}#flowNo:{}#关闭rfile文件{}...", nano, flowNo, file.getPath());
            try {
                rfile.close();
            } catch (IOException e) {
                log.error("nano:{}#flowNo:{}#关闭rfile文件出错#{}", nano, flowNo, file.getPath(), e);
                IOUtils.closeQuietly(rfile);
            }
        }
    }

    //getter setter


    public boolean isHaveErr() {
        return haveErr;
    }

    public void setHaveErr(boolean haveErr) {
        this.haveErr = haveErr;
    }

    public boolean isTailFinish() {
        return tailFinish;
    }

    public void setTailFinish(boolean tailFinish) {
        this.tailFinish = tailFinish;
    }

    public boolean isTailing() {
        return tailing;
    }

    public void setTailing(boolean tailing) {
        this.tailing = tailing;
    }

    public long getFilePointer() {
        return filePointer;
    }

    public void setFilePointer(long filePointer) {
        this.filePointer = filePointer;
    }

    public long getMaxFilePointer() {
        return maxFilePointer;
    }

    public void setMaxFilePointer(long maxFilePointer) {
        this.maxFilePointer = maxFilePointer;
    }

    public FinishListener getFinishListener() {
        return finishListener;
    }

    public void setFinishListener(FinishListener finishListener) {
        this.finishListener = finishListener;
    }

    public int getSameFilePointerTime() {
        return sameFilePointerTime;
    }

    public void setSameFilePointerTime(int sameFilePointerTime) {
        this.sameFilePointerTime = sameFilePointerTime;
    }

    public int getMaxSameFilePointerTime() {
        return maxSameFilePointerTime;
    }

    public void setMaxSameFilePointerTime(int maxSameFilePointerTime) {
        this.maxSameFilePointerTime = maxSameFilePointerTime;
    }

    public boolean isWaitingFinish() {
        return waitingFinish;
    }

    public void setWaitingFinish(boolean waitingFinish) {
        this.waitingFinish = waitingFinish;
    }

    public int getMaxWaitingFinishSleepTime() {
        return maxWaitingFinishSleepTime;
    }

    public void setMaxWaitingFinishSleepTime(int maxWaitingFinishSleepTime) {
        this.maxWaitingFinishSleepTime = maxWaitingFinishSleepTime;
    }

    public int getBuffLen() {
        return buffLen;
    }

    public void setBuffLen(int buffLen) {
        this.buffLen = buffLen;
    }

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    //interface
    public interface FinishListener {
        void finish();
    }

    public interface TailListener {
        void fireMoreTail(byte[] bytes, int start, int len);
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
