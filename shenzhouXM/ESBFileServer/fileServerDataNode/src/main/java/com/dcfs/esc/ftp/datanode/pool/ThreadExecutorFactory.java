package com.dcfs.esc.ftp.datanode.pool;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by mocg on 2017/8/21.
 */
public class ThreadExecutorFactory {

    private ThreadExecutorFactory() {
    }

    //chunkDispatchExecutor
    private static final int DEF_CHUNK_DISPATCH_EXECUTOR_COUNT = 64;
    private static int chunkDispatchExecutorCount = DEF_CHUNK_DISPATCH_EXECUTOR_COUNT;
    private static volatile EventExecutorGroup eventExecutorsForChunkDispatch;
    //fileRouteExecutor
    private static final int DEF_FILE_ROUTE_EXECUTOR_COUNT = 32;
    private static int fileRouteExecutorCount = DEF_FILE_ROUTE_EXECUTOR_COUNT;
    private static volatile Executor fileRouteExecutor;
    //fileBackupExecutor
    private static final int DEF_FILE_BACKUP_EXECUTOR_COUNT = 32;
    private static int fileBackupExecutorCount = DEF_FILE_BACKUP_EXECUTOR_COUNT;
    private static volatile Executor fileBackupExecutor;

    private static final Object lock = new Object();

    public static EventExecutorGroup getEventExecutorGroupForChunkDispatch() {
        if (eventExecutorsForChunkDispatch != null) return eventExecutorsForChunkDispatch;
        synchronized (lock) {
            eventExecutorsForChunkDispatch = new DefaultEventExecutorGroup(chunkDispatchExecutorCount);
        }
        return eventExecutorsForChunkDispatch;
    }

    public static Executor getExecutorForFileRoute() {
        if (fileRouteExecutor != null) return fileRouteExecutor;
        synchronized (lock) {
            fileRouteExecutor = Executors.newFixedThreadPool(fileRouteExecutorCount, new ThreadFactory() {
                String poolName = "pool-Route";
                ThreadGroup tg = new ThreadGroup(poolName);
                int count = 0;

                public Thread newThread(Runnable r) {
                    return new Thread(tg, r, poolName + count++);
                }
            });
        }
        return fileRouteExecutor;
    }

    public static Executor getExecutorForFileBackup() {
        if (fileBackupExecutor != null) return fileBackupExecutor;
        synchronized (lock) {
            fileBackupExecutor = Executors.newFixedThreadPool(fileBackupExecutorCount, new ThreadFactory() {
                String poolName = "pool-Backup";
                ThreadGroup tg = new ThreadGroup(poolName);
                int count = 0;

                public Thread newThread(Runnable r) {
                    return new Thread(tg, r, poolName + count++);
                }
            });
        }
        return fileBackupExecutor;
    }


    //getset

    public static int getChunkDispatchExecutorCount() {
        return chunkDispatchExecutorCount;
    }

    public static void setChunkDispatchExecutorCount(int chunkDispatchExecutorCount) {
        ThreadExecutorFactory.chunkDispatchExecutorCount = chunkDispatchExecutorCount;
    }

    public static int getFileRouteExecutorCount() {
        return fileRouteExecutorCount;
    }

    public static void setFileRouteExecutorCount(int fileRouteExecutorCount) {
        ThreadExecutorFactory.fileRouteExecutorCount = fileRouteExecutorCount;
    }

    public static int getFileBackupExecutorCount() {
        return fileBackupExecutorCount;
    }

    public static void setFileBackupExecutorCount(int fileBackupExecutorCount) {
        ThreadExecutorFactory.fileBackupExecutorCount = fileBackupExecutorCount;
    }
}
