package com.dcfs.esb.ftp.common.cons;

import java.nio.charset.Charset;

/**
 * Created by mocg on 2016/12/23.
 */
public class EncodingCons {
    public static final String XML_FILE_DEF_ENCODING = "UTF-8";//NOSONAR
    public static final Charset XML_FILE_DEF_CHARSET = Charset.forName(XML_FILE_DEF_ENCODING);
    public static final String PROPERTIES_FILE_DEF_ENCODING = "UTF-8";
    public static final Charset PROPERTIES_FILE_DEF_CHARSET = Charset.forName(PROPERTIES_FILE_DEF_ENCODING);
    public static final String DEF_ENCODING = "UTF-8";
    public static final Charset DEF_CHARSET = Charset.forName(DEF_ENCODING);
    //spring读取配置时使用的编码
    public static final String SPRING_READ_VALUE_ENCODING = "ISO-8859-1";
    public static final Charset SPRING_READ_VALUE_CHARSET = Charset.forName(SPRING_READ_VALUE_ENCODING);
    //cfg配置文件的编码
    public static final String CFG_CONTENT_ENCODING = "UTF-8";
    public static final Charset CFG_CONTENT_CHARSET = Charset.forName(CFG_CONTENT_ENCODING);
    public static final String CFG_CONTENT_IN_DB_ENCODING = "UTF-8";
    public static final Charset CFG_CONTENT_IN_DB_CHARSET = Charset.forName(CFG_CONTENT_IN_DB_ENCODING);

    private EncodingCons() {
    }
}
