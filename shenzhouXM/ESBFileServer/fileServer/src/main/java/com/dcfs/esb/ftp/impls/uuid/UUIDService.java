package com.dcfs.esb.ftp.impls.uuid;

import org.apache.commons.lang3.RandomUtils;

/**
 * Created by mocg on 2016/10/21.
 */
public class UUIDService {
    private static IdWorker idWorker;

    private UUIDService() {
    }

    public static long nextId() {
        if (idWorker == null) {
            final int bitLen = 10;
            return System.currentTimeMillis() << bitLen | RandomUtils.nextInt(0, 1 << bitLen);
        }
        return idWorker.nextId();
    }

    public static IdWorker getIdWorker() {
        return idWorker;
    }

    public static void setIdWorker(IdWorker idWorker) {
        UUIDService.idWorker = idWorker;
    }
}
