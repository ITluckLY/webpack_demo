package com.dcfs.esb.ftp.common.cons;

/**
 * Created by mocg on 2016/7/22.
 */
public enum NodeType {
    //add CLNODE 20180330
    NAMENODE(0), DATANODE(1), LOGNODE(2), MONITOR(3), UNDEFINED(4),CLNODE(5);

    private final int num;

    NodeType(int num) {
        this.num = num;
    }

    public final int num() {
        return num;
    }
}
