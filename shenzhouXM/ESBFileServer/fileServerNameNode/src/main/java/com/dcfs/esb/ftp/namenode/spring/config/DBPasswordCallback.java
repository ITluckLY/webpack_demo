package com.dcfs.esb.ftp.namenode.spring.config;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.DruidPasswordCallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 数据库密码回调解密
 */
@Component
@SuppressWarnings("serial")
public class DBPasswordCallback extends DruidPasswordCallback {

    @Value("${jdbc.password}")
    private String propJdbcPwd;

    //公钥
    private static final String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCX4sGt9aBSepZieJpJbNmWuyIYDj" +
            "E9A/AtAli6IrMSfkqriXr0endKM8qXjQ/8xnBk/R98CI+6ZLS4Xh/P9hIQdzA9Cmd0t6AATLn7xIRBgS1vW9y4pvlVWhk9ZJF+K7G/yH7c" +
            "lVkFdVO7HFgiYBIaVPlC7TA/uB3r2cOwLa/3KQIDAQAB";

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        if (StringUtils.isNotBlank(propJdbcPwd)) {
            try {
                //这里的password是将jdbc.properties配置得到的密码进行解密之后的值
                String password = ConfigTools.decrypt(PUBLIC_KEY_STRING, propJdbcPwd);
                setPassword(password.toCharArray());
            } catch (Exception e) {
                setPassword(propJdbcPwd.toCharArray());
            }
        }
    }
}
