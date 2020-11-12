package com.dcfs.esc.ftp.comm.helper;

import com.dcfs.esb.ftp.common.scrt.Md5Alg;
import com.dcfs.esc.ftp.comm.util.NumberConvertUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by mocg on 2017/6/9.
 */
public class FileMd5Helper {
    protected FileMd5Helper() {
    }

    /**
     * 生成本地文件的Md5校验码信息
     *
     * @return 返回本地文件的校验码信息
     */
    public static String getFileMd5(Md5Alg md5Alm) {
        return md5Alm.digestAndString();
    }

    public static void md5(RandomAccessFile raf, final long position, Md5Alg md5Alm) throws IOException {
        md5(raf, 0L, position, md5Alm);
    }

    public static void md5(RandomAccessFile raf, final long startPosition, final long endPosition, Md5Alg md5Alm) throws IOException {
        md5Alm.reset();
        final int bufLen = 1024 * 8;
        byte[] buf = new byte[bufLen];
        long bakpos = raf.getFilePointer();
        raf.seek(startPosition);
        long readTotal = startPosition;
        while (readTotal + bufLen < endPosition) {
            raf.read(buf);
            md5Alm.update(buf);
            readTotal += bufLen;
        }
        //最后一块
        int lastLen = NumberConvertUtil.longToInt(endPosition - readTotal);
        raf.read(buf, 0, lastLen);
        md5Alm.update(buf, 0, lastLen);
        raf.seek(bakpos);
    }
}
