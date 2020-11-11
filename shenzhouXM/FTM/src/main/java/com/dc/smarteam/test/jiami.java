package com.dc.smarteam.test;

import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.ScrtUtil;
import com.sun.xml.internal.rngom.binary.Pattern;

import java.util.regex.Matcher;

/**
 * Created by 65209 on 2019-12-22.
 */
public class jiami {
    public static void main(String[] args) {
        String passwd = "29718edb-f8ad-4695-a6c3-275a7f592eaa";
        String pwd = ScrtUtil.encryptEsb(passwd);
        System.out.println("加密密文是："+pwd);
        String mingwen = ScrtUtil.decryptEsb("kWZSIV5iqhhk4ljNcLwC25JB3OVyakvFLNJQU9cMgZI5XBUrMuXW6EMG6Sf1vSRAvV4GqB6Y8c4qrVb5pQ4cceIJbgLGJIUYXGVApNen35E=");
        System.out.println("解密结果是:"+mingwen);
//        Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
//        Matcher m = p.matcher(serviceurl);
    }
}
