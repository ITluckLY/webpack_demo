package com.dcfs.esb.ftp.cons;

/**
 * Created by mocg on 2016/8/31.
 */
public enum NodeState {
    STOP(0), RUNNING(1);

    private final int num;

    NodeState(int num) {
        this.num = num;
    }

    public final int num() {
        return num;
    }
}
