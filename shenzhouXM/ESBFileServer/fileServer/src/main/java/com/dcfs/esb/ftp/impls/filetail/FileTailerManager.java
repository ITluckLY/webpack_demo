package com.dcfs.esb.ftp.impls.filetail;

import java.io.File;
import java.io.IOException;

/**
 * Created by mocg on 2017/6/7.
 */
public class FileTailerManager {

    private FileTailer tailer;
    private Thread tailerThread;

    public FileTailerManager(FileTailer tailer) {
        this.tailer = tailer;
    }

    public void start() {
        tailerThread = new Thread(tailer);
        tailerThread.start();
    }

    public void stopTailingAndWait() {
        tailer.stopTailingAndWait();
    }

    public void stopTailing(boolean byErr) {
        tailer.stopTailing();
        tailer.setHaveErr(byErr);
    }

    public void stopTailing4ReplaceFile() throws InterruptedException {
        tailer.stopTailing4ReplaceFile();
        //等待tail线程结束，释放文件锁
        if (tailerThread != null) tailerThread.join();
    }

    public void reopenFile(File file) throws IOException, InterruptedException {
        if (tailer.isTailFinish()) return;
        stopTailing4ReplaceFile();
        tailer.reopenFile(file);
        tailer.setTailing(true);
        tailerThread = new Thread(tailer);
        tailerThread.start();
    }

    public void setWaitingFinish(boolean waitingFinish) {
        tailer.setWaitingFinish(waitingFinish);
    }
}
