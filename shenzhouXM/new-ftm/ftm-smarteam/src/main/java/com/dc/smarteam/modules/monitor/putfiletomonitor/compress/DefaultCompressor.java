package com.dc.smarteam.modules.monitor.putfiletomonitor.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DefaultCompressor implements Compressor {

    public byte[] compress(byte[] src) throws Exception {
        byte[] b = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(src);
        gzip.finish();
        gzip.close();
        b = baos.toByteArray();
        baos.close();
        return b;
    }

    public byte[] decompress(byte[] src) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream gzip = new GZIPInputStream(bais);
        byte buffer[] = new byte[10 * 1024];
        int i = 0;
        while ((i = gzip.read(buffer)) != -1) {
            baos.write(buffer, 0, i);
        }
        baos.flush();
        baos.close();
        gzip.close();
        bais.close();
        byte b[] = baos.toByteArray();
        return b;
    }

}
