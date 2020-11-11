package com.dc.smarteam.modules.monitor.putfiletomonitor.compress;

public class CompressFactory {

    public static byte[] compress(byte[] src, String compressFlag) throws Exception {
        byte[] dest = getCompressor(compressFlag).compress(src);
        return dest;
    }

    public static byte[] decompress(byte[] src, String compressFlag) throws Exception {
        byte[] dest = getCompressor(compressFlag).decompress(src);
        return dest;
    }

    public static Compressor getCompressor(String compressFlag) {
        return new DefaultCompressor();
    }

}
