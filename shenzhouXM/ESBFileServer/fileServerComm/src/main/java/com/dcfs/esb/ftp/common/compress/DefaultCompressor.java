package com.dcfs.esb.ftp.common.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DefaultCompressor implements Compressor {

    public byte[] compress(byte[] src) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(src);
        gzip.finish();
        gzip.close();
        return baos.toByteArray();
    }

    public byte[] decompress(byte[] src) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream gzip = new GZIPInputStream(bais);
        final int bufLen = 10 * 1024;
        byte[] buffer = new byte[bufLen];
        int i;
        while ((i = gzip.read(buffer)) != -1) {
            baos.write(buffer, 0, i);
        }
        baos.flush();
        baos.close();
        gzip.close();
        bais.close();
        return baos.toByteArray();
    }

}
