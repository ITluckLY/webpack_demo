package com.dcfs.esc.ftp.comm.util;

import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by mocg on 2017/6/29.
 */
public class IOUtil extends IOUtils {

    /**
     * 关闭一系列Closeable,并返回是否有异常
     *
     * @param closeables
     * @return
     */
    public static boolean closeQuietly(Closeable... closeables) {
        return closeQuietly(false, closeables);
    }

    public static boolean closeQuietly(boolean printException, Closeable... closeables) {
        if (closeables == null) return false;
        boolean haveException = false;
        for (Closeable closeable : closeables) {
            if (closeable == null) continue;
            try {
                closeable.close();
            } catch (IOException ioe) {
                haveException = true;
                if (printException) ioe.printStackTrace();//NOSONAR
            }
        }
        return haveException;
    }
}
