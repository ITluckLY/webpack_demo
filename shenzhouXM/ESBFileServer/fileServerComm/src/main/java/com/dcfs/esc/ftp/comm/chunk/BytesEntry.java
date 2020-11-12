package com.dcfs.esc.ftp.comm.chunk;

/**
 * Created by mocg on 2017/8/29.
 */
public class BytesEntry {

    private byte[] data;
    private int offset;
    private int len;

    public BytesEntry(byte[] data) {
        this.data = data;
        this.offset = 0;
        this.len = data == null ? 0 : data.length;
    }

    public BytesEntry(byte[] data, int offset, int len) {
        this.data = data;
        this.offset = offset;
        this.len = len;
    }

    public void clean() {
        data = null;
        len = 0;
    }

    //getset

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
