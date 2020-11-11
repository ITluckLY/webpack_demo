package com.dc.smarteam.modules.monitor.putfiletomonitor.compress;

/**
 * 数据压缩处理类
 *
 * @author zhuliang
 */
public interface Compressor {

    /**
     * 压缩
     *
     * @param src
     * @return
     */
    byte[] compress(byte[] src) throws Exception;

    /**
     * 解压缩
     *
     * @param src
     * @return
     */
    byte[] decompress(byte[] src) throws Exception;

}
