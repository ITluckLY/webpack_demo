package com.dc.smarteam.uuid;

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
            return System.currentTimeMillis() << 10 | RandomUtils.nextLong(0, 1 << 10);
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
