package com.dcfs.esc.ftp.comm.helper;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by mocg on 2017/9/19.
 */
public class DtoSeqHelper {
    private DtoSeqHelper() {
    }

    /**
     * 随机生成长度为128的字符串，只包括字母和数字
     *
     * @return
     */
    public static String generateInitDtoSeq() {
        final int deflen = 128;
        return generateSeq(deflen);
    }

    /**
     * 随机生成字符串，只包括字母和数字
     *
     * @param length
     * @return
     */
    public static String generateSeq(int length) {
        return RandomStringUtils.random(length, true, true);
    }
}
