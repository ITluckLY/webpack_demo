package com.dcfs.esb.ftp.common.compress;

public class CompressFactory {
    protected CompressFactory() {
    }

    public static byte[] compress(byte[] src, String compressFlag) throws Exception {
        return getCompressor(compressFlag).compress(src);
    }

    public static byte[] decompress(byte[] src, String compressFlag) throws Exception {
        return getCompressor(compressFlag).decompress(src);
    }

    public static Compressor getCompressor(String compressFlag) {//NOSONAR
        return new DefaultCompressor();
    }

}
